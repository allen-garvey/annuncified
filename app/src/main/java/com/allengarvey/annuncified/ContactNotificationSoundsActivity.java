package com.allengarvey.annuncified;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import java.util.Arrays;
import java.util.HashSet;


public class ContactNotificationSoundsActivity extends CustomNotificationSoundsActivity{
    private static final Integer[] typesOfPhoneNumbersToDisplayInList = {ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                                                                    ContactsContract.CommonDataKinds.Phone.TYPE_MMS,
                                                                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE};
    private static final HashSet<Integer> phoneNumberTypesSet = new HashSet<>(Arrays.asList(typesOfPhoneNumbersToDisplayInList));

    protected void populateLists(){
        initLists();
        final String defaultNotificationSoundKey = getString(R.string.default_contact_notification_sound_key);
        HashSet<String> contactIDSet = new HashSet<>();
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
                    itemSoundIsDefault.add(true);
                }
                else{
                    notificationName = NotifyUtil.ringtoneNameFromUri(this, NotifyUtil.uriFromPath(path));
                    itemSoundIsDefault.add(false);
                }
                itemNames.add(name);
                itemDisplayNames.add(getFormattedListItemText(name, notificationName));
                itemIDs.add(contactID);
            }
        }
        phones.close();
    }

    protected String notificationSoundPathFromItemID(Context context, String contactID){
        return NotifyUtil.notificationSoundPathFromContactsID(this, contactID);
    }


    public void setNotificationSoundPathForItemId(Context context, String contactID, String uriPath){
        NotifyUtil.setNotificationSoundPathForContact(this, contactID, uriPath);
    }


}
