package com.allengarvey.annuncified;


import android.content.Intent;
import android.media.RingtoneManager;
import android.view.View;
import android.widget.ListView;

public class ChangeNotificationsActivity extends AbstractChangeSoundsActivity{

    @Override
    protected void onListItemClick(ListView l, View v, int postion, long id){
        switch(postion){
            case 0:
                showDefaultSoundModal();
                break;
            case 1:
                startActivity(new Intent(ChangeNotificationsActivity.this, GroupNotificationSoundsActivity.class));
                break;
            default:
                startActivity(new Intent(ChangeNotificationsActivity.this, ContactNotificationSoundsActivity.class));
                break;
        }
    }

    @Override
    protected String getDefaultSoundText(){
        return getString(R.string.default_notification_text);
    }

    @Override
    protected String getDefaultSoundPath(){
        return NotifyUtil.getDefaultNotificationPath(this);
    }

    @Override
    protected String[] menuList(){
        return new String[]{"", getString(R.string.group_notification_text) , getString(R.string.custom_notification_text)};
    }

    @Override
    protected int getRingtoneManagerType(){
        return RingtoneManager.TYPE_NOTIFICATION;
    }

    @Override
    protected String defaultSoundModalPickerTitle(){
        return getString(R.string.set_default_notification_tone_modal);
    }

    @Override
    protected void saveDefaultSound(String uriPath){
        NotifyUtil.setDefaultNotificationPath(this, uriPath);
    }
}