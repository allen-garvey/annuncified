package com.allengarvey.annuncified;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.ArrayList;

public class SMSReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs;
            String msg_from;
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
                        contactID = c.getString(c.getColumnIndex(ContactsContract.PhoneLookup._ID));
                    }

                }catch(Exception e){
                    //sms message from non-contact will cause exception
                    Log.d("Exception caught", e.getMessage());

                }
            }
            //Log.e("Message sent from: ", msg_from + " " + contactName);
            //Log.e("SMS Receiver contact id: ", contactID);

            playRingtone(context, contactID);

        }

    }

    private void playRingtone(Context context, String contactID){
        String ringtonePath = NotifyUtil.notificationSoundPathFromContactsID(context, contactID);
        Uri ringtoneUri = NotifyUtil.getDefaultNotificationSound(context);
        if(ringtonePath.equals(context.getString(R.string.silent_ringtone_key)) || (contactID.equals("") && NotifyUtil.getIgnoreTextsFromNonContactsSetting(context))){
            return;
        }
        if((ringtonePath.equals(NotifyUtil.NOT_FOUND) || ringtonePath.equals(context.getString(R.string.default_contact_notification_sound_key))) && !contactID.equals("")){
            //ringtone not set for contact, so see if any of the groups the contact is in has notification sound set
            ArrayList<String> groupList = NotifyUtil.getGroupIdsFromContactId(context, contactID);
            for(String groupId : groupList){
                String groupNotificationPath = NotifyUtil.notificationSoundPathFromGroupID(context, groupId);
                if(!groupNotificationPath.equals(NotifyUtil.NOT_FOUND)){
                    ringtoneUri = NotifyUtil.uriFromPath(groupNotificationPath);
                    break;
                }
            }
        }
        else {
            ringtoneUri = NotifyUtil.uriFromPath(ringtonePath);
        }

        //use mediaplayer instead of ringtone to play sound because Moto e has ringtones fade in, while notifications play at full volume
        try{
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            player.setDataSource(context, ringtoneUri);
            player.prepare();
            player.setLooping(false);
            player.start();
        }
        catch(Exception e){}
    }


}
