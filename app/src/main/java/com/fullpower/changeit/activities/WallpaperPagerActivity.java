package com.fullpower.changeit.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.fullpower.changeit.R;
import com.fullpower.changeit.adapters.MyFragmentStatePagerAdapter;
import com.fullpower.changeit.fragments.ExploreFragment;
import com.fullpower.changeit.fragments.InstagramFragment;
import com.fullpower.changeit.model.PhotoLab;
import com.fullpower.changeit.service500px.Feed500px;
import com.fullpower.changeit.serviceAlphaCoders.FeedAlphaCoders;
import com.fullpower.changeit.serviceFlickr.FeedFlickr;
import com.fullpower.changeit.servicePixabay.FeedPixabay;
import com.fullpower.changeit.serviceUnSplash.PhotoUnSplash;
import com.fullpower.changeit.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WallpaperPagerActivity extends AppCompatActivity {
    private final String Tag = "WallPagerAvtivity";
    private ViewPager mViewPager;
    public static boolean loading = false;
    private int previousItemCount = 0, totalItemCount;
    private PhotoLab mPhotoLab;
    private static final String EXTRA_POSITION_ID = ".position_id";
    private static final String EXTRA_TAB_ID = "tab_id";
    private MyFragmentStatePagerAdapter mMyFragmentStatePagerAdapter;
    private String TAG = "WallpaperPagerActivity";
    private int tab;
    private int position;
    private int visibleThreshold = 7;
    private int rrp = 100;
    Object ob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoLab = PhotoLab.getPhotoLab(this);
        setContentView(R.layout.activity_pager);
        position = (int) getIntent()
                .getSerializableExtra(EXTRA_POSITION_ID);
        tab = (int) getIntent().getSerializableExtra(EXTRA_TAB_ID);
        ob = mPhotoLab.getNextPhoto(tab, position);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        UpdateUI();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                totalItemCount = mPhotoLab.getSize(tab);
                if (!loading && position > totalItemCount - visibleThreshold && PhotoLab.counter<ExploreFragment.maxCountOfPhotosApi) {
                    loading = true;
                    int x = InstagramFragment.currentSearch500px++ % 2;
                    if (x == 0) {
                        final int i = (InstagramFragment.termCount++) % (ExploreFragment.termArray500px.length);
                        mMyFragmentStatePagerAdapter.loadData500pxSearch(ExploreFragment.termArray500px[i], rrp, InstagramFragment.currentPage500pxSearch[i],
                                InstagramFragment.imageSize, ExploreFragment.key500px, ExploreFragment.exclude, 1, InstagramFragment.sort, new Callback<Feed500px>() {
                                    @Override
                                    public void success(Feed500px feed, Response response) {
                                        //mFragmentStatePagerAdapter.notifyDataSetChanged();
                                        InstagramFragment.currentPage500px[i]++;
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        //Toast.makeText(WallpaperPagerActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        final int i = (InstagramFragment.featureCount++) % (ExploreFragment.feature.length);
                        mMyFragmentStatePagerAdapter.loadData500px(ExploreFragment.feature[i], rrp, ExploreFragment.includeOnly, InstagramFragment.currentPage500px[i],
                                InstagramFragment.imageSize, ExploreFragment.key500px, ExploreFragment.exclude, 1, new Callback<Feed500px>() {
                                    @Override
                                    public void success(Feed500px feed, Response response) {
                                        //mFragmentStatePagerAdapter.notifyDataSetChanged();
                                        InstagramFragment.currentPage500px[i]++;
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        //Toast.makeText(WallpaperPagerActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    mMyFragmentStatePagerAdapter.loadUnSplashData(ExploreFragment.keyUnsplash, ExploreFragment.perPageUnSplash, InstagramFragment.currentPageUnSplash, new Callback<List<PhotoUnSplash>>() {
                        @Override
                        public void success(List<PhotoUnSplash> feed, Response response) {
                            //mFragmentStatePagerAdapter.notifyDataSetChanged();
                            int val = Integer.valueOf(InstagramFragment.currentPageFlickr);
                            val = val + 1;
                            InstagramFragment.currentPageFlickr = Integer.toString(val);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            //Toast.makeText(WallpaperPagerActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (Utils.getResolution() == 1) {
                        mMyFragmentStatePagerAdapter.loadDataPixabay(ExploreFragment.keyPixabay, InstagramFragment.image_type, InstagramFragment.editors_choice, InstagramFragment.order, InstagramFragment.safesearch, InstagramFragment.currentPagePixabay, ExploreFragment.perPagePixabay, new Callback<FeedPixabay>() {
                            @Override
                            public void success(FeedPixabay feed, Response response) {
                                //mFragmentStatePagerAdapter.notifyDataSetChanged();
                                InstagramFragment.currentPagePixabay++;
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                //Toast.makeText(WallpaperPagerActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    final int v = (InstagramFragment.currentAlphaCoders++) % ExploreFragment.methodAlphaCoders.length;
                    mMyFragmentStatePagerAdapter.loadDataAlphaCoders(ExploreFragment.keyAlphaCoders, ExploreFragment.methodAlphaCoders[v], InstagramFragment.info_level, InstagramFragment.currentAlphaCodersCount[v], InstagramFragment.check_last, new Callback<FeedAlphaCoders>() {
                        @Override
                        public void success(FeedAlphaCoders feed, Response response) {
                            //mFragmentStatePagerAdapter.notifyDataSetChanged();
                            InstagramFragment.currentAlphaCodersCount[v]++;
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            //Toast.makeText(WallpaperPagerActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                    int y = InstagramFragment.methodtype++ % 2;
                    if (y == 0) {
                        mMyFragmentStatePagerAdapter.loadFlickrData(InstagramFragment.method, ExploreFragment.keyFlickr, InstagramFragment.date, InstagramFragment.extras, InstagramFragment.format, ExploreFragment.perPageFlickr,
                                InstagramFragment.currentPageFlickr, InstagramFragment.nonjsoncallback, new Callback<FeedFlickr>() {
                                    @Override
                                    public void success(FeedFlickr feed, Response response) {
                                        Calendar calendar;
                                        calendar = Calendar.getInstance();
                                        calendar.add(Calendar.DATE, -InstagramFragment.dateDecCounter);
                                        InstagramFragment.dateDecCounter++;
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                        InstagramFragment.date = df.format(calendar.getTime());
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        //Toast.makeText(WallpaperPagerActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        final int m = (InstagramFragment.currentSearchFlickr++) % ExploreFragment.termArrayFlickr.length;
                        mMyFragmentStatePagerAdapter.loadFlickrDataSearch(InstagramFragment.method, ExploreFragment.keyFlickr, ExploreFragment.termArrayFlickr[m], InstagramFragment.extras, InstagramFragment.format, ExploreFragment.perPageFlickr,
                                InstagramFragment.currentFlickrSearch[m], InstagramFragment.sortFlickr, InstagramFragment.nonjsoncallback, new Callback<FeedFlickr>() {
                                    @Override
                                    public void success(FeedFlickr feed, Response response) {
                                        int v = Integer.valueOf(InstagramFragment.currentFlickrSearch[m]);
                                        v = v + 1;
                                        InstagramFragment.currentFlickrSearch[m] = Integer.toString(v);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        //Toast.makeText(WallpaperPagerActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
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

    public static Intent newIntent(Context packageContext, int
            position, int tab) {
        Intent intent = new Intent(packageContext,
                WallpaperPagerActivity.class);
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
        if (mMyFragmentStatePagerAdapter == null) {
            mMyFragmentStatePagerAdapter = new MyFragmentStatePagerAdapter(fragmentManager, this, tab);
            mViewPager.setAdapter(mMyFragmentStatePagerAdapter);
        } else {
            mMyFragmentStatePagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UpdateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
        }
    }
}
