// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.fullpower.changeit;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.adapter.IdentityAdapter;
import com.theartofdev.fastimageloader.adapter.ImgIXAdapter;
import com.fullpower.changeit.utils.Specs;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.StringTokenizer;

public class AppApplication extends Application {
    public static final int INSTAGRAM_IMAGE_SIZE = 360;
    public static final int INSTAGRAM_AVATAR_SIZE = 60;
    public static boolean mPrefetchImages;
    public static boolean mAutoChangeWallpaper;
    public static String mLastCategoriesAt0;
    public static String mTimeIntevalVal;
    public static String mTimeIntetvalSpinner;
    public static boolean allowSaving = false;
    public static Long timeInterval = 3600 * 24l;
    public static String showAds = "false";
    public static boolean boookmarkShown = true;
    public static int radioChoice=0;
    public static boolean configDataLoaded = false;
    public static String infoUrl = "http://ec2-52-23-186-182.compute-1.amazonaws.com";
    public static List<Integer> mList;
    private String TAG = "AppApplication";
    public static int width;
    public static int height;
    private static ImageSize imageSize;
    private static ImageSize imageSizeLarge;
    private static DisplayImageOptions sDisplayImageOptions;
    private static ImageLoader sImageLoader;
    private static ImageLoaderConfiguration sImageLoaderConfiguration;
    public static Context mContext;
    public static int currentEndpointIndex = 0;
    public static boolean isPermisiiongranted = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        height = metrics.heightPixels;
        width = metrics.widthPixels;
        sDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .considerExifParams(true)
                .showImageForEmptyUri(R.drawable.pattern_dark)
                .showImageOnFail(R.drawable.pattern_dark)
                .showImageOnLoading(R.drawable.pattern_dark)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .postProcessor(null)
                //.resetViewBeforeLoading(true)
                .displayer(new FadeInBitmapDisplayer(100)).build();

        sImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .threadPriority(Thread.MAX_PRIORITY)
                .threadPoolSize(5)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(sDisplayImageOptions)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSize(2*1024*1024)
                .diskCacheSize(20 * 1024 * 1024).build();

        sImageLoader = ImageLoader.getInstance();
        sImageLoader.init(sImageLoaderConfiguration);
        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mPrefetchImages = preferences.getBoolean("prefetch", true);
        mTimeIntevalVal = preferences.getString("intervalVal", "1");
        mTimeIntetvalSpinner = preferences.getString("intervalspinner", "day");
        mAutoChangeWallpaper = preferences.getBoolean("autoChangeWallpaper", false);
        allowSaving = preferences.getBoolean("allowSaving", true);
        mLastCategoriesAt0 = preferences.getString("lastCategory", "");
        timeInterval = preferences.getLong("interval", 24 * 3600);
        configDataLoaded = preferences.getBoolean("ispermissiongranted", false);
        infoUrl = preferences.getString("infoUrl", "http://ec2-52-23-186-182.compute-1.amazonaws.com");
        currentEndpointIndex = preferences.getInt("currentEndpointIndex", 0);
        showAds = preferences.getString("showAds", "false");
        boookmarkShown = preferences.getBoolean("isBookMarkShown", true);
        radioChoice=preferences.getInt("radio",0);
        FastImageLoader
                .init(this)
                .setDefaultImageServiceAdapter(new ImgIXAdapter())
                .setWriteLogsToLogcat(true)
                .setLogLevel(Log.DEBUG)
                .setDebugIndicator(false);

        FastImageLoader.buildSpec(Specs.IMG_IX_UNBOUNDED)
                .setUnboundDimension()
                .setPixelConfig(Bitmap.Config.RGB_565)
                .build();
        imageSize = new ImageSize(width/2, width/2);
        imageSizeLarge=new ImageSize(width,width);
        IdentityAdapter identityUriEnhancer = new IdentityAdapter();
        //Log.i(TAG, Integer.toString(height) + Integer.toString(width));
        FastImageLoader.buildSpec(Specs.IMG_IX_IMAGE)
                .setDimensionByDisplay()
                .setHeightByResource(R.dimen.image_height)
                .setWidthByResource(R.dimen.image_height)
                .setPixelConfig(Bitmap.Config.RGB_565)
                .build();

        FastImageLoader.buildSpec(Specs.INSTA_AVATAR)
                .setDimension(INSTAGRAM_AVATAR_SIZE)
                .setImageServiceAdapter(identityUriEnhancer)
                .build();

        FastImageLoader.buildSpec(Specs.INSTA_IMAGE)
                .setDimension(width)
                .setPixelConfig(Bitmap.Config.ARGB_8888)
                .setImageServiceAdapter(identityUriEnhancer)
                .build();

        FastImageLoader.buildSpec(Specs.UNBOUNDED_MAX)
                .setUnboundDimension()
                .setMaxDensity()
                .build();

    }

    public static void loadImageByPath(String path, ImageViewAware image, ImageLoadingListener listener) {
        sImageLoader.cancelDisplayTask(image);
        image.getWrappedView().clearAnimation();
        image.setImageDrawable(null);
        final ImageSize size = ImageSizeUtils.defineTargetSizeForView(image, imageSize);
        final String cacheKey = MemoryCacheUtils.generateKey(path, size);
        List<Bitmap> cachedBitmaps = MemoryCacheUtils.findCachedBitmapsForImageUri(cacheKey, sImageLoader.getMemoryCache());
        if (cachedBitmaps.size() > 0) {
            final Bitmap bitmap = cachedBitmaps.get(0);
            // Yep, sometimes it is null
            if (bitmap != null) {
                if (listener != null) {
                    listener.onLoadingComplete(path, image.getWrappedView(), bitmap);
                }
                return;
            }
        }
        sImageLoader.displayImage(path, image, sDisplayImageOptions);
    }

    public static void loadImageByPathLarge(String path, ImageViewAware image, ImageLoadingListener listener) {
        sImageLoader.cancelDisplayTask(image);
        image.getWrappedView().clearAnimation();
        image.setImageDrawable(null);
        final ImageSize size = ImageSizeUtils.defineTargetSizeForView(image, imageSizeLarge);
        final String cacheKey = MemoryCacheUtils.generateKey(path, size);
        List<Bitmap> cachedBitmaps = MemoryCacheUtils.findCachedBitmapsForImageUri(cacheKey, sImageLoader.getMemoryCache());
        if (cachedBitmaps.size() > 0) {
            final Bitmap bitmap = cachedBitmaps.get(0);
            // Yep, sometimes it is null
            if (bitmap != null) {
                if (listener != null) {
                    listener.onLoadingComplete(path, image.getWrappedView(), bitmap);
                }
                image.setImageBitmap(bitmap);
                return;
            }
        }
        sImageLoader.displayImage(path, image, sDisplayImageOptions);
    }

/*    public static void loadImageByPath(String path,final ImageViewAware image) {
        sImageLoader.cancelDisplayTask(image);
        image.getWrappedView().clearAnimation();
        image.setImageDrawable(null);
        final ImageSize size = ImageSizeUtils.defineTargetSizeForView(image, imageSize);
        sImageLoader.getInstance().displayImage(path, image, sDisplayImageOptions,new SimpleImageLoadingListener() {
            boolean cacheFound=false;
            @Override
            public void onLoadingStarted(String url, View view) {
                final String cacheKey = MemoryCacheUtils.generateKey(url, size);
               List<Bitmap> cachedBitmaps = MemoryCacheUtils.findCachedBitmapsForImageUri(cacheKey, sImageLoader.getMemoryCache());
                cacheFound=!cachedBitmaps.isEmpty();
                if (!cacheFound) {
                    //final Bitmap bitmap = cachedBitmaps.get(0);
                    File diskCache = DiskCacheUtils.findInCache(cacheKey, sImageLoader.getDiskCache());
                    // Yep, sometimes it is null
                    if (diskCache != null) {
                        cacheFound= diskCache.exists();
                    }
                }
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (cacheFound) {
                    ImageView imageView=(ImageView)view;
                    imageView.setImageBitmap(loadedImage);
                }
                else
                    sImageLoader.displayImage(imageUri, (ImageView) view,sDisplayImageOptions,null);
            }
        });
    }*/

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}

