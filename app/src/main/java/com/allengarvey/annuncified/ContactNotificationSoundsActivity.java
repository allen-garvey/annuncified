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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Allen X on 5/15/15.
 */
public class ContactNotificationSoundsActivity extends ListActivity{
    private String[] contactList;
    private ArrayList<String> contactNames;
    private ArrayList<String> contactDisplayNames;
    private ArrayList<String> contactIDs;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initLists();
        contactList = contactDisplayNames.toArray(new String[contactNames.size()]);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, contactList);
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
        int id = item.getItemId();

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
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext())
        {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String contactID = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String notificationName;
            String path = NotifyUtil.notificationSoundPathFromContactsID(this, contactID);
            if(path.equals(NotifyUtil.NOT_FOUND)){
                notificationName = getString(R.string.contact_notification_sound_not_set_text);

            }
            else if(path.equals(getString(R.string.default_contact_notification_sound_key))){
                notificationName = getString(R.string.contact_notification_sound_not_set_text);
            }
            else {
                notificationName = NotifyUtil.ringtoneNameFromUri(this, NotifyUtil.uriFromPath(path));
            }
            contactNames.add(name);
            contactDisplayNames.add(getFormattedListItemText(name, notificationName));
            contactIDs.add(contactID);
        }
        phones.close();
    }



    public void setNotificationSound(int position){
        Uri ringtoneUri = (Uri) null;
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
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            String contactID = contactIDs.get(requestCode);
            String uriPath;
            String ringtoneName;


            if(RingtoneManager.isDefault(uri)){
                uriPath = getString(R.string.default_contact_notification_sound_key);
                ringtoneName = getString(R.string.contact_notification_sound_not_set_text);
            }
            else if (uri != null){
                uriPath = uri.toString();
                ringtoneName = NotifyUtil.ringtoneNameFromUri(this, uri);
            }

            else{
                uriPath = getString(R.string.silent_ringtone_key);
                ringtoneName = getString(R.string.silent_ringtone_text);

            }
            NotifyUtil.setNotificationSoundPathForContact(this, contactID, uriPath);
            contactList[requestCode] = getFormattedListItemText(contactNames.get(requestCode), ringtoneName);
            arrayAdapter.notifyDataSetChanged();
        }
    }


    private String getFormattedListItemText(String contactName, String notificationName){
        return contactName + ": " + notificationName;
    }

}
