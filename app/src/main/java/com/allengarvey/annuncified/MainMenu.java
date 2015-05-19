package com.allengarvey.annuncified;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import java.util.List;


public class MainMenu extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
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


    private void init(){
        NotifyUtil.setSMSReceiverState(this, NotifyUtil.getSMSReceiverStatePreferences(this));
    }




    public void settingsButtonAction(View v){
        startActivity(new Intent(MainMenu.this, SettingsActivity.class));

    }

    public void settingsHelpButtonAction(View v){
        startActivity(new Intent(MainMenu.this, HelpActivity.class));

    }

    public void settingsSetNotificationsButtonAction(View v){
        startActivity(new Intent(MainMenu.this, ChangeNotificationsActivity.class));

    }

}