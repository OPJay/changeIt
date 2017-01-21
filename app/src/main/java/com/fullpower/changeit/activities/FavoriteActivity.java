package com.fullpower.changeit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.fullpower.changeit.R;
import com.fullpower.changeit.fragments.FavouriteFragment;
import com.fullpower.changeit.fragments.FavouriteFragmentOffline;

/**
 * Created by OJaiswal153939 on 1/2/2016.
 */
public class FavoriteActivity extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        setContentView(R.layout.activity_fragment_favorite);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment =
                fm.findFragmentById(R.id.fragment_container_favourite);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container_favourite, fragment)
                    .commit();
        }
    }
    public Fragment createFragment()
    {
        return FavouriteFragmentOffline.newInstance();
    }
    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, FavoriteActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_remove, menu);
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
