package com.fullpower.changeit.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fullpower.changeit.AppApplication;
import com.fullpower.changeit.R;
import com.fullpower.changeit.adapters.EditNameDialogListener;
import com.fullpower.changeit.serviceAlphaCoders.Tag;
import com.fullpower.changeit.serviceAutoChange.AutoChangeService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OJaiswal153939 on 6/19/2016.
 */
public class EditTimeFragment extends DialogFragment implements TextView.OnEditorActionListener {
    private EditText mEditText;
    private Spinner mSpinner;
    private TextView okButton;
    private TextView cancelButton;
    String time;
    int selection;
    String timeInterval;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    int mRadioChoice;
    public EditTimeFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.datepicker_fragment, container);
        mEditText = (EditText) view.findViewById(R.id.edittext);
        mSpinner = (Spinner) view.findViewById(R.id.spinner1);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.radio);
        int buttonId = mRadioGroup.getCheckedRadioButtonId();
        mRadioButton = (RadioButton) view.findViewById(buttonId);
        List<String> categories = new ArrayList<String>();
        categories.add("day");
        categories.add("hour");
        categories.add("minute");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, categories);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSpinner.setAdapter(dataAdapter);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(AppApplication.mContext);
        final String mTimeIntevalVal = prefs.getString("intervalVal", "1");
        final String mTimeIntetvalSpinner = prefs.getString("intervalspinner", "0");
        mRadioChoice= prefs.getInt("radio", 0);
        if (mRadioChoice == 0) {
            mRadioGroup.check(R.id.radio1);
        } else
            mRadioGroup.check(R.id.radio2);
        int i = Integer.parseInt(mTimeIntetvalSpinner);
        mSpinner.setSelection(i);
        mEditText.setText(mTimeIntevalVal);
        mEditText.setOnEditorActionListener(this);
        okButton = (TextView) view.findViewById(R.id.okButton);
        cancelButton = (TextView) view.findViewById(R.id.cancelButton);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selection = position;
                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Selected: " + selection, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = mEditText.getText().toString();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AppApplication.mContext);
                prefs.edit().putString("intervalVal", time).apply();
                selection = mSpinner.getSelectedItemPosition();
                String val = Integer.toString(selection);
                //Log.i("EditFragment", val);
                prefs.edit().putString("intervalspinner", val).apply();
                int buttonId = mRadioGroup.getCheckedRadioButtonId();
                int x = 0;
                if (buttonId == R.id.radio1) {
                    prefs.edit().putInt("radio", 0).apply();
                    x = 0;
                } else if (buttonId == R.id.radio2) {
                    prefs.edit().putInt("radio", 1).apply();
                    x = 1;
                }
                long factor = 1000;
                long interval = 0;
                if (selection == 2)
                    factor *= 60;
                else if (selection == 1)
                    factor *= 3600;
                else
                    factor *= 24 * 3600;
                try {
                    interval = Long.parseLong(time);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid format entered", Toast.LENGTH_SHORT).show();
                }
                if (interval > 0) {
                    interval *= factor;
                    boolean isServiceAlarmOn = AutoChangeService.isServiceAlarmOn(AppApplication.mContext);
                    SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(AppApplication.mContext);
                    boolean b=preferences.getBoolean("autoChangeWallpaper", false);
                    preferences.edit().putLong("interval",interval).apply();
                    if(b)
                    AutoChangeService.setServiceAlarm(getActivity(), interval, true);
                    // Toast.makeText(getContext(), "Selected: " + time, Toast.LENGTH_SHORT).show();
                    dismiss();
                } else
                    Toast.makeText(getContext(), "Enter value greater than zero", Toast.LENGTH_SHORT).show();


            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //getDialog().setTitle("Choose time interval");
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // Show soft keyboard automatically
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEditText.setOnEditorActionListener(this);
        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_SEARCH == actionId) {
            this.dismiss();
            return true;
        }
        return false;
    }
}