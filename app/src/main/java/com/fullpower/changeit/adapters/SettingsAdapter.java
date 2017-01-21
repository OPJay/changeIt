package com.fullpower.changeit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.fullpower.changeit.activities.TAndConditionActivity;
import com.fullpower.changeit.fragments.EditTimeFragment;
import com.fullpower.changeit.model.SettingsObjects;
import com.fullpower.changeit.serviceAutoChange.AutoChangeService;

import java.util.ArrayList;

/**
 * Created by OJaiswal153939 on 6/19/2016.
 */
public class SettingsAdapter extends ArrayAdapter<SettingsObjects> {
    ArrayList<SettingsObjects> mSettingsObjectses = null;
    Context context;

    public SettingsAdapter(Context context, ArrayList<SettingsObjects> settingsObjects) {
        super(context, R.layout.row_setting, settingsObjects);
        // TODO Auto-generated constructor stub
        this.mSettingsObjectses = settingsObjects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        SwitchCompat cb;
        if (position == 1) {
            convertView = inflater.inflate(R.layout.row_setting_checkbox2, parent, false);
            cb = (SwitchCompat) convertView.findViewById(R.id.switchB);
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AppApplication.mContext);
            cb.setChecked(prefs.getBoolean("autoChangeWallpaper", false));
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //final int mChoice = prefs.getInt("choice", 0);
                    if (isChecked) {
                        prefs.edit().putBoolean("autoChangeWallpaper", true).apply();
                        Toast.makeText(AppApplication.mContext, "Auto wallpaper change enabled", Toast.LENGTH_SHORT).show();
                        boolean isServiceAlarmOn = AutoChangeService.isServiceAlarmOn(AppApplication.mContext);
                        Long interval=prefs.getLong("interval",24*3600*1000l);
                        AutoChangeService.setServiceAlarm(AppApplication.mContext, interval, true);
                        //NotificationService.setServiceAlarm(AppApplication.mContext,24*60*60*1000l, true);
                        //Log.i("SettingsAdapter","Checked clicked");
                    } else {
                        prefs.edit().putBoolean("autoChangeWallpaper", false).apply();
                        //Toast.makeText(context, "Auto wallpaper change disabled", Toast.LENGTH_SHORT).show();
                        boolean isServiceAlarmOn = AutoChangeService.isServiceAlarmOn(AppApplication.mContext);
                        AutoChangeService.setServiceAlarm(AppApplication.mContext, 0l, false);
                    }
                }
            });
        } else if (position == 2) {
            convertView = inflater.inflate(R.layout.row_setting, parent, false);
            RelativeLayout relativelayout = (RelativeLayout) convertView.findViewById(R.id.rowsettings);
            relativelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentActivity fragmentActivity = (FragmentActivity) context;
                    FragmentManager fm = (FragmentManager) fragmentActivity.getSupportFragmentManager();
                    EditTimeFragment editTimeFragment = new EditTimeFragment();
                    editTimeFragment.show(fm, "fragment_edit_name");
                }
            });
        }
        else if(position==4)
        {
            convertView = inflater.inflate(R.layout.row_setting, parent, false);
            FragmentTransaction mFragmentTransaction;
            RelativeLayout relativelayout = (RelativeLayout) convertView.findViewById(R.id.rowsettings);
            relativelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentActivity fragmentActivity = (FragmentActivity) context;
                    Intent intent=new Intent(context, TAndConditionActivity.class);
                    context.startActivity(intent);
                }
            });
        }
        else if(position==5)
        {
            convertView = inflater.inflate(R.layout.row_setting_end, parent, false);
        }
        else
        {
            convertView = inflater.inflate(R.layout.row_setting, parent, false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.text1);
        title.setText(mSettingsObjectses.get(position).getTitle());
        TextView description = (TextView) convertView.findViewById(R.id.text2);
        description.setText(mSettingsObjectses.get(position).getDescription());
        return convertView;
    }
}