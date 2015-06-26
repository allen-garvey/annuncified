package com.allengarvey.annuncified;

import android.content.Intent;
import android.media.RingtoneManager;
import android.view.View;
import android.widget.ListView;

/**
 * Class to change default ringtone sound and serves as menu to change group ringtones
 */
public class ChangeRingtonesActivity extends AbstractChangeSoundsActivity{

    @Override
    protected void onListItemClick(ListView l, View v, int postion, long id){
        switch(postion){
            case 0:
                showDefaultSoundModal();
                break;
            default:
                startActivity(new Intent(ChangeRingtonesActivity.this, GroupRingtonesActivity.class));
                break;
        }
    }

    @Override
    protected String getDefaultSoundText(){
        return getString(R.string.set_default_ringtone_text);
    }

    @Override
    protected String getDefaultSoundPath(){
        return NotifyUtil.getDefaultRingtonePath(this);
    }

    @Override
    protected String[] menuList(){
        return new String[] {"", getString(R.string.set_group_ringtones_text)};
    }

    @Override
    protected int getRingtoneManagerType(){
        return RingtoneManager.TYPE_RINGTONE;
    }

    @Override
    protected String defaultSoundModalPickerTitle(){
        return getString(R.string.custom_ringtone_modal_text);
    }

    @Override
    protected void saveDefaultSound(String uriPath){
        NotifyUtil.setDefaultRingtonePath(this, uriPath);
    }
}
