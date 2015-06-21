package com.allengarvey.annuncified;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.HashSet;


public class NotifyUtil{
    //////////////////////////
    // constants
    //////////////////////////
    public static final int receiverDefaultState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    public static final int callReceiverDefaultState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
    public static final String contactNotificationsInfoSharedPreferencesName = "Annuncified contact notifications info shared preferences name";
    public static final String groupNotificationsInfoSharedPreferencesName = "Annuncified group notifications info shared preferences name";
    public static final String NOT_FOUND = "This is the string you receive if for some reason the value is not stored in shared preferences.";


    ////////////////////////////////////////////////////////////
    // get shared preferences object helper methods
    ////////////////////////////////////////////////////////////

    public static SharedPreferences getSharedPreferences(Context app){
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    public static SharedPreferences getContactNotificationsInfoSharedPreferences(Context app){
        return app.getSharedPreferences(contactNotificationsInfoSharedPreferencesName, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getGroupNotificationsInfoSharedPreferences(Context app){
        return app.getSharedPreferences(groupNotificationsInfoSharedPreferencesName, Context.MODE_PRIVATE);
    }


    ////////////////////////////////////////////////////////////
    // initialize app broadcast receivers based on settings
    ////////////////////////////////////////////////////////////

    public static void startBroadcastReceiversBasedOnSettings(Context app){
        NotifyUtil.setSMSReceiverState(app, NotifyUtil.getSMSReceiverStatePreferences(app));
        NotifyUtil.setCallReceiverState(app, NotifyUtil.getCallReceiverStatePreferences(app));
    }


    ////////////////////////////////////////////////////////////
    // get and set methods for stored shared preferences values
    ////////////////////////////////////////////////////////////

    //receiverState is PackageManager.COMPONENT_ENABLED_STATE_ENABLED
    //or PackageManager.COMPONENT_ENABLED_STATE_DISABLED
    public static void setSMSReceiverState(Context app, int receiverState){
        ComponentName receiver = new ComponentName(app, SMSReceiver.class);
        PackageManager pm = app.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                receiverState,
                PackageManager.DONT_KILL_APP);
    }

    public static void setCallReceiverState(Context app, int receiverState){
        ComponentName receiver = new ComponentName(app, CallReceiver.class);
        PackageManager pm = app.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                receiverState,
                PackageManager.DONT_KILL_APP);
    }

    public static boolean getIgnoreCallsFromNonContactsSetting(Context app){
        return NotifyUtil.getSharedPreferences(app).getBoolean(app.getString(R.string.ignore_calls_from_non_contacts_key),false);
    }

    public static void setIgnoreCallsFromNonContactsSetting(Context app, boolean newSetting){
        NotifyUtil.getSharedPreferences(app).edit().putBoolean(app.getString(R.string.ignore_calls_from_non_contacts_key), newSetting).apply();
    }

    public static boolean getIgnoreTextsFromNonContactsSetting(Context app){
        return NotifyUtil.getSharedPreferences(app).getBoolean(app.getString(R.string.ignore_texts_from_non_contacts_key),false);
    }
    public static void setIgnoreTextsFromNonContactsSetting(Context app, boolean newSetting){
        NotifyUtil.getSharedPreferences(app).edit().putBoolean(app.getString(R.string.ignore_texts_from_non_contacts_key), newSetting).apply();
    }
    public static boolean getStartAppOnBootSetting(Context app){
        return NotifyUtil.getSharedPreferences(app).getBoolean(app.getString(R.string.boot_receiver_key),false);
    }
    public static void setStartAppOnBootSetting(Context app, boolean newSetting){
        NotifyUtil.getSharedPreferences(app).edit().putBoolean(app.getString(R.string.boot_receiver_key), newSetting).apply();
    }

    public static int getSMSReceiverStatePreferences(Context app){
        return getSharedPreferences(app).getInt(app.getString(R.string.receiver_state_shared_preferences_key), receiverDefaultState);
    }

    public static void setSMSReceiverStatePreferences(Context app, int newReceiverState){
        getSharedPreferences(app).edit().putInt(app.getString(R.string.receiver_state_shared_preferences_key), newReceiverState).apply();
    }

    public static int getCallReceiverStatePreferences(Context app){
        return getSharedPreferences(app).getInt(app.getString(R.string.call_receiver_key), callReceiverDefaultState);
    }

    public static void setCallReceiverStatePreferences(Context app, int newReceiverState){
        getSharedPreferences(app).edit().putInt(app.getString(R.string.call_receiver_key), newReceiverState).apply();
    }


    ////////////////////////////////////////////////////////////
    // Get Ringtone Uris and ringtone name string helper methods
    /////////////////////////////////////////////////////////////

    public static Uri getDefaultNotificationSound(Context app){
        String defaultURIPath = getSharedPreferences(app).getString(app.getString(R.string.default_notification_sound_uri_shared_preferences_key), NOT_FOUND);
        if(!defaultURIPath.equals(NOT_FOUND)){
            return uriFromPath(defaultURIPath);
        }

        return Settings.System.DEFAULT_NOTIFICATION_URI;
    }

    public static void resetOriginalDefaultRingtonePath(Context app){
        Uri originalUri = NotifyUtil.uriFromPath(getSharedPreferences(app).getString(app.getString(R.string.old_default_ringtone_uri_shared_preferences_key), Settings.System.DEFAULT_RINGTONE_URI.toString()));
        RingtoneManager.setActualDefaultRingtoneUri(app, RingtoneManager.TYPE_RINGTONE, originalUri);

    }
    public static void saveOriginalDefaultRingtonePath(Context app){
        String currentUriPath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).toString();
        if(currentUriPath != null){
            getSharedPreferences(app).edit().putString(app.getString(R.string.old_default_ringtone_uri_shared_preferences_key), currentUriPath).apply();
        }
    }


    public static String ringtoneNameFromUri(Context app, Uri ringtoneUri){
        Ringtone ringtone = RingtoneManager.getRingtone(app, ringtoneUri);
        return ringtone.getTitle(app);
    }

    public static Uri uriFromPath(String path){
        return Uri.parse(path);
    }

    public static String notificationSoundPathFromContactsID(Context app, String contactID){
        return getContactNotificationsInfoSharedPreferences(app).getString(contactID, NOT_FOUND);
    }

    public static void setNotificationSoundPathForContact(Context app, String contactID, String path){
        getContactNotificationsInfoSharedPreferences(app).edit().putString(contactID, path).apply();
    }

    public static String notificationSoundPathFromGroupID(Context app, String groupID){
        return getGroupNotificationsInfoSharedPreferences(app).getString(groupID, NOT_FOUND);
    }

    public static void setNotificationSoundPathForGroup(Context app, String groupID, String path){
        getGroupNotificationsInfoSharedPreferences(app).edit().putString(groupID, path).apply();
    }

    public static String ringtoneSoundPathFromContactsID(Context app, String contactID){
        Cursor c = app.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[] {ContactsContract.Data.CUSTOM_RINGTONE},
                ContactsContract.Data.CONTACT_ID + "=?",
                new String[] {contactID}, null);
        if(c.moveToFirst()){
            return c.getString(c.getColumnIndex(ContactsContract.Data.CUSTOM_RINGTONE));
        }
        return NOT_FOUND;
    }

    public static String getContactIdFromPhoneNumber(Context context, String number) {
        String contactId = NOT_FOUND;

        // define the columns I want the query to return
        String[] projection = new String[] {
                ContactsContract.PhoneLookup._ID};

        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

        if (cursor.moveToFirst()) {
            contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
        }
        cursor.close();
        return contactId;
    }

    public static ArrayList<String> getGroupIdsFromContactId(Context app, String contactId){
        ArrayList<String> groupList = new ArrayList<>();
        HashSet<String> visibleGroupIds = new HashSet<>();
        Cursor groups = NotifyUtil.getGroupsDataCursor(app);
        while(groups.moveToNext()){
            String groupID = groups.getString(groups.getColumnIndex(ContactsContract.Groups._ID));
            visibleGroupIds.add(groupID);
        }

        Cursor c = app.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{
                        ContactsContract.Data.CONTACT_ID,
                        ContactsContract.Data.DATA1
                },
                ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.Data.CONTACT_ID + " = " + contactId,
                new String[]{ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE}, null);
        while (c.moveToNext()){
            String groupId = c.getString(c.getColumnIndex(ContactsContract.Data.DATA1));

            if(visibleGroupIds.contains(groupId)){
                groupList.add(groupId);
            }
        }
        c.close();

        return groupList;
    }

    ///////////////////////////////////////////////////////////////
    //Cursors
    //////////////////////////////////////////////////////////////

    public static Cursor getGroupsDataCursor(Context app){
        //confusingly 0 means that the group is visible
        return app.getContentResolver().query(ContactsContract.Groups.CONTENT_URI, null,ContactsContract.Groups.GROUP_VISIBLE + " = 0",null, ContactsContract.Groups.TITLE + " ASC");
    }


}