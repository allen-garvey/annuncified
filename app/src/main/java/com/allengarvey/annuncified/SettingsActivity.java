package com.allengarvey.annuncified;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;


public class SettingsActivity extends ActionBarActivity{
    private int smsRecieverState;
    private Switch listenForTextsSwitch;
    private Switch ignoreTextsFromNonContactsSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        listenForTextsSwitch = (Switch) findViewById(R.id.annunciator_listen_for_texts_switch);
        ignoreTextsFromNonContactsSwitch = (Switch) findViewById(R.id.ignore_texts_from_non_contacts_switch);
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
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private boolean isSwitchChanged(){
        if((listenForTextsSwitch.isChecked() && smsRecieverState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) || (!listenForTextsSwitch.isChecked() && smsRecieverState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED)){
            return false;
        }
        return true;
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

    private void saveIgnoreTextsFromNonContactsSetting(){
        boolean newSetting = ignoreTextsFromNonContactsSwitch.isChecked();
        boolean oldSetting = NotifyUtil.getIgnoreTextsFromNonContactsSetting(this);
        if(newSetting != oldSetting){
            NotifyUtil.setIgnoreTextsFromNonContactsSetting(this, newSetting);
        }
    }

    private void saveSmsListenerToggleSwitchSetting(){
        if(isSwitchChanged()){
            int newSMSState;
            if(listenForTextsSwitch.isChecked()){
                newSMSState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
            }
            else {
                newSMSState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            }

            NotifyUtil.setSMSReceiverState(this, newSMSState);
            NotifyUtil.setSMSReceiverStatePreferences(this, newSMSState);
            Log.d("Settings activity writing", "Writing shared preferences " + newSMSState);
        }

    }

    private void init(){
        displaySMSListenerToggleSwitch();
        displayIgnoreNotificationsFromNonContactsSwitch();
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

    private void displayIgnoreNotificationsFromNonContactsSwitch(){
        ignoreTextsFromNonContactsSwitch.setChecked(NotifyUtil.getIgnoreTextsFromNonContactsSetting(this));

    }




}
