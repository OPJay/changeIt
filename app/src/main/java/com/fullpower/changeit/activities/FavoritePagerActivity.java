package com.fullpower.changeit.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.fullpower.changeit.AppApplication;
import com.fullpower.changeit.R;
import com.fullpower.changeit.adapters.FavoritePagerAdapter;
import com.fullpower.changeit.adapters.FavoriteRecyclerViewAdapter;
import com.fullpower.changeit.model.PhotoLab;
import com.fullpower.changeit.model.PhotoObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FavoritePagerActivity extends AppCompatActivity {
    private final String Tag = "WallpaperPagerSearch";
    private ViewPager mViewPager;
    public static boolean loading = false;
    private PhotoLab mPhotoLab;
    private static final String EXTRA_POSITION_ID = ".position_id";
    private static final String EXTRA_TAB_ID = "tab_id";
    private FavoritePagerAdapter mFavoritePagerAdapter;
    private String TAG = "WallpaperPagerActivity";
    private int tab;
    private int position;
    public static List<PhotoObject> list;
    public static boolean isInternetAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoLab = PhotoLab.getPhotoLab(this);
        setContentView(R.layout.activity_pager);
        position = (int) getIntent()
                .getSerializableExtra(EXTRA_POSITION_ID);
        tab = (int) getIntent().getSerializableExtra(EXTRA_TAB_ID);
        list = mPhotoLab.getPhotos();
        removeFromList(list);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        UpdateUI();
        isInternetAvailable = isNetworkAvailableAndConnected();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setCurrentItem(position);
        isStoragePermissionGranted();
    }

    public void removeFromList(List<PhotoObject>list)
    {
        for(int i=0;i<list.size();i++)
        {
            String str=list.get(i).getUrl();
            //Log.i(TAG,str);
            if(str!=null && str.contains("localImage")) {
                //Log.i(TAG,str);
                list.remove(i);
            }
        }
    }
    public static Intent newIntent(Context packageContext, int
            position, int tab) {
        Intent intent = new Intent(packageContext,
                FavoritePagerActivity.class);
        intent.putExtra(EXTRA_POSITION_ID, position);
        intent.putExtra(EXTRA_TAB_ID, tab);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

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

    private void UpdateUI() {
        FragmentManager fragmentManager =
                getSupportFragmentManager();
        if (mFavoritePagerAdapter == null) {
            mFavoritePagerAdapter = new FavoritePagerAdapter(fragmentManager, this, tab);
            mViewPager.setAdapter(mFavoritePagerAdapter);
        } else {
            //mMyFragmentStatePagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UpdateUI();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission
        }
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
