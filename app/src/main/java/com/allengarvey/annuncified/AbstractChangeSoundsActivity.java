package com.allengarvey.annuncified;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

/**
 * Base class for Change Notifications activity and change ringtones activity
 */
public abstract class AbstractChangeSoundsActivity extends ListActivity{
    //Declare UI elements
    protected String[] menuList;
    protected ArrayAdapter<String> menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        menuList = menuList();
        menuAdapter = new ArrayAdapter<>(this, R.layout.wide_list_layout, R.id.list_item, menuList);
        setListAdapter(menuAdapter);

        setDefaultSoundText(NotifyUtil.ringtoneNameFromUri(this, getDefaultSoundUri()));
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


    public void showDefaultSoundModal(){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, getRingtoneManagerType());
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, defaultSoundModalPickerTitle());
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, getDefaultSoundUri());
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
                setDefaultSoundText(uri);
                saveDefaultSound(uri.toString());
            }
        }
    }


    protected void setDefaultSoundText(Uri ringtoneUri){
        setDefaultSoundText(NotifyUtil.ringtoneNameFromUri(this, ringtoneUri));
    }

    protected void setDefaultSoundText(String ringtoneUriName){
        String ringtoneTitle = ringtoneUriName;
        if(ringtoneUriName == null){
            ringtoneTitle = getString(R.string.silent_ringtone_text);
        }
        menuList[0] = getDefaultSoundText() + " " + ringtoneTitle;
        menuAdapter.notifyDataSetChanged();
    }

    protected Uri getDefaultSoundUri(){
        String path = getDefaultSoundPath();
        if(path == null){
            return null;
        }
        return NotifyUtil.uriFromPath(path);
    }

    abstract protected String getDefaultSoundText();

    abstract protected String getDefaultSoundPath();

    abstract protected String[] menuList();

    abstract protected int getRingtoneManagerType();


    abstract protected String defaultSoundModalPickerTitle();

    abstract protected void saveDefaultSound(String uriPath);

}
