package com.fullpower.changeit.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.fullpower.changeit.activities.WallpaperPagerActivity;
import com.fullpower.changeit.fragments.InstagramFragment;
import com.fullpower.changeit.fragments.PagerFragment;
import com.fullpower.changeit.model.PhotoLab;
import com.fullpower.changeit.service500px.Feed500px;
import com.fullpower.changeit.service500px.Service500px;
import com.fullpower.changeit.serviceAlphaCoders.AlphaCodersService;
import com.fullpower.changeit.serviceAlphaCoders.FeedAlphaCoders;
import com.fullpower.changeit.serviceFlickr.FeedFlickr;
import com.fullpower.changeit.serviceFlickr.FlickrService;
import com.fullpower.changeit.servicePixabay.FeedPixabay;
import com.fullpower.changeit.servicePixabay.PixabayService;
import com.fullpower.changeit.serviceUnSplash.PhotoUnSplash;
import com.fullpower.changeit.serviceUnSplash.UnSplashService;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by OJaiswal153939 on 3/16/2016.
 */
public class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private PhotoLab mPhotoLab;
    public static int position = 0;
    private Context mContext;
    private int tab;
    private Service500px mService500px;
    private FlickrService mFlickrService;
    private UnSplashService mUnSplashService;
    private PixabayService mPixabayService;
    private AlphaCodersService mAlphaCodersService;
    public static boolean notifyDataSetChanged = false;
    public static void setNotifyDataSetChanged(boolean notifyDataSetChanged) {
        MyFragmentStatePagerAdapter.notifyDataSetChanged = notifyDataSetChanged;
    }

    public static int getPosition() {
        return position;
    }

    public MyFragmentStatePagerAdapter(FragmentManager fm, Context context, int t) {
        super(fm);
        mPhotoLab = PhotoLab.getPhotoLab(context);
        mContext = context;
        tab = t;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.500px.com/v1")
                .build();
        mService500px = restAdapter.create(Service500px.class);
        RestAdapter restAdapterFlickr = new RestAdapter.Builder()
                .setEndpoint("https://api.flickr.com/services")
                .build();
        mFlickrService = restAdapterFlickr.create(FlickrService.class);
        RestAdapter restAdapterUnSplash = new RestAdapter.Builder()
                .setEndpoint("https://api.unsplash.com")
                .build();
        mUnSplashService = restAdapterUnSplash.create(UnSplashService.class);
        RestAdapter restAdapterPixabay = new RestAdapter.Builder()
                .setEndpoint("https://api.unsplash.com")
                .build();
        mPixabayService = restAdapterPixabay.create(PixabayService.class);

        RestAdapter restAdapterAlpahCoders = new RestAdapter.Builder()
                .setEndpoint("https://wall.alphacoders.com/api2.0")
                .build();
        mAlphaCodersService = restAdapterAlpahCoders.create(AlphaCodersService.class);

    }

    public void loadData500pxSearch(String term, final int rrp, final int currentPage, String imageSize, String consumerKey, String exclude,Integer tags,String sort,final Callback<Feed500px> callback) {
        mService500px.getFeedSearch(term, rrp, currentPage, imageSize, consumerKey, exclude, 1, InstagramFragment.sort,new Callback<Feed500px>() {
            @Override
            public void success(Feed500px feed, Response response) {
                mPhotoLab.addObjects(feed.photos);
                notifyDataSetChanged();
                MyRecyclerViewAdapter.setDoNotifyDataSetChangedOnce(true,tab);
                callback.success(feed, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }
    public void loadData500px(String feature, final int rrp,String only, final int currentPage, String imageSize, String consumerKey, String exclude,Integer tags,final Callback<Feed500px> callback) {
        mService500px.getFeed(feature, rrp,only, currentPage, imageSize, consumerKey, exclude, 1,new Callback<Feed500px>() {
            @Override
            public void success(Feed500px feed, Response response) {
                mPhotoLab.addObjects(feed.photos);
                notifyDataSetChanged();
                MyRecyclerViewAdapter.setDoNotifyDataSetChangedOnce(true,tab);
                callback.success(feed, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void loadDataPixabay(String key, String imageType, boolean editorsChoice, String order, boolean safeSearch, int page, int perPage, final Callback<FeedPixabay> callback) {
        mPixabayService.getFeed(key, imageType, editorsChoice, order, safeSearch, page, perPage, new Callback<FeedPixabay>() {
            @Override
            public void success(FeedPixabay feed, Response response) {
                mPhotoLab.addObjects(feed.hits);
                notifyDataSetChanged();
                MyRecyclerViewAdapter.setDoNotifyDataSetChangedOnce(true,tab);
                callback.success(feed, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void loadFlickrData(String method, String key, String date, String extras, String format, String per_page,
                               String page, String nonjsoncallback, final Callback<FeedFlickr> callback) {
        mFlickrService.getFeed(method, key, date, extras, format, per_page, page, nonjsoncallback, new Callback<FeedFlickr>() {
            @Override
            public void success(FeedFlickr feed, Response response) {
                WallpaperPagerActivity.loading = false;
                mPhotoLab.addObjects(feed.photos.photo);
                notifyDataSetChanged();
                MyRecyclerViewAdapter.setDoNotifyDataSetChangedOnce(true,tab);
                callback.success(feed, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void loadFlickrDataSearch(String method, String key, String text, String extras, String format, String per_page,
                               String page,String sort, String nonjsoncallback, final Callback<FeedFlickr> callback) {
        mFlickrService.getFeedSearch(method, key, text, extras, format, per_page, page,sort, nonjsoncallback, new Callback<FeedFlickr>() {
            @Override
            public void success(FeedFlickr feed, Response response) {
                WallpaperPagerActivity.loading = false;
                mPhotoLab.addObjects(feed.photos.photo);
                notifyDataSetChanged();
                MyRecyclerViewAdapter.setDoNotifyDataSetChangedOnce(true,tab);
                callback.success(feed, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void loadDataAlphaCoders(String key, String method, String info_level, Integer page, String check_last, final Callback<FeedAlphaCoders> callback) {
        mAlphaCodersService.getFeed(key, method, info_level, page, check_last, new Callback<FeedAlphaCoders>() {
            @Override
            public void success(FeedAlphaCoders feed, Response response) {
                WallpaperPagerActivity.loading = false;
                mPhotoLab.addObjects(feed.wallpapers);
                notifyDataSetChanged();
                MyRecyclerViewAdapter.setDoNotifyDataSetChangedOnce(true,tab);
                callback.success(feed, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void loadUnSplashData(String clientId, String perPage, String currentPage, final Callback<List<PhotoUnSplash>> callback) {
        mUnSplashService.getFeed(clientId, perPage, currentPage, new Callback<List<PhotoUnSplash>>() {
            @Override
            public void success(List<PhotoUnSplash> feed, Response response) {
                WallpaperPagerActivity.loading = false;
                mPhotoLab.addObjects(feed);
                notifyDataSetChanged();
                MyRecyclerViewAdapter.setDoNotifyDataSetChangedOnce(true,tab);
                callback.success(feed, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    @Override
    public int getCount() {
        if (notifyDataSetChanged) {
            notifyDataSetChanged = false;
            notifyDataSetChanged();
        }
        return mPhotoLab.getSize(tab);
    }

    @Override
    public Fragment getItem(int position) {
        this.position = position;
        return PagerFragment.newInstance(position, tab);
    }
}
