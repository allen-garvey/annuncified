package com.allengarvey.annuncified;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import java.util.HashSet;


public class GroupNotificationSoundsActivity extends CustomNotificationSoundsActivity{

    @Override
    protected void populateLists(){
        HashSet<String> groupIDSet = new HashSet<>();

        final String defaultSoundKey = getString(R.string.default_contact_notification_sound_key);

        Cursor groups = NotifyUtil.getGroupsDataCursor(this);
        while (groups.moveToNext()){
            String name = groups.getString(groups.getColumnIndex(ContactsContract.Groups.TITLE));
            String groupID = groups.getString(groups.getColumnIndex(ContactsContract.Groups._ID));

            if(!groupIDSet.contains(groupID)){
                groupIDSet.add(groupID);
                String notificationName;
                String path = notificationSoundPathFromItemID(this, groupID);
                if(path.equals(NotifyUtil.NOT_FOUND) || path.equals(defaultSoundKey)){
                    notificationName = defaultSoundText();
                    itemSoundIsDefault.add(true);
                }
                else{
                    notificationName = NotifyUtil.ringtoneNameFromUri(this, NotifyUtil.uriFromPath(path));
                    itemSoundIsDefault.add(false);
                }
                itemNames.add(name);
                itemDisplayNames.add(getFormattedListItemText(name, notificationName));
                itemIDs.add(groupID);
            }
        }
        groups.close();
    }

    @Override
    protected String notificationSoundPathFromItemID(Context context, String itemID){
        return NotifyUtil.notificationSoundPathFromGroupID(this, itemID);
    }

    @Override
    public void setNotificationSoundPathForItemId(Context context, String groupID, String uriPath){
        NotifyUtil.setNotificationSoundPathForGroup(this, groupID, uriPath);
    }




}