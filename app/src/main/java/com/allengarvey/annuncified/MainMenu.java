package com.allengarvey.annuncified;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainMenu extends ListActivity{
    private String[] menuList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        menuList = getResources().getStringArray(R.array.main_menu);
        setListAdapter(new ArrayAdapter<>(this, R.layout.main_list_layout, R.id.listItem, menuList));
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


    private void init(){
        NotifyUtil.setSMSReceiverState(this, NotifyUtil.getSMSReceiverStatePreferences(this));
        NotifyUtil.setCallReceiverState(this, NotifyUtil.getCallReceiverStatePreferences(this));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int postion, long id){
        switch(postion){
            case 0:
                startActivity(new Intent(MainMenu.this, ChangeNotificationsActivity.class));
                break;
            case 1:
                startActivity(new Intent(MainMenu.this, SettingsActivity.class));
                break;
            default:
                startActivity(new Intent(MainMenu.this, HelpActivity.class));
                break;
        }
    }

}