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
import com.fullpower.changeit.R;
import com.fullpower.changeit.adapters.EditNameDialogListener;
import com.fullpower.changeit.adapters.SettingsAdapter;
import com.fullpower.changeit.adapters.TAndCAdapter;
import com.fullpower.changeit.model.SettingsObjects;

import java.util.ArrayList;


public class TAndCFragment extends ListFragment {
    String mArrayListTitle[] = {
            "Terms & conition"
    };
    String mArrayListDescription[] = {
            "The images and graphics shown in this application are fully owned by the source they come from.They can" +
                    " be photographers,websites or even a company.Even though most of the images are license free,the " +
                    "owner reserves all rights.So comply with any requirements or restrictions imposed on usage of the " +
                    "photos by their respective owners. Remember,Change it" +
                    " doesn't own the images - the users do. Although the Change it can be used to provide you " +
                    "with access to different  photos, neither the provision of the Change it to you nor your use" +
                    " of the this application override the photo owners' requirements and restrictions, which may include \"all rights reserved\" " +
                    "notices (attached to each photo by default when uploaded to Change it), Creative Commons licenses or other terms and conditions " +
                    "that may be agreed upon between you and the owners. In all cases, you are solely responsible for making use of Change it photos in " +
                    "compliance with the photo owners' requirements or restrictions. If you use Change it photos for a commercial purpose, the photos must be" +
                    " marked with a Creative Commons license that allows for such use, unless otherwise agreed upon between you and the owner. ",
    };
    ArrayList<SettingsObjects> mSettingsObjects;

    public TAndCFragment() {
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
        TAndCAdapter adapter = new TAndCAdapter(getActivity(), mSettingsObjects);
        setListAdapter(adapter);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}