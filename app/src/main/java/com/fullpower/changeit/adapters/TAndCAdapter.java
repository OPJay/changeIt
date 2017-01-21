package com.fullpower.changeit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fullpower.changeit.AppApplication;
import com.fullpower.changeit.R;
import com.fullpower.changeit.fragments.EditTimeFragment;
import com.fullpower.changeit.model.SettingsObjects;
import com.fullpower.changeit.serviceAutoChange.AutoChangeService;

import java.util.ArrayList;

/**
 * Created by OJaiswal153939 on 6/19/2016.
 */
public class TAndCAdapter extends ArrayAdapter<SettingsObjects> {
    ArrayList<SettingsObjects> mSettingsObjectses = null;
    Context context;

    public TAndCAdapter(Context context, ArrayList<SettingsObjects> settingsObjects) {
        super(context, R.layout.row_setting, settingsObjects);
        // TODO Auto-generated constructor stub
        this.mSettingsObjectses = settingsObjects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.row_setting, parent, false);
        TextView title = (TextView) convertView.findViewById(R.id.text1);
        title.setText(mSettingsObjectses.get(position).getTitle());
        TextView description = (TextView) convertView.findViewById(R.id.text2);
        description.setText(mSettingsObjectses.get(position).getDescription());
        return convertView;
    }
}