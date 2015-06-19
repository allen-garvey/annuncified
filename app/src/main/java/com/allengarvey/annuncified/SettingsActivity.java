package com.allengarvey.annuncified;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;


public class SettingsActivity extends ActionBarActivity{
    private int smsRecieverState;
    private Switch listenForTextsSwitch;
    private Switch ignoreTextsFromNonContactsSwitch;
    private Switch ignoreCallsFromNonContactsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        listenForTextsSwitch = (Switch) findViewById(R.id.listen_for_texts_switch);
        ignoreTextsFromNonContactsSwitch = (Switch) findViewById(R.id.ignore_texts_from_non_contacts_switch);
        ignoreCallsFromNonContactsSwitch = (Switch) findViewById(R.id.ignore_calls_from_non_contacts_switch);

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
    protected void onResume(){
        super.onResume();
        init();
    }


    public void switchChanged(View view){
        if(view == ignoreTextsFromNonContactsSwitch){
            saveIgnoreTextsFromNonContactsSetting();
        }
        else if(view == listenForTextsSwitch){
            saveSmsListenerToggleSwitchSetting();
        }
        else{
            saveIgnoreCallsFromNonContactsSetting();
        }
    }

    private void saveIgnoreCallsFromNonContactsSetting(){
        boolean ignoreCalls = ignoreCallsFromNonContactsSwitch.isChecked();
        NotifyUtil.setIgnoreCallsFromNonContactsSetting(this, ignoreCalls);

        int callReceiverState;
        if(ignoreCalls){
            callReceiverState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        }
        else {
            callReceiverState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }
        NotifyUtil.setCallReceiverState(this, callReceiverState);
        NotifyUtil.setCallReceiverStatePreferences(this, callReceiverState);
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
        ignoreTextsFromNonContactsSwitch.setChecked(NotifyUtil.getIgnoreTextsFromNonContactsSetting(this));
        ignoreCallsFromNonContactsSwitch.setChecked(NotifyUtil.getIgnoreCallsFromNonContactsSetting(this));


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




}
