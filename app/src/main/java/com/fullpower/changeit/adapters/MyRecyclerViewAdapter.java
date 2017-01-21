package com.fullpower.changeit.adapters;

/**
 * Created by OJaiswal153939 on 2/25/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.fullpower.changeit.AppApplication;
import com.fullpower.changeit.R;
import com.fullpower.changeit.activities.WallpaperPagerActivity;
import com.fullpower.changeit.scrollListeners.EndlessRecyclerOnScrollListener;
import com.fullpower.changeit.model.PhotoLab;
import com.fullpower.changeit.service500px.Feed500px;
import com.fullpower.changeit.service500px.Photo500px;
import com.fullpower.changeit.service500px.Service500px;
import com.fullpower.changeit.serviceAlphaCoders.AlphaCodersService;
import com.fullpower.changeit.serviceAlphaCoders.FeedAlphaCoders;
import com.fullpower.changeit.serviceAlphaCoders.Wallpaper;
import com.fullpower.changeit.serviceFlickr.FeedFlickr;
import com.fullpower.changeit.serviceFlickr.FlickrService;
import com.fullpower.changeit.serviceFlickr.PhotoFlickr;
import com.fullpower.changeit.servicePixabay.FeedPixabay;
import com.fullpower.changeit.servicePixabay.Hit;
import com.fullpower.changeit.servicePixabay.PixabayService;
import com.fullpower.changeit.serviceUnSplash.PhotoUnSplash;
import com.fullpower.changeit.serviceUnSplash.UnSplashService;
import com.fullpower.changeit.utils.Utils;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_PROGRESSBAR = 0;
    private boolean isFooterEnabled = true;
    private int tab;
    public static Service500px mService500px;
    public static FlickrService mServiceFlickr;
    private static UnSplashService mUnSplashService;
    private static PixabayService mPixabayService;
    private static AlphaCodersService mAlphaCodersService;

    public static void setDoNotifyDataSetChangedOnce(boolean doNotifyDataSetChangedOnce, int tab) {
        MyRecyclerViewAdapter.doNotifyDataSetChangedOnce[tab] = doNotifyDataSetChangedOnce;
    }

    private static boolean doNotifyDataSetChangedOnce[] = {true, true, true, true};
    private int numberOfCols;
    private static String TAG = "MyRecyclerViewAdapter";
    private Context mContext;
    private PhotoLab mPhotoLab;

    public MyRecyclerViewAdapter(Context context, int numberOfCols, int tab) {
        Log.i(TAG,"RecyclerViewAdapter created! "+Integer.toString(tab));
        mContext = context;
        mPhotoLab = PhotoLab.getPhotoLab(context);
        this.numberOfCols = numberOfCols;
        this.tab = tab;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.500px.com/v1")
                .build();
        mService500px = restAdapter.create(Service500px.class);
        RestAdapter restAdapterSearch = new RestAdapter.Builder()
                .setEndpoint("https://duckduckgo.com/")
                .build();
        RestAdapter restAdapterFlickr = new RestAdapter.Builder()
                .setEndpoint("https://api.flickr.com/services")
                .build();
        mServiceFlickr = restAdapterFlickr.create(FlickrService.class);
        RestAdapter restAdapterUnSplash = new RestAdapter.Builder()
                .setEndpoint("https://api.unsplash.com")
                .build();
        mUnSplashService = restAdapterUnSplash.create(UnSplashService.class);
        RestAdapter restAdapterPixabay = new RestAdapter.Builder()
                .setEndpoint("https://pixabay.com")
                .build();
        mPixabayService = restAdapterPixabay.create(PixabayService.class);
        RestAdapter restAdapterAlpahCoders = new RestAdapter.Builder()
                .setEndpoint("https://wall.alphacoders.com/api2.0")
                .build();
        mAlphaCodersService = restAdapterAlpahCoders.create(AlphaCodersService.class);
    }

    public void loadData500px(String feature, int rrp, String only, int currentPage, String imageSize, String consumerKey, String exclude, Integer tags, final Callback<Feed500px> callback) {
        mService500px.getFeed(feature, rrp, only, currentPage, imageSize, consumerKey, exclude, 1, new Callback<Feed500px>() {
            @Override
            public void success(Feed500px feed, Response response) {
                mPhotoLab.addObjects(feed.photos);
                notifyDataSetChanged();
                MyFragmentStatePagerAdapter.setNotifyDataSetChanged(true);
                setDoNotifyDataSetChangedOnce(true, tab);
                callback.success(feed, response);
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.i("PhotoLab", error.toString());
                //callback.failure(error);
            }
        });
    }

    public void loadData500pxSearch(String term, int rrp, int currentPage, String imageSize, String consumerKey, String exclude, Integer tags, String sort, final Callback<Feed500px> callback) {
        mService500px.getFeedSearch(term, rrp, currentPage, imageSize, consumerKey, exclude, 1, sort, new Callback<Feed500px>() {
            @Override
            public void success(Feed500px feed, Response response) {
                mPhotoLab.addObjects(feed.photos);
                notifyDataSetChanged();
                MyFragmentStatePagerAdapter.setNotifyDataSetChanged(true);
                setDoNotifyDataSetChangedOnce(true, tab);
                callback.success(feed, response);
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.i("PhotoLab", error.toString());
                //callback.failure(error);
            }
        });
    }

    public void loadDataPixabay(String key, String imageType, boolean editorsChoice, String order, boolean safeSearch, int page, int perPage, final Callback<FeedPixabay> callback) {
        mPixabayService.getFeed(key, imageType, editorsChoice, order, safeSearch, page, perPage, new Callback<FeedPixabay>() {
            @Override
            public void success(FeedPixabay feed, Response response) {
                mPhotoLab.addObjects(feed.hits);
                //Log.i(TAG + "YYYY", Integer.toString(feed.hits.size()));
                notifyDataSetChanged();
                MyFragmentStatePagerAdapter.setNotifyDataSetChanged(true);
                setDoNotifyDataSetChangedOnce(true, tab);
                callback.success(feed, response);
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.i("PhotoLab", error.toString());
                //callback.failure(error);
            }
        });
    }

    public void loadDataUnSplash(String clientId, String perPage, final String currentPage, final Callback<List<PhotoUnSplash>> callback) {
        mUnSplashService.getFeed(clientId, perPage, currentPage, new Callback<List<PhotoUnSplash>>() {
            @Override
            public void success(List<PhotoUnSplash> feed, Response response) {
                mPhotoLab.addObjects(feed);
                //Log.i(TAG + "XXXX" + currentPage, Integer.toString(feed.size()));
                notifyDataSetChanged();
                MyFragmentStatePagerAdapter.setNotifyDataSetChanged(true);
                setDoNotifyDataSetChangedOnce(true, tab);
                callback.success(feed, response);
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.i("PhotoLab", error.toString());
                //callback.failure(error);
            }
        });
    }

    public void loadDataAlphaCoders(String key, String method, String info_level, Integer page, String check_last, final Callback<FeedAlphaCoders> callback) {
        mAlphaCodersService.getFeed(key, method, info_level, page, check_last, new Callback<FeedAlphaCoders>() {
            @Override
            public void success(FeedAlphaCoders feed, Response response) {
                if (feed.wallpapers != null)
                    mPhotoLab.addObjects(feed.wallpapers);
                //Log.i(TAG,"alphacoders "+Integer.toString(feed.wallpapers.size()));
                notifyDataSetChanged();
                MyFragmentStatePagerAdapter.setNotifyDataSetChanged(true);
                setDoNotifyDataSetChangedOnce(true, tab);
                callback.success(feed, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    //boolean b is for deciding whether to load data for the given tab or not
    public void loadFlickrData(String method, String key, String date, String extras, String format, String per_page,
                               String page, String nonjsoncallback, final Callback<FeedFlickr> callback) {

        mServiceFlickr.getFeed(method, key, date, extras, format, per_page, page, nonjsoncallback, new Callback<FeedFlickr>() {
            @Override
            public void success(FeedFlickr feed, Response response) {
                EndlessRecyclerOnScrollListener.loading = false;
                //Log.i(TAG,"Flickr loaded");
                if (feed.photos != null) {
                    mPhotoLab.addObjects(feed.photos.photo);
                }
                notifyDataSetChanged();
                MyFragmentStatePagerAdapter.setNotifyDataSetChanged(true);
                setDoNotifyDataSetChangedOnce(true, tab);
                callback.success(feed, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    //boolean b is for deciding whether to load data for the given tab or not
    public void loadFlickrDataSearch(String method, String key, String text, String extras, String format, String per_page,
                                     String page, String sort, String nonjsoncallback, final Callback<FeedFlickr> callback) {

        mServiceFlickr.getFeedSearch(method, key, text, extras, format, per_page, page, sort, nonjsoncallback, new Callback<FeedFlickr>() {
            @Override
            public void success(FeedFlickr feed, Response response) {
                EndlessRecyclerOnScrollListener.loading = false;
                //Log.i(TAG,"Flickr Search loaded");
                if (feed.photos != null) {
                    mPhotoLab.addObjects(feed.photos.photo);
                }
                notifyDataSetChanged();
                MyFragmentStatePagerAdapter.setNotifyDataSetChanged(true);
                setDoNotifyDataSetChangedOnce(true, tab);
                callback.success(feed, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public GridLayout progressBar;
        public ProgressBar mProgressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (GridLayout) v.findViewById(R.id.progress_bar);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = mProgressBar.getMeasuredHeight();
            int width = displaymetrics.widthPixels;
        }
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        public ImageView mTargetImageView;
        private Photo500px mPhoto500px;
        private PhotoFlickr mPhotoFlickr;
        private PhotoUnSplash mPhotoUnSplash;
        private Hit mPhotoPixabay;
        private Wallpaper mWallpaper;
        private int position;
        private String url;

        public ImageView getTargetImageView() {
            return mTargetImageView;
        }

        public DataObjectHolder(View itemView) {
            super(itemView);
            mTargetImageView = (ImageView) itemView.findViewById(R.id.card_image);
            Point p = new Point();
            Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            display.getSize(p);
            mTargetImageView.setLayoutParams(new RelativeLayout.LayoutParams(p.x / numberOfCols, p.x / numberOfCols));
            mTargetImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //mTargetImageView.setPlaceholder(ContextCompat.getDrawable(mContext, R.drawable.pattern_repeat));
            //mTargetImageView.setShowDownloadProgressIndicator(false);
            mTargetImageView.setOnClickListener(this);
            //Log.i(TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        public PhotoFlickr getPhotoFlickr() {
            return mPhotoFlickr;
        }

        public Photo500px getPhoto500px() {
            return mPhoto500px;
        }

        public PhotoUnSplash getPhotoUnSplash() {
            return mPhotoUnSplash;
        }

        public Hit getPhotoPixabay() {
            return mPhotoPixabay;
        }

        public Wallpaper getPhotoAlphaCoders() {
            return mWallpaper;
        }

        @Override
        public void onClick(View v) {
            Intent intent = WallpaperPagerActivity.newIntent(mContext, position, tab);
            mContext.startActivity(intent);
        }

        public void setPhoto(Photo500px photo) {
            mPhoto500px = photo;
        }

        public void setPhoto(PhotoFlickr photo) {
            mPhotoFlickr = photo;
        }

        public void setPhoto(PhotoUnSplash photo) {
            mPhotoUnSplash = photo;
        }

        public void setPhoto(Wallpaper photo) {
            mWallpaper = photo;
        }

        public void setPhoto(Hit photo) {
            mPhotoPixabay = photo;
        }

        public void setPosition(int pos) {
            position = pos;
        }
    }

    @Override
    public int getItemCount() {
        int size = mPhotoLab.getSize(tab);
        if (doNotifyDataSetChangedOnce[tab]) {
            doNotifyDataSetChangedOnce[tab]= false;
            try {
                notifyDataSetChanged();
            } catch (Exception e) {
            }
        }
        return (isFooterEnabled) ? size + 1 : size;
    }

    @Override
    public int getItemViewType(int position) {
        //Log.i(TAG,Integer.toString(position));
        return (isFooterEnabled && position >= mPhotoLab.getSize(tab)) ? VIEW_TYPE_PROGRESSBAR : VIEW_TYPE_ITEM;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_view_list_linear, parent, false);
            vh = new DataObjectHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progressbar, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int t = mPhotoLab.getSize(tab);
        //Log.i(TAG,Integer.toString(t));
        if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).mProgressBar.setIndeterminate(true);
        } else if ((t > 0 && position < t) && holder instanceof DataObjectHolder) {
            Log.i(TAG, Integer.toString(position));
            ((DataObjectHolder) holder).setPosition(position);
            final ImageViewAware imageAware = new ImageViewAware(((DataObjectHolder) holder).mTargetImageView, false);
            Object ob = mPhotoLab.getNextPhoto(tab, position);
            int isInstanceOf = Utils.isInstanceOf(ob);
            switch (isInstanceOf) {
                case 0:
                    ((DataObjectHolder) holder).setPhoto((Photo500px) ob);
                    AppApplication.loadImageByPath(((DataObjectHolder) holder).getPhoto500px().images.get(1).url, imageAware, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            ImageView imageView = (ImageView) view;
                            imageView.setImageBitmap(loadedImage);
                            Log.i(TAG, "Image Loaded 0");
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                    break;
                case 1:
                    ((DataObjectHolder) holder).setPhoto((PhotoFlickr) ob);
                    AppApplication.loadImageByPath(((DataObjectHolder) holder).getPhotoFlickr().url_m, imageAware, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            ImageView imageView = (ImageView) view;
                            imageView.setImageBitmap(loadedImage);
                            Log.i(TAG, "Image Loaded 1");
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                    break;
                case 2:
                    ((DataObjectHolder) holder).setPhoto((PhotoUnSplash) ob);
                    AppApplication.loadImageByPath(((DataObjectHolder) holder).getPhotoUnSplash().urls.small, imageAware, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            ImageView imageView = (ImageView) view;
                            imageView.setImageBitmap(loadedImage);
                            Log.i(TAG, "Image Loaded 2");
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                    break;
                case 3:
                    ((DataObjectHolder) holder).setPhoto((Hit) ob);
                    //Log.i(TAG, ((DataObjectHolder) holder).getPhotoPixabay().webformatURL);
                    AppApplication.loadImageByPath(((DataObjectHolder) holder).getPhotoPixabay().webformatURL, imageAware, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            ImageView imageView = (ImageView) view;
                            imageView.setImageBitmap(loadedImage);
                            Log.i(TAG, "Image Loaded 3");
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                    break;
                case 4:
                    ((DataObjectHolder) holder).setPhoto((Wallpaper) ob);
                    String urlThumb = ((DataObjectHolder) holder).getPhotoAlphaCoders().url_thumb;
                    urlThumb.replaceAll("15.1", "15.2");
                    AppApplication.loadImageByPath(urlThumb, imageAware, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            ImageView imageView = (ImageView) view;
                            imageView.setImageBitmap(loadedImage);
                            Log.i(TAG, "Image Loaded 4");
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                    break;
                default:
                    //Log.i(TAG, "Null returned");

            }
        }
    }

}