package com.allengarvey.annuncified;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;

/**
 * Created by Allen X on 5/15/15.
 */
public class SettingsActivity extends ActionBarActivity{
    private int smsRecieverState;
    private Switch switch1;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        switch1 = (Switch) findViewById(R.id.switch1);
        displaySMSListenerToggleSwitch();
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
        if((switch1.isChecked() && smsRecieverState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) || (!switch1.isChecked() && smsRecieverState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED)){
            return false;
        }
        else {
            return true;
        }
    }


    @Override
    protected void onPause(){
        super.onPause();
        smsListenerToggleSwitch();
        Log.d("Settings activity paused", "settings changed? " + isSwitchChanged());
    }

    @Override
    protected void onResume(){
        super.onResume();
        displaySMSListenerToggleSwitch();
        Log.d("Settings activity", "Activity resumed");
    }

    private void smsListenerToggleSwitch(){
        if(isSwitchChanged()){
            int newSMSState;
            if(switch1.isChecked()){
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

    private void displaySMSListenerToggleSwitch(){
        smsRecieverState = NotifyUtil.getSMSReceiverStatePreferences(this);
        if(smsRecieverState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED){
            switch1.setChecked(true);
        }
        else{
            switch1.setChecked(false);
        }
    }




}
