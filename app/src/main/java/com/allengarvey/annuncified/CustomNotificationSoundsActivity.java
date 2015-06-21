package com.allengarvey.annuncified;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;


public abstract class CustomNotificationSoundsActivity extends ListActivity{
    protected String[] itemList;
    protected Boolean[] itemUsesDefaultSound;
    protected ArrayList<String> itemNames;
    protected ArrayList<String> itemDisplayNames;
    protected ArrayList<String> itemIDs;
    protected ArrayList<Boolean> itemSoundIsDefault;
    protected ContactArrayAdapter arrayAdapter;
    protected String defaultSoundText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        defaultSoundText = getString(R.string.contact_notification_sound_not_set_text);
        populateLists();
        itemList = itemDisplayNames.toArray(new String[itemNames.size()]);
        itemUsesDefaultSound = itemSoundIsDefault.toArray(new Boolean[itemSoundIsDefault.size()]);
        arrayAdapter = new ContactArrayAdapter(this, R.layout.narrow_list_layout, R.id.list_item, itemList, itemUsesDefaultSound);
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

    protected void initLists(){
        itemNames = new ArrayList<>();
        itemIDs = new ArrayList<>();
        itemDisplayNames = new ArrayList<>();
        itemSoundIsDefault = new ArrayList<>();
    }

    protected abstract void populateLists(); //initLists(); should be first line of this method

    protected abstract String notificationSoundPathFromItemID(Context context, String itemID);

    public void setNotificationSound(int position){
        Uri ringtoneUri = null;
        String ringtonePath = notificationSoundPathFromItemID(this, itemIDs.get(position));
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
            String itemId = itemIDs.get(requestCode);
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
            setNotificationSoundPathForItemId(this, itemId, uriPath);

            //no need to call adapter.dataSetChanged() because onResume is called automatically
        }
    }

    public abstract void setNotificationSoundPathForItemId(Context context, String itemId, String uriPath);


    public String getFormattedListItemText(String itemName, String notificationName){
        return itemName + ": " + notificationName;
    }


}
