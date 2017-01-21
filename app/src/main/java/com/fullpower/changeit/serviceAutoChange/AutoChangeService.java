package com.fullpower.changeit.serviceAutoChange;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.fullpower.changeit.AppApplication;
import com.fullpower.changeit.fragments.ExploreFragment;
import com.fullpower.changeit.fragments.InstagramFragment;
import com.fullpower.changeit.model.PhotoLab;
import com.fullpower.changeit.model.PhotoObject;
import com.fullpower.changeit.service500px.Feed500px;
import com.fullpower.changeit.service500px.Service500px;
import com.fullpower.changeit.serviceAlphaCoders.AlphaCodersService;
import com.fullpower.changeit.serviceAlphaCoders.FeedAlphaCoders;
import com.fullpower.changeit.serviceFlickr.FeedFlickr;
import com.fullpower.changeit.serviceFlickr.FlickrService;
import com.fullpower.changeit.servicePageInfo.FeedPageInfo;
import com.fullpower.changeit.servicePageInfo.InfoService;
import com.fullpower.changeit.servicePixabay.FeedPixabay;
import com.fullpower.changeit.servicePixabay.PixabayService;
import com.fullpower.changeit.serviceUnSplash.PhotoUnSplash;
import com.fullpower.changeit.serviceUnSplash.UnSplashService;
import com.fullpower.changeit.utils.PictureUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by OJaiswal153939 on 6/20/2016.
 */
public class AutoChangeService extends IntentService {
    private static final String TAG = "AutoChangeService";
    public static Service500px mService500px;
    public static FlickrService mServiceFlickr;
    private static UnSplashService mUnSplashService;
    private static PixabayService mPixabayService;
    private static AlphaCodersService mAlphaCodersService;
    InfoService mInfoService;
    String url = "";
    public static int rChoice;
    private List<PhotoObject> photoList;
    private List<File> filesFromStorage;
    private PhotoLab mPhotoLab;

    public static Intent newIntent(Context context) {
        return new Intent(context, AutoChangeService.class);
    }

    public AutoChangeService() {
        super(TAG);
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
        RestAdapter restAdapterNotication;
        int index = 0;
        restAdapterNotication = new RestAdapter.Builder()
                .setEndpoint(ExploreFragment.infoEndpoint[index])
                .build();
        mInfoService = restAdapterNotication.create(InfoService.class);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Random random = new Random();
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File myDir = new File(root + "/ChangeIt");
        myDir.mkdirs();
        File db = new File(myDir, "database.txt");
        boolean isPermissionGranted = true;
        if (Build.VERSION.SDK_INT >= 23) {
            isPermissionGranted = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        mInfoService.getImageSearch("db", new Callback<FeedPageInfo>() {
                    @Override
                    public void success(FeedPageInfo feedPageInfo, Response response) {
                        //Log.i(TAG,"API Working "+feedPageInfo.tags.get(0).tagArray);
                        int x = random.nextInt(feedPageInfo.getInfo.size());
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AppApplication.mContext);
                        rChoice=prefs.getInt("radio",0);
                        final PhotoLab mPhotoLab = PhotoLab.getPhotoLab(getApplicationContext());
                        List<PhotoObject> mlist = mPhotoLab.getPhotos();
                        if (rChoice == 1 && mlist.size() > 0) {
                            url = mlist.get(random.nextInt(mlist.size())).url;
                            if (url.contains("localImage")) {
                                SetWallpaperOffline setWallpaper = new SetWallpaperOffline(url.replace("localImage", ""));
                                setWallpaper.execute();
                            } else {
                                SetWallpaper setWallpaper = new SetWallpaper();
                                setWallpaper.execute(url);
                            }

                        } else {
                            if (x == 0) {
                                int m = random.nextInt(2);
                                if (m == 0) {
                                    int n = random.nextInt(ExploreFragment.feature.length);
                                    mService500px.getFeed(ExploreFragment.feature[n], ExploreFragment.perPage500px, ExploreFragment.includeOnly, random.nextInt(5), InstagramFragment.imageSize, ExploreFragment.key500px, ExploreFragment.exclude, 1, new Callback<Feed500px>() {
                                        @Override
                                        public void success(Feed500px feed, Response response) {
                                            url = feed.photos.get(random.nextInt(feed.photos.size())).images.get(3).url;
                                            SetWallpaper setWallpaper = new SetWallpaper();
                                            setWallpaper.execute(url);
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            //Log.i("PhotoLab", error.toString());
                                            //callback.failure(error);
                                        }
                                    });
                                } else {
                                    final int i = random.nextInt(ExploreFragment.termArray500px.length);
                                    mService500px.getFeedSearch(ExploreFragment.termArray500px[i], ExploreFragment.perPage500px, random.nextInt(5), InstagramFragment.imageSize, ExploreFragment.key500px, ExploreFragment.exclude, 1, InstagramFragment.sort, new Callback<Feed500px>() {
                                        @Override
                                        public void success(Feed500px feed, Response response) {
                                            mPhotoLab.addObjects(feed.photos);
                                            url = feed.photos.get(random.nextInt(feed.photos.size())).images.get(3).url;
                                            SetWallpaper setWallpaper = new SetWallpaper();
                                            setWallpaper.execute(url);
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            //Log.i("PhotoLab", error.toString());
                                            //callback.failure(error);
                                        }
                                    });
                                }
                            } else if (x == 1) {
                                int c = random.nextInt(2);
                                if (c == 0) {
                                    Calendar calender = Calendar.getInstance();
                                    int dateDecCounter = 2;
                                    dateDecCounter = 2 + random.nextInt(5);
                                    calender.add(Calendar.DATE, -dateDecCounter);
                                    dateDecCounter++;
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                    String date = df.format(calender.getTime());
                                    mServiceFlickr.getFeed(InstagramFragment.method, ExploreFragment.keyFlickr, InstagramFragment.date, InstagramFragment.extras, InstagramFragment.format, ExploreFragment.perPageFlickr, Integer.toString(random.nextInt(5)), InstagramFragment.nonjsoncallback, new Callback<FeedFlickr>() {
                                        @Override
                                        public void success(FeedFlickr feed, Response response) {
                                            url = feed.photos.photo.get(random.nextInt(feed.photos.photo.size())).url_l;
                                            SetWallpaper setWallpaper = new SetWallpaper();
                                            setWallpaper.execute(url);
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                        }
                                    });
                                } else {
                                    mServiceFlickr.getFeedSearch(InstagramFragment.method, ExploreFragment.keyFlickr, ExploreFragment.termArrayFlickr[random.nextInt(ExploreFragment.termArray500px.length)], InstagramFragment.extras, InstagramFragment.format, ExploreFragment.perPageFlickr, Integer.toString(random.nextInt(5)), InstagramFragment.sortFlickr, InstagramFragment.nonjsoncallback, new Callback<FeedFlickr>() {
                                        @Override
                                        public void success(FeedFlickr feed, Response response) {
                                            url = feed.photos.photo.get(random.nextInt(feed.photos.photo.size())).url_l;
                                            SetWallpaper setWallpaper = new SetWallpaper();
                                            setWallpaper.execute(url);
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                        }
                                    });
                                }
                            } else if (x == 2) {
                                mUnSplashService.getFeed(ExploreFragment.keyUnsplash, ExploreFragment.perPageUnSplash, Integer.toString(random.nextInt(5)), new Callback<List<PhotoUnSplash>>() {
                                    @Override
                                    public void success(List<PhotoUnSplash> feed, Response response) {
                                        url = feed.get(random.nextInt(feed.size())).urls.regular;
                                        SetWallpaper setWallpaper = new SetWallpaper();
                                        setWallpaper.execute(url);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        //Log.i("PhotoLab", error.toString());
                                        //callback.failure(error);
                                    }
                                });
                            } else if (x == 3) {
                                mPixabayService.getFeed(ExploreFragment.keyPixabay, InstagramFragment.image_type, InstagramFragment.editors_choice, InstagramFragment.order, InstagramFragment.safesearch, random.nextInt(5), ExploreFragment.perPagePixabay, new Callback<FeedPixabay>() {
                                    @Override
                                    public void success(FeedPixabay feed, Response response) {
                                        url = feed.hits.get(random.nextInt(feed.hits.size())).webformatURL;
                                        url.replaceAll("(_640)", "_960");
                                        SetWallpaper setWallpaper = new SetWallpaper();
                                        setWallpaper.execute(url);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        //Log.i("PhotoLab", error.toString());
                                        //callback.failure(error);
                                    }
                                });
                            } else if (x == 4) {
                                mAlphaCodersService.getFeed(ExploreFragment.keyAlphaCoders, ExploreFragment.methodAlphaCoders[random.nextInt(ExploreFragment.methodAlphaCoders.length)], InstagramFragment.info_level, random.nextInt(5), InstagramFragment.check_last, new Callback<FeedAlphaCoders>() {
                                    @Override
                                    public void success(FeedAlphaCoders feed, Response response) {
                                        url = feed.wallpapers.get(random.nextInt(feed.wallpapers.size())).url_image;
                                        SetWallpaper setWallpaper = new SetWallpaper();
                                        setWallpaper.execute(url);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        //callback.failure(error);
                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (!isNetworkAvailableAndConnected()) {
                            filesFromStorage = getFilesFromPhotos();
                            mPhotoLab = PhotoLab.getPhotoLab(getApplicationContext());
                            photoList = mPhotoLab.getPhotos();
                            addToList(photoList);
                            Random r=new Random();
                            File mFile=filesFromStorage.get(r.nextInt(filesFromStorage.size()));
                            String path=mFile.getAbsolutePath();
                            SetWallpaperOffline setWallpaper = new SetWallpaperOffline(path);
                            setWallpaper.execute(path);
                            return;
                        }
                    }
                }
        );
        //Log.i(TAG, "Received an intent: " + intent);
    }

    public void addToList(List<PhotoObject> list) {
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i).getUrl();
            //Log.i(TAG, str);
            if (str.contains("localImage")) {
                //Log.i(TAG, str);
                String path = str.replace("localImage", "");
                //Log.i(TAG, path);
                File file = new File(path);
                filesFromStorage.add(file);
            }
            //Log.i(TAG, Integer.toString(filesFromStorage.size()));
        }
        /*for (int i = 0; i < filesFromStorage.size(); i++) {
            String str = filesFromStorage.get(i).getAbsolutePath();
            Log.i(TAG + "xXXX", str);
        }*/

    }

    public List<PhotoObject> removefromList(List<PhotoObject> list) {
        List<PhotoObject> myList = list;
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i).getUrl();
            //Log.i(TAG, str);
            if (str.contains("localImage")) {
                //Log.i(TAG, str);
                myList.remove(i);
            }
            //Log.i(TAG,Integer.toString(filesFromStorage.size()));
        }
        return myList;
    }

    public String readFromJsonFile() {
        String contents = "";
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File myDir = new File(root + "/ChangeIt");
        myDir.mkdirs();
        File db = new File(myDir, "database.txt");
        try {
            if (db.exists()) {
                int length = (int) db.length();
                byte[] bytes = new byte[length];
                FileInputStream in = new FileInputStream(db);
                try {
                    in.read(bytes);
                } finally {
                    in.close();
                }
                contents = new String(bytes);
            }
        } catch (Exception e) {
            //Log.i(TAG, e.toString());
        }
        return contents;
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)
                        getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() !=
                null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

    public static void setServiceAlarm(Context context, long timeInteval, boolean isOn) {
        Intent i = AutoChangeService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i,
                0);
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), timeInteval,
                    pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = AutoChangeService.newIntent(context);
        PendingIntent pi = PendingIntent
                .getService(context, 0, i,
                        0);
        return pi != null;
    }

    public static List<File> getFilesFromPhotos() {
        String state = Environment.getExternalStorageState();

        if (state.contentEquals(Environment.MEDIA_MOUNTED) || state.contentEquals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File file = new File(root + "/ChangeIt");
            File[] photoFiles = file.listFiles();
            List<File> list = new ArrayList<File>();
            if (photoFiles != null)
                list = new ArrayList<>(Arrays.asList(photoFiles));
           /* for(int i=0;i<photoFiles.length;i++)
            {
                Log.i(TAG,photoFiles[i].getAbsolutePath());
            }*/
            return list;
        } else {
            Log.v("Error", "External Storage Unaccessible: " + state);
            File[] files = new File[0];
            return Arrays.asList(files);
        }
    }

    public class SetWallpaper extends AsyncTask<String, Void, Bitmap> {
        int height;
        int width;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageURL = params[0];
            DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
            height = metrics.heightPixels;//(metrics.densityDpi/160);
            width = metrics.widthPixels * 2;//(metrics.densityDpi/160);
            Bitmap bmp = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            options.outHeight = height;
            options.outWidth = width;
            try {
                URL ulrn = new URL(imageURL);
                HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
                InputStream is = con.getInputStream();
                int x = 5;
                bmp = PictureUtils.getScaledBitmap(is, width, height, ulrn, x);
                //Log.i(TAG, url);
                if (bmp == null) {
                    //Log.i(TAG, "bmp Null");
                    return bmp;
                }
            } catch (Exception e) {
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Bitmap bitmap = result;
            if (null != result) {
                try {
                    WallpaperManager myWallpaperManager = WallpaperManager
                            .getInstance(getApplicationContext());
                    myWallpaperManager.setWallpaperOffsetSteps(1, 1);
                    myWallpaperManager.suggestDesiredDimensions(width, height);
                    myWallpaperManager.setBitmap(bitmap);
                    //Toast.makeText(getApplicationContext(), "Wallpaper set", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(),"Error setting Wallpaper", Toast.LENGTH_SHORT).show();
                }
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    class SetWallpaperOffline extends AsyncTask<String, Void, Bitmap> {
        int height;
        int width;
        String url;

        SetWallpaperOffline(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageURL = params[0];
            DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
            height = metrics.heightPixels;//(metrics.densityDpi/160);
            width = metrics.widthPixels * 2;//(metrics.densityDpi/160);
            File f = new File(imageURL);
            Bitmap bitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Bitmap bitmap = result;
            if (null != result) {
                try {
                    WallpaperManager myWallpaperManager = WallpaperManager
                            .getInstance(getApplicationContext());
                    myWallpaperManager.setWallpaperOffsetSteps(1, 1);
                    myWallpaperManager.suggestDesiredDimensions(width, height);
                    myWallpaperManager.setBitmap(bitmap);
                    Toast.makeText(getApplicationContext(), "Wallpaper set",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            e.toString(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }
}
