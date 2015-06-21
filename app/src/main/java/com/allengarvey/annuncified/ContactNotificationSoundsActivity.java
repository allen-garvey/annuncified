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
import java.util.Arrays;
import java.util.HashSet;


public class ContactNotificationSoundsActivity extends ListActivity{
    private String[] contactList;
    private Boolean[] contactIsDefaultSound;
    private ArrayList<String> contactNames;
    private ArrayList<String> contactDisplayNames;
    private ArrayList<String> contactIDs;
    private ArrayList<Boolean> contactSoundIsDefault;
    private ContactArrayAdapter arrayAdapter;
    private static final Integer[] typesOfPhoneNumbersToDisplayInList = {ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                                                                    ContactsContract.CommonDataKinds.Phone.TYPE_MMS,
                                                                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE};
    private static final HashSet<Integer> phoneNumberTypesSet = new HashSet<>(Arrays.asList(typesOfPhoneNumbersToDisplayInList));
    private String defaultNotificationSoundKey;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        defaultNotificationSoundKey = getString(R.string.default_contact_notification_sound_key);
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
        HashSet<String> contactIDSet = new HashSet<>();
        contactDisplayNames = new ArrayList<>();
        contactSoundIsDefault = new ArrayList<>();
        final String contactSoundNotSetText = getString(R.string.contact_notification_sound_not_set_text);
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext()){

            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String contactID = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            int phoneNumberType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

            if(phoneNumberTypesSet.contains(phoneNumberType) && !contactIDSet.contains(contactID)){
                contactIDSet.add(contactID);
                String notificationName;
                String path = NotifyUtil.notificationSoundPathFromContactsID(this, contactID);
                if(path.equals(NotifyUtil.NOT_FOUND) || path.equals(defaultNotificationSoundKey)){
                    notificationName = contactSoundNotSetText;
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
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.custom_notification_tone_modal_text));
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
                uriPath = defaultNotificationSoundKey;
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


}
