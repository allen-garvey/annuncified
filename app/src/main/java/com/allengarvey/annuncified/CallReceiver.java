package com.allengarvey.annuncified;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;


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

                String ringtonePath = null;
                String contactId = NotifyUtil.getContactIdFromPhoneNumber(app, incomingNumber);

                if(!contactId.equals(NotifyUtil.NOT_FOUND)){
                    String contactSoundPath = NotifyUtil.ringtoneSoundPathFromContactsID(app, contactId);
                    if(!contactSoundPath.equals(NotifyUtil.NOT_FOUND)){
                        ringtonePath = contactSoundPath;
                    }
                    else{
                        ArrayList<String> groupIds = NotifyUtil.getGroupIdsFromContactId(app, contactId);
                        for(String groupId : groupIds){
                            String groupRingtonePath = NotifyUtil.ringtonePathFromGroupID(app, groupId);
                            if(!groupRingtonePath.equals(NotifyUtil.NOT_FOUND)){
                                ringtonePath = groupRingtonePath;
                                break;
                            }
                        }
                    }
                }

                else if(!NotifyUtil.getIgnoreCallsFromNonContactsSetting(app)){
                    ringtonePath = NotifyUtil.getDefaultRingtonePath(app);

                }

                //service required since would lose reference to ringtone
                Intent startIntent = new Intent(app, RingtonePlayingService.class);
                startIntent.putExtra("ringtone-uri", ringtonePath);
                app.startService(startIntent);


            }
            else{ //hung up, took call, etc.
                Intent stopIntent = new Intent(app, RingtonePlayingService.class);
                app.stopService(stopIntent);

            }

        }


    }
}
