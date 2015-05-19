package com.allengarvey.annuncified;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by Allen X on 5/15/15.
 */
public class ChangeNotificationsActivity extends ActionBarActivity{
    //Declare UI elements
    private Button defaultNotificationbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_notifications_layout);
        //Inflate UI
        defaultNotificationbutton = (Button) findViewById(R.id.defaultNotificationbutton);

        setDefaultDisplayButtonText(NotifyUtil.getDefaultNotificationSound(this));
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


    public void setNotificationSound(View v){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Default Tone");
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
                setDefaultDisplayButtonText(uri);
                NotifyUtil.getSharedPreferences(this).edit()
                        .putString(getString(R.string.default_notification_sound_uri_shared_preferences_key), uri.toString())
                        .apply();

            }

        }
    }

    public void contactNotificationSoundsActivityLaunch(View v){
        startActivity(new Intent(ChangeNotificationsActivity.this, ContactNotificationSoundsActivity.class));

    }

    private void setDefaultDisplayButtonText(Uri ringtoneUri){
        setDefaultDisplayButtonText(NotifyUtil.ringtoneNameFromUri(this, ringtoneUri));
    }

    private void setDefaultDisplayButtonText(String ringtoneUriName){
        defaultNotificationbutton.setText(ringtoneUriName);
    }


}