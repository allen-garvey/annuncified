package com.allengarvey.annuncified;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Allen X on 5/15/15.
 */
public class SMSReceiver extends BroadcastReceiver{

    private SharedPreferences preferences;


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from = "";
            String contactName = "";
            String contactID = "";
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();

                        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(msg_from));
                        Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);

                        c.moveToFirst();
                        contactName = c.getString(c.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                        contactID = c.getString(c.getColumnIndex(ContactsContract.PhoneLookup._ID));
                    }

                }catch(Exception e){
                    //sms message from non-contact will cause exception
                    Log.d("Exception caught", e.getMessage());

                }
            }
            Log.e("Message sent from: ", msg_from + " " + contactName);
            Log.e("SMS Receiver contact id: ", contactID);

            playRingtone(context, contactID);

        }

    }

    private void playRingtone(Context context, String contactID){
        String ringtonePath = NotifyUtil.notificationSoundPathFromContactsID(context, contactID);
        Uri ringtoneUri;
        if(ringtonePath.equals(context.getString(R.string.silent_ringtone_key))){
            return;
        }
        if(ringtonePath.equals(NotifyUtil.NOT_FOUND) || ringtonePath.equals(context.getString(R.string.default_contact_notification_sound_key))){
            ringtoneUri = NotifyUtil.getDefaultNotificationSound(context);
        }
        else {
            ringtoneUri = NotifyUtil.uriFromPath(ringtonePath);
        }

        Ringtone notificationTone = RingtoneManager.getRingtone(context, ringtoneUri);
        notificationTone.play();
    }


}
