package com.allengarvey.annuncified;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;


public class SettingsActivity extends ActionBarActivity{
    private Switch listenForTextsSwitch;
    private Switch ignoreTextsFromNonContactsSwitch;
    private Switch ignoreCallsFromNonContactsSwitch;
    private Switch startOnBootSwitch;
    private Switch listenForCallsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        listenForTextsSwitch = (Switch) findViewById(R.id.listen_for_texts_switch);
        listenForCallsSwitch = (Switch) findViewById(R.id.listen_for_calls_switch);
        ignoreTextsFromNonContactsSwitch = (Switch) findViewById(R.id.ignore_texts_from_non_contacts_switch);
        ignoreCallsFromNonContactsSwitch = (Switch) findViewById(R.id.ignore_calls_from_non_contacts_switch);
        startOnBootSwitch = (Switch) findViewById(R.id.start_on_boot_switch);
        init(); //have to call init before attaching listeners or for some reason doesn't display properly

        Switch[] switches = {listenForCallsSwitch, listenForTextsSwitch};

        for(Switch s : switches){ //sliding a switch is different than clicking it, so this captures both
            s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                    switchChanged(buttonView);
                }
            });
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        saveAllSettings(); //required to save onPause as well as on switchChanged because otherwise sometimes the settings won't be saved
    }

    private void saveAllSettings(){
        int newSMSState = getReceiverState(listenForTextsSwitch.isChecked());
        NotifyUtil.setSMSReceiverState(this, newSMSState);

        int newCallReceiverState = getReceiverState(listenForCallsSwitch.isChecked());
        NotifyUtil.setCallReceiverState(this, newCallReceiverState);

        NotifyUtil.saveAllSettings(this, newSMSState, newCallReceiverState, ignoreCallsFromNonContactsSwitch.isChecked(), ignoreTextsFromNonContactsSwitch.isChecked(), startOnBootSwitch.isChecked());

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


    public void switchChanged(View view){


        if(view == listenForCallsSwitch){
            setEnabledCallSettings(listenForCallsSwitch.isChecked());
        }
        else if(view == listenForTextsSwitch){
            setEnabledNotificationSettings(listenForTextsSwitch.isChecked());
        }
        saveAllSettings();

    }



    private void setEnabledCallSettings(boolean isEnabled){
        ignoreCallsFromNonContactsSwitch.setEnabled(isEnabled);

    }

    private void setEnabledNotificationSettings(boolean isEnabled){
        ignoreTextsFromNonContactsSwitch.setEnabled(isEnabled);
    }

    private void displayEnabled(){
        setEnabledCallSettings(listenForCallsSwitch.isChecked());
        setEnabledNotificationSettings(listenForTextsSwitch.isChecked());
    }


    private void init(){
        displaySMSListenerToggleSwitch();
        displayCallListenerSwitch();
        ignoreTextsFromNonContactsSwitch.setChecked(NotifyUtil.getIgnoreTextsFromNonContactsSetting(this));
        ignoreCallsFromNonContactsSwitch.setChecked(NotifyUtil.getIgnoreCallsFromNonContactsSetting(this));
        startOnBootSwitch.setChecked(NotifyUtil.getStartAppOnBootSetting(this));
        displayEnabled();

    }

    private void displaySMSListenerToggleSwitch(){
        int smsRecieverState = NotifyUtil.getSMSReceiverStatePreferences(this);
        listenForTextsSwitch.setChecked(isEnabled(smsRecieverState));
    }

    private void displayCallListenerSwitch(){
        int callReceiverState = NotifyUtil.getCallReceiverStatePreferences(this);
        listenForCallsSwitch.setChecked(isEnabled(callReceiverState));
    }

    private int getReceiverState(boolean isEnabled){
        if(isEnabled){
            return PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        }
        return PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
    }

    private boolean isEnabled(int receiverState){
        return receiverState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }




}
