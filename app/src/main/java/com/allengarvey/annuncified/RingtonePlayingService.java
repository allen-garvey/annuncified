package com.allengarvey.annuncified;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;


public class RingtonePlayingService extends Service{
    private Ringtone ringtone;

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Uri ringtoneUri = null;
        String uriPath = intent.getExtras().getString("ringtone-uri");
        if(uriPath != null){
            ringtoneUri = Uri.parse(uriPath);
        }

        this.ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
        ringtone.play();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        ringtone.stop();
    }
}
