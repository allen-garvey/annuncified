package com.allengarvey.annuncified;

import android.content.Context;
import android.media.RingtoneManager;

/**
 * Created by Allen X on 6/24/15.
 */
public class GroupRingtonesActivity extends GroupNotificationSoundsActivity{

    @Override
    protected String notificationSoundPathFromItemID(Context context, String itemID){
        return NotifyUtil.ringtonePathFromGroupID(this, itemID);
    }

    @Override
    public void setNotificationSoundPathForItemId(Context context, String groupID, String uriPath){
        NotifyUtil.setRingtonePathForGroup(this, groupID, uriPath);
    }

    @Override
    protected String modalTitle(){
        return getString(R.string.custom_ringtone_modal_text);
    }

    @Override
    protected int modalPickerSoundType(){
        return RingtoneManager.TYPE_RINGTONE;
    }

    @Override
    protected String defaultSoundText(){
        return getString(R.string.ringtone_not_set_text);
    }
}
