package com.allengarvey.annuncified;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;


public class SettingsActivity extends ActionBarActivity{
    private int smsRecieverState;
    private int callRecieverState;
    private Switch listenForTextsSwitch;
    private Switch ignoreTextsFromNonContactsSwitch;
    private Switch listenForCallsSwitch;
    private Switch ignoreCallsFromNonContactsSwitch;
    private Switch playCallsAtFullVolumeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        listenForTextsSwitch = (Switch) findViewById(R.id.listen_for_texts_switch);
        ignoreTextsFromNonContactsSwitch = (Switch) findViewById(R.id.ignore_texts_from_non_contacts_switch);
        listenForCallsSwitch = (Switch) findViewById(R.id.listen_for_calls_switch);
        ignoreCallsFromNonContactsSwitch = (Switch) findViewById(R.id.ignore_calls_from_non_contacts_switch);
        playCallsAtFullVolumeSwitch = (Switch) findViewById(R.id.play_calls_at_full_volume_switch);

        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause(){
        super.onPause();
        saveSettings();

    }

    @Override
    protected void onResume(){
        super.onResume();
        init();
    }

    private void saveSettings(){
        saveSmsListenerToggleSwitchSetting();
        saveIgnoreTextsFromNonContactsSetting();
    }

    public void switchChanged(View view){
        if(view == ignoreTextsFromNonContactsSwitch){
            saveIgnoreTextsFromNonContactsSetting();
        }
        else if(view == listenForTextsSwitch){
            saveSmsListenerToggleSwitchSetting();
        }
        else if(view == ignoreCallsFromNonContactsSwitch){
            saveIgnoreCallsFromNonContactsSetting();
        }
        else if(view == listenForCallsSwitch){
            saveCallListenerToggleSwitchSetting();
        }
        else{
            confirmPlayRingtonesAtFullVolumeSwitchSetting();
        }
    }

    private void saveIgnoreCallsFromNonContactsSetting(){
        boolean newSetting = ignoreCallsFromNonContactsSwitch.isChecked();
        NotifyUtil.setIgnoreCallsFromNonContactsSetting(this, newSetting);
    }

    private void saveCallListenerToggleSwitchSetting(){
        int newState;
        if(listenForCallsSwitch.isChecked()){
            newState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        }
        else {
            newState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }

        //add method to actually turn on and turn off receiver
        NotifyUtil.setCallReceiverStatePreferences(this, newState);
    }

    private void showConfirmDialogue(final boolean newSetting){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        savePlayRingtonesAtFullVolumeSwitchSetting(newSetting);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        playCallsAtFullVolumeSwitch.setChecked(false);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.setting_play_calls_at_full_volume_modal_warning)).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void confirmPlayRingtonesAtFullVolumeSwitchSetting(){
        final boolean newSetting = playCallsAtFullVolumeSwitch.isChecked();
        if(newSetting){
            //display modal dialogue confirming action
            showConfirmDialogue(newSetting);
        }
        else{
            savePlayRingtonesAtFullVolumeSwitchSetting(newSetting);
        }

    }

    private void savePlayRingtonesAtFullVolumeSwitchSetting(boolean newSetting){
        NotifyUtil.setPlayCallsAtFullVolumeSetting(this, newSetting);
    }

    private void saveIgnoreTextsFromNonContactsSetting(){
        boolean newSetting = ignoreTextsFromNonContactsSwitch.isChecked();
        NotifyUtil.setIgnoreTextsFromNonContactsSetting(this, newSetting);
    }

    private void saveSmsListenerToggleSwitchSetting(){
        int newSMSState;
        if(listenForTextsSwitch.isChecked()){
            newSMSState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        }
        else {
            newSMSState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }

        NotifyUtil.setSMSReceiverState(this, newSMSState);
        NotifyUtil.setSMSReceiverStatePreferences(this, newSMSState);

    }



    private void init(){
        displaySMSListenerToggleSwitch();
        displayCallListenerToggleSwitch();
        ignoreTextsFromNonContactsSwitch.setChecked(NotifyUtil.getIgnoreTextsFromNonContactsSetting(this));
        ignoreCallsFromNonContactsSwitch.setChecked(NotifyUtil.getIgnoreCallsFromNonContactsSetting(this));
        playCallsAtFullVolumeSwitch.setChecked(NotifyUtil.getPlayCallsAtFullVolumeSetting(this));

    }

    private void displaySMSListenerToggleSwitch(){
        smsRecieverState = NotifyUtil.getSMSReceiverStatePreferences(this);
        if(smsRecieverState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED){
            listenForTextsSwitch.setChecked(true);
        }
        else{
            listenForTextsSwitch.setChecked(false);
        }
    }

    private void displayCallListenerToggleSwitch(){
        callRecieverState = NotifyUtil.getCallReceiverStatePreferences(this);
        if(callRecieverState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED){
            listenForCallsSwitch.setChecked(true);
        }
        else{
            listenForCallsSwitch.setChecked(false);
        }
    }






}
