package com.allengarvey.annuncified;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import static android.R.color.tertiary_text_light;

public class ContactArrayAdapter extends BaseAdapter{
    private int textViewResourceId;
    private Context app;
    private int resource;
    private String[] objects;
    private Boolean[] contactSoundIsDefault;

    public ContactArrayAdapter(Context context, int resource, int textViewResourceId, String[] objects, Boolean[] contactIsDefault){
        this.textViewResourceId = textViewResourceId;
        this.app = context;
        this.resource = resource;
        this.objects = objects;
        this.contactSoundIsDefault = contactIsDefault;

    }

    @Override
    public int getCount() {
        return objects.length;
    }

    @Override
    public Object getItem(int arg0) {
        return objects[arg0];
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }
        TextView text = (TextView) view.findViewById(textViewResourceId);
        String item = (String) getItem(position);
        text.setText(item);

        if(!contactSoundIsDefault[position]){
            text.setTextColor(Color.BLACK);
        }
        else{
            text.setTextColor(app.getResources().getColor(tertiary_text_light));
        }
        return view;
    }
}
