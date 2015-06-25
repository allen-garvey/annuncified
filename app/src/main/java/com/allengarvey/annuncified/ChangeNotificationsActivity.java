package com.allengarvey.annuncified;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChangeNotificationsActivity extends ListActivity{
    //Declare UI elements
    private String[] menuList;
    private ArrayAdapter<String>menuAdapter;
    String defaultNotificationText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        menuList = new String[]{"", getResources().getString(R.string.group_notification_text) , getResources().getString(R.string.custom_notification_text)};
        menuAdapter = new ArrayAdapter<>(this, R.layout.wide_list_layout, R.id.list_item, menuList);
        setListAdapter(menuAdapter);
        defaultNotificationText = getResources().getString(R.string.default_notification_text);
        setDefaultNotificationText(NotifyUtil.getDefaultNotificationSound(this));
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


    public void setNotificationSound(){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.set_default_notification_tone_modal));
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, NotifyUtil.getDefaultNotificationSound(this));
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        this.startActivityForResult(intent, 5);

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null){
                setDefaultNotificationText(uri);
                NotifyUtil.getSharedPreferences(this).edit()
                        .putString(getString(R.string.default_notification_sound_uri_shared_preferences_key), uri.toString())
                        .apply();
            }
        }
    }


    @Override
    protected void onListItemClick(ListView l, View v, int postion, long id){
        switch(postion){
            case 0:
                setNotificationSound();
                break;
            case 1:
                startActivity(new Intent(ChangeNotificationsActivity.this, GroupNotificationSoundsActivity.class));
                break;
            default:
                startActivity(new Intent(ChangeNotificationsActivity.this, ContactNotificationSoundsActivity.class));
                break;
        }
    }



    private void setDefaultNotificationText(Uri ringtoneUri){
        setDefaultNotificationText(NotifyUtil.ringtoneNameFromUri(this, ringtoneUri));
    }

    private void setDefaultNotificationText(String ringtoneUriName){
        menuList[0] = defaultNotificationText + " " + ringtoneUriName;
        menuAdapter.notifyDataSetChanged();
    }


}