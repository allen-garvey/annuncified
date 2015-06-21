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
import java.util.HashSet;


public class GroupNotificationSoundsActivity extends ListActivity{
    private String[] groupList;
    private Boolean[] groupIsDefaultSound;
    private ArrayList<String> groupNames;
    private ArrayList<String> groupDisplayNames;
    private ArrayList<String> groupIDs;
    private ArrayList<Boolean> groupSoundIsDefault;
    private ContactArrayAdapter arrayAdapter;
    private String defaultSoundText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        defaultSoundText = getString(R.string.contact_notification_sound_not_set_text);
        initLists();
        groupList = groupDisplayNames.toArray(new String[groupNames.size()]);
        groupIsDefaultSound = groupSoundIsDefault.toArray(new Boolean[groupSoundIsDefault.size()]);
        arrayAdapter = new ContactArrayAdapter(this, R.layout.narrow_list_layout, R.id.list_item, groupList, groupIsDefaultSound);
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
        groupNames = new ArrayList<>();
        groupIDs = new ArrayList<>();
        HashSet<String> groupIDSet = new HashSet<>();
        groupDisplayNames = new ArrayList<>();
        groupSoundIsDefault = new ArrayList<>();
        final String defaultSoundKey = getString(R.string.default_contact_notification_sound_key);
        //confusingly 0 means visible for group_visible
        Cursor groups = NotifyUtil.getGroupsDataCursor(this);
        while (groups.moveToNext()){
            String name = groups.getString(groups.getColumnIndex(ContactsContract.Groups.TITLE));
            String groupID = groups.getString(groups.getColumnIndex(ContactsContract.Groups._ID));


            if(!groupIDSet.contains(groupID)){
                groupIDSet.add(groupID);
                String notificationName;
                String path = NotifyUtil.notificationSoundPathFromGroupID(this, groupID);
                if(path.equals(NotifyUtil.NOT_FOUND) || path.equals(defaultSoundKey)){
                    notificationName = defaultSoundText;
                    groupSoundIsDefault.add(true);
                }
                else{
                    notificationName = NotifyUtil.ringtoneNameFromUri(this, NotifyUtil.uriFromPath(path));
                    groupSoundIsDefault.add(false);
                }
                groupNames.add(name);
                groupDisplayNames.add(getFormattedListItemText(name, notificationName));
                groupIDs.add(groupID);
            }
        }
        groups.close();
    }



    public void setNotificationSound(int position){
        Uri ringtoneUri = null;
        String ringtonePath = NotifyUtil.notificationSoundPathFromGroupID(this, groupIDs.get(position));
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
            String groupID = groupIDs.get(requestCode);
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
            NotifyUtil.setNotificationSoundPathForGroup(this, groupID, uriPath);

            //no need to call adapter.dataSetChanged() because onResume is called automatically
        }
    }


    private String getFormattedListItemText(String groupName, String notificationName){
        return groupName + ": " + notificationName;
    }


}