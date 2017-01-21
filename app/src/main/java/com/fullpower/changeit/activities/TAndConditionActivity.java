package com.fullpower.changeit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.fullpower.changeit.R;

/**
 * Created by OJaiswal153939 on 1/2/2016.
 */
public class TAndConditionActivity extends AppCompatActivity {
    TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tandc);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbarPager);
        tb.inflateMenu(R.menu.menu_add);
        //mFrameLayout = (FrameLayout) view.findViewById(R.id.searchFrameLayout);
        setSupportActionBar(tb);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tb.setTitle("Terms & Conditons");
        mTextView = (TextView) findViewById(R.id.textTAndC);
        mTextView.setText(R.string.termAndCondition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
