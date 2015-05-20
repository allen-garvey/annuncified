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


public class NotifyUtil{

    public static final int receiverDefaultState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    public static final String contactInfoSharedPreferencesName = "Annuncified contact info notification sounds shared preferences name";
    public static final String NOT_FOUND = "This is the string you receive if for some reason the value is not stored in shared preferences.";


    //receiverState is PackageManager.COMPONENT_ENABLED_STATE_ENABLED
    //or PackageManager.COMPONENT_ENABLED_STATE_DISABLED
    public static void setSMSReceiverState(Context app, int receiverState){
        ComponentName receiver = new ComponentName(app, SMSReceiver.class);
        PackageManager pm = app.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                receiverState,
                PackageManager.DONT_KILL_APP);
    }

    public static boolean getIgnoreTextsFromNonContactsSetting(Context app){
        return NotifyUtil.getSharedPreferences(app).getBoolean(app.getString(R.string.ignore_texts_from_non_contacts_key),false);
    }
    public static void setIgnoreTextsFromNonContactsSetting(Context app, boolean newSetting){
        NotifyUtil.getSharedPreferences(app).edit().putBoolean(app.getString(R.string.ignore_texts_from_non_contacts_key), newSetting).apply();
    }
    public static SharedPreferences getSharedPreferences(Context app){
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    public static SharedPreferences getContactInfoSharedPreferences(Context app){
        return app.getSharedPreferences(contactInfoSharedPreferencesName, Context.MODE_PRIVATE);
    }

    public static int getSMSReceiverStatePreferences(Context app){
        return getSharedPreferences(app).getInt(app.getString(R.string.receiver_state_shared_preferences_key), receiverDefaultState);
    }

    public static void setSMSReceiverStatePreferences(Context app, int newReceiverState){
        getSharedPreferences(app).edit().putInt(app.getString(R.string.receiver_state_shared_preferences_key), newReceiverState).apply();
    }


    public static Uri getDefaultNotificationSound(Context app){
        String NOT_FOUND = "URI not in shared preferences";
        String defaultURIPath = getSharedPreferences(app).getString(app.getString(R.string.default_notification_sound_uri_shared_preferences_key), NOT_FOUND);
        if(!defaultURIPath.equals(NOT_FOUND)){
            return uriFromPath(defaultURIPath);
        }

        return Settings.System.DEFAULT_NOTIFICATION_URI;
    }

    public static String ringtoneNameFromUri(Context app, Uri ringtoneUri){
        Ringtone ringtone = RingtoneManager.getRingtone(app, ringtoneUri);
        return ringtone.getTitle(app);
    }

    public static Uri uriFromPath(String path){
        return Uri.parse(path);
    }

    public static String notificationSoundPathFromContactsID(Context app, String contactID){
        return getContactInfoSharedPreferences(app).getString(contactID, NOT_FOUND);
    }

    public static void setNotificationSoundPathForContact(Context app, String contactID, String path){
        getContactInfoSharedPreferences(app).edit().putString(contactID, path).apply();
    }



}