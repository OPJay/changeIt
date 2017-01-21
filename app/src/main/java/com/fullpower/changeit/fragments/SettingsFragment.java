package com.fullpower.changeit.fragments;

/**
 * Created by OJaiswal153939 on 3/3/2016.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.fullpower.changeit.AppApplication;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.fullpower.changeit.R;
import com.fullpower.changeit.adapters.EditNameDialogListener;
import com.fullpower.changeit.adapters.SettingsAdapter;
import com.fullpower.changeit.model.SettingsObjects;

import java.util.ArrayList;


public class SettingsFragment extends ListFragment implements AdapterView.OnItemClickListener, EditNameDialogListener {
    String mArrayListTitle[] = {
            "Clean cache",
            "Enable Auto change of wallpaper",
            "Auto change time interval",
            "Version",
            "Terms & conditions",
            "About Change It"
    };
    String mArrayListDescription[] = {
            "Cleans cached contents in cache memory",
            //"Enables app to prefetch the content to improve loading of contents",
            "Changes the wallpaper at regular time interval",
            "Choose the regular time inteval at which wallapaper changes",
            "1.0",
            "Terms & conditions of use",
            "Change it is developed by Full Power Studio " +
                    "and is free to use.We hope that your phone looks more better with our app."
    };
    ArrayList<SettingsObjects> mSettingsObjects;

    public SettingsFragment() {
        // Required empty public constructor
        mSettingsObjects = new ArrayList<>();
        for (int i = 0; i < mArrayListTitle.length; i++) {
            SettingsObjects ob = new SettingsObjects(mArrayListTitle[i], mArrayListDescription[i]);
            mSettingsObjects.add(ob);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SettingsAdapter adapter = new SettingsAdapter(getActivity(), mSettingsObjects);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SwitchCompat mSwitch;
        switch (position) {
            case 0:
                try {
                    //FastImageLoader.clearDiskCache();
                    AppApplication.deleteCache(getContext());
                }
                catch (Exception e)
                {

                }
                Toast.makeText(getActivity(), "Cache cleared", Toast.LENGTH_SHORT).show();
                return;
            default:
                return;
        }
    }

    @Override
    public void onFinishEditDialog(String inputText) {
    }


}