package com.allengarvey.annuncified;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


public class CallReceiver extends BroadcastReceiver{
    public void onReceive(Context context, Intent intent) {

        try {
            TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            //Create Listener
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
