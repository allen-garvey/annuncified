package com.allengarvey.annuncified;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;


public class ContactNotificationSoundsActivity extends ListActivity{
    private String[] contactList;
    private Boolean[] contactIsDefaultSound;
    private ArrayList<String> contactNames;
    private ArrayList<String> contactDisplayNames;
    private ArrayList<String> contactIDs;
    private ArrayList<Boolean> contactSoundIsDefault;
    private ContactArrayAdapter arrayAdapter;
    private static final int[] typesOfPhoneNumbersToDisplayInList = {ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                                                                    ContactsContract.CommonDataKinds.Phone.TYPE_MMS,
                                                                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE};
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        initLists();
        contactList = contactDisplayNames.toArray(new String[contactNames.size()]);
        contactIsDefaultSound = contactSoundIsDefault.toArray(new Boolean[contactSoundIsDefault.size()]);
        arrayAdapter = new ContactArrayAdapter(this, R.layout.narrow_list_layout, R.id.list_item, contactList, contactIsDefaultSound);
        setListAdapter(arrayAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int postion, long id){
        setNotificationSound(postion);

    }

    private void initLists(){
        contactNames = new ArrayList<>();
        contactIDs = new ArrayList<>();
        contactDisplayNames = new ArrayList<>();
        contactSoundIsDefault = new ArrayList<>();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext()){

            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String contactID = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            int phoneNumberType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

            if(isInArray(phoneNumberType, typesOfPhoneNumbersToDisplayInList)){
                String notificationName;
                String path = NotifyUtil.notificationSoundPathFromContactsID(this, contactID);
                if(path.equals(NotifyUtil.NOT_FOUND) || path.equals(getString(R.string.default_contact_notification_sound_key))){
                    notificationName = getString(R.string.contact_notification_sound_not_set_text);
                    contactSoundIsDefault.add(true);
                }
                else{
                    notificationName = NotifyUtil.ringtoneNameFromUri(this, NotifyUtil.uriFromPath(path));
                    contactSoundIsDefault.add(false);
                }
                contactNames.add(name);
                contactDisplayNames.add(getFormattedListItemText(name, notificationName));
                contactIDs.add(contactID);
            }
        }
        phones.close();
    }



    public void setNotificationSound(int position){
        Uri ringtoneUri = null;
        String ringtonePath = NotifyUtil.notificationSoundPathFromContactsID(this, contactIDs.get(position));
        if(!ringtonePath.equals(NotifyUtil.NOT_FOUND) || !ringtonePath.equals(getString(R.string.silent_ringtone_key))){
            ringtoneUri = NotifyUtil.uriFromPath(ringtonePath);
        }

        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri);
        this.startActivityForResult(intent, position);

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent){
        if (resultCode == Activity.RESULT_OK){

            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            String contactID = contactIDs.get(requestCode);
            String uriPath;


            if(RingtoneManager.isDefault(uri)){
                uriPath = getString(R.string.default_contact_notification_sound_key);
            }
            else if (uri != null){
                uriPath = uri.toString();
            }

            else{
                uriPath = getString(R.string.silent_ringtone_key);
            }
            NotifyUtil.setNotificationSoundPathForContact(this, contactID, uriPath);

            //no need to call adapter.dataSetChanged() because onResume is called automatically
        }
    }


    private String getFormattedListItemText(String contactName, String notificationName){
        return contactName + ": " + notificationName;
    }

    private boolean isInArray(int needle, int[] haystack){
        for(int i : haystack){
            if(i == needle){
                return true;
            }
        }
        return false;
    }

}
