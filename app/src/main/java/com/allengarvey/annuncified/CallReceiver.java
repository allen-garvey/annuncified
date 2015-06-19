package com.allengarvey.annuncified;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


public class CallReceiver extends BroadcastReceiver{
    public void onReceive(Context context, Intent intent) {

        try {
            TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            //Create Listner
            MyPhoneStateListener PhoneListener = new MyPhoneStateListener(context);

            // Register listener for LISTEN_CALL_STATE
            tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener{
        private Context app;

        public MyPhoneStateListener(Context app){
            super();
            this.app = app;
        }

        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                String msg = "New Phone Call Event. Incoming Number : "+incomingNumber;
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(app, msg, duration);
                toast.show();

                if(NotifyUtil.getIgnoreCallsFromNonContactsSetting(app) && NotifyUtil.getContactIdFromPhoneNumber(app, incomingNumber).equals(NotifyUtil.NOT_FOUND)){
                    NotifyUtil.saveOriginalDefaultRingtonePath(app);
                    silenceCall();
                }
            }
            else{ //hung up, took call, etc.
                NotifyUtil.resetOriginalDefaultRingtonePath(app);
            }
        }

        public void silenceCall(){
            RingtoneManager.setActualDefaultRingtoneUri(app, RingtoneManager.TYPE_RINGTONE, null);
        }

    }
}
