package com.fullpower.changeit.serviceAutoChange;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.BoolRes;
import android.support.annotation.IntegerRes;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.fullpower.changeit.R;
import com.fullpower.changeit.activities.MainActivity;
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
import com.fullpower.changeit.servicePageInfo.PageInfo;
import com.fullpower.changeit.servicePixabay.FeedPixabay;
import com.fullpower.changeit.servicePixabay.PixabayService;
import com.fullpower.changeit.serviceUnSplash.PhotoUnSplash;
import com.fullpower.changeit.serviceUnSplash.UnSplashService;
import com.fullpower.changeit.utils.PictureUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
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
public class NotificationService extends IntentService {
    private static final String TAG = "NotificationService";
    InfoService mInfoService;
    String url = "";
    String searchKey;
    public static Service500px mService500px;
    public static FlickrService mServiceFlickr;
    private static UnSplashService mUnSplashService;
    private static PixabayService mPixabayService;
    private static AlphaCodersService mAlphaCodersService;
    //String tagArray[] = ExploreFragment.tagArrayI;
    private Context mContext;
    private Bitmap mBitmap = null;

    public static Intent newIntent(Context context) {
        return new Intent(context, NotificationService.class);
    }

    public NotificationService() {
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
        //final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //int index = prefs.getInt("currentEndpointIndex", 0);
        restAdapterNotication = new RestAdapter.Builder()
                .setEndpoint(ExploreFragment.infoEndpoint[0])
                .build();
        mInfoService = restAdapterNotication.create(InfoService.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /*if (!isNetworkAvailableAndConnected()) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean isConfigurationsLoaded = prefs.getBoolean("configDataLoaded", false);
            if (isConfigurationsLoaded) {
                prefs.edit().putBoolean("configDataLoaded", false).apply();
            }
            return;
        }*/
        //final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //final String lastCategoryString = prefs.getString("lastCategory", "");
        final Random random = new Random();
        mInfoService.getImageSearch("db", new Callback<FeedPageInfo>() {
            @Override
            public void success(FeedPageInfo feedPageInfo, Response response) {
                //updateAPIParams(feedPageInfo);
                boolean isPermissionGranted = true;
                if (Build.VERSION.SDK_INT >= 23) {
                    isPermissionGranted = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED;
                }
                if (isPermissionGranted) {
                    FetchItemsTask fetchItemsTask = new FetchItemsTask();
                    fetchItemsTask.execute();
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
        int x = random.nextInt(5);
        final PhotoLab mPhotoLab = PhotoLab.getPhotoLab(getApplicationContext());
        if (x == 0) {
            int m = random.nextInt(2);
            if (m == 0) {
                int n = random.nextInt(ExploreFragment.feature.length);
                mService500px.getFeed(ExploreFragment.feature[n], ExploreFragment.perPage500px, ExploreFragment.includeOnly, random.nextInt(20), InstagramFragment.imageSize, ExploreFragment.key500px, ExploreFragment.exclude, 1, new Callback<Feed500px>() {
                    @Override
                    public void success(Feed500px feed, Response response) {
                        List<PhotoObject> mlist = mPhotoLab.getPhotos();
                        url = feed.photos.get(random.nextInt(feed.photos.size())).images.get(3).url;
                        SetNotification task = new SetNotification();
                        task.execute(url);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //Log.i("PhotoLab", error.toString());
                        //callback.failure(error);
                    }
                });
            } else {
                final int i = random.nextInt(ExploreFragment.termArray500px.length);
                mService500px.getFeedSearch(ExploreFragment.termArray500px[i], ExploreFragment.perPage500px, random.nextInt(20), InstagramFragment.imageSize, ExploreFragment.key500px, ExploreFragment.exclude, 1, InstagramFragment.sort, new Callback<Feed500px>() {
                    @Override
                    public void success(Feed500px feed, Response response) {
                        mPhotoLab.addObjects(feed.photos);
                        url = feed.photos.get(random.nextInt(feed.photos.size())).images.get(3).url;
                        SetNotification task = new SetNotification();
                        task.execute(url);
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
                dateDecCounter = 2 + random.nextInt(20);
                calender.add(Calendar.DATE, -dateDecCounter);
                dateDecCounter++;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String date = df.format(calender.getTime());
                mServiceFlickr.getFeed(InstagramFragment.method, ExploreFragment.keyFlickr,
                        InstagramFragment.date, InstagramFragment.extras, InstagramFragment.format,
                        ExploreFragment.perPageFlickr, Integer.toString(random.nextInt(5)),
                        InstagramFragment.nonjsoncallback, new Callback<FeedFlickr>() {
                            @Override
                            public void success(FeedFlickr feed, Response response) {
                                url = feed.photos.photo.get(random.nextInt(feed.photos.photo.size())).url_l;
                                SetNotification task = new SetNotification();
                                task.execute(url);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                            }
                        });
            } else {
                mServiceFlickr.getFeedSearch(InstagramFragment.method, ExploreFragment.keyFlickr,
                        ExploreFragment.termArrayFlickr[random.nextInt(ExploreFragment.termArrayFlickr.length)],
                        InstagramFragment.extras, InstagramFragment.format, ExploreFragment.perPageFlickr,
                        Integer.toString(random.nextInt(5)), InstagramFragment.sortFlickr,
                        InstagramFragment.nonjsoncallback, new Callback<FeedFlickr>() {
                            @Override
                            public void success(FeedFlickr feed, Response response) {
                                url = feed.photos.photo.get(random.nextInt(feed.photos.photo.size())).url_l;
                                SetNotification task = new SetNotification();
                                task.execute(url);
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
                    SetNotification task = new SetNotification();
                    task.execute(url);
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
                    SetNotification task = new SetNotification();
                    task.execute(url);
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
                    SetNotification task = new SetNotification();
                    task.execute(url);
                }

                @Override
                public void failure(RetrofitError error) {
                    //callback.failure(error);
                }
            });
        }
        //Log.i(TAG, "Received an intent Notification: ");
    }

    /*public void updateAPIParams(FeedPageInfo feedPageInfo) {
        for (int i = 0; i < feedPageInfo.getInfo.size(); i++) {
            PageInfo pageInfo = feedPageInfo.getInfo.get(i);
            switch (i) {
                case 0:
                    ExploreFragment.perPage500px = Integer.parseInt(pageInfo.perPage);
                    ExploreFragment.key500px = pageInfo.key;
                    ExploreFragment.termArray500px = pageInfo.termArray.split("\\-");
                    ExploreFragment.feature = pageInfo.feature.split("\\-");
                    ExploreFragment.includeOnly = pageInfo.include.replaceAll("-", ",");
                    ExploreFragment.exclude = pageInfo.exclude.replaceAll("-", ",");
                    break;
                case 1:
                    ExploreFragment.perPageUnSplash = pageInfo.perPage;
                    ExploreFragment.keyUnsplash = pageInfo.key;
                    break;
                case 2:
                    ExploreFragment.perPagePixabay = Integer.parseInt(pageInfo.perPage);
                    ExploreFragment.keyPixabay = pageInfo.key;
                    break;
                case 3:
                    ExploreFragment.keyAlphaCoders = pageInfo.key;
                    break;
                case 4:
                    ExploreFragment.perPageFlickr = pageInfo.perPage;
                    ExploreFragment.keyFlickr = pageInfo.key;
                    ExploreFragment.termArrayFlickr = pageInfo.termArray.split("\\-");
                    break;
                default:
                    break;
            }
        }
        ExploreFragment.maxCountOfPhotosApi= Integer.parseInt(feedPageInfo.tags.get(0).maxCountOfPhotosApi);
        ExploreFragment.showAds= feedPageInfo.tags.get(0).showAds;
        ExploreFragment.infoEndpoint=feedPageInfo.tags.get(0).links.split("\\s");
    }*/

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

    public static void setServiceAlarm(Context context, long timeInterval, boolean isOn) {
        Intent i = NotificationService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 1, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), timeInterval,
                    pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = NotificationService.newIntent(context);
        PendingIntent pi = PendingIntent
                .getService(context, 1, i,
                        PendingIntent.FLAG_NO_CREATE);
        //Log.i(TAG, Boolean.toString(pi != null));
        return pi != null;
    }

    public class SetNotification extends AsyncTask<String, Void, Bitmap> {
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
            height = 200;//(metrics.densityDpi/160);
            width = metrics.widthPixels;//(metrics.densityDpi/160);
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
                //Log.i(TAG, Integer.toString(width) + " " + Integer.toString(height));
                bmp = PictureUtils.getScaledBitmap(is, width, height, ulrn, x);
                //Log.i(TAG, url);
                if (null == bmp) {
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
                    Resources resources = getResources();
                    Intent i = MainActivity.newIntent(getApplicationContext());
                    //PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 2, i, 0);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(i);
                    PendingIntent pi = stackBuilder.getPendingIntent(2, PendingIntent.FLAG_UPDATE_CURRENT);
                    //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Notification notification = new
                            NotificationCompat.Builder(getApplicationContext())
                            .setTicker(resources.getString(R.string.new_pictures_title))
                            .setSmallIcon(android.R.drawable.ic_menu_report_image)
                            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                    R.drawable.wallpaper))
                            .setContentTitle(resources.getString(R.string.new_pictures_title))
                            .setContentText(resources.getString(R.string.new_pictures_text))
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigPictureStyle()
                                    .setSummaryText(resources.getString(R.string.new_pictures_text))
                                    .bigPicture(bitmap))
                            //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .build();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        int smallIconViewId = getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());

                        if (smallIconViewId != 0) {
                            if (notification.contentIntent != null)
                                notification.contentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                            if (notification.headsUpContentView != null)
                                notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                            if (notification.bigContentView != null)
                                notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
                        }
                    }
                    NotificationManagerCompat notificationManager =
                            NotificationManagerCompat.from(getApplicationContext());
                    notificationManager.notify(0, notification);
                } catch (Exception e) {
                }
            }
            try {
            } catch (Exception e) {
                //Log.i(TAG, e.toString());
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
        }

        public String fetchItems(int index) {
            String jsonString = "";
            if (index >= ExploreFragment.infoEndpoint.length)
                return jsonString;
            try {
                String url =
                        Uri.parse(ExploreFragment.infoEndpoint[index] + "/db/")
                                .buildUpon()
                                .appendQueryParameter("db",
                                        "db")
                                .build().toString();
                jsonString = getUrlString(url);
                //Log.i(TAG, "Received JSON at : " +Integer.toString(index)+jsonString);
            } catch (Exception ioe) {
                Log.e(TAG, "Failed to fetch items", ioe);
            }
            if (jsonString.compareTo("") == 0) {
                return fetchItems(++index);
            }
            return jsonString;
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() !=
                    HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage()
                        +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
            File myDir = new File(root + "/ChangeIt");
            myDir.mkdirs();
            File db = new File(myDir, "database.txt");
            String result = fetchItems(0);
            FileOutputStream oFile;
            if (result.compareTo("") != 0) {
                try {

                    if (!db.exists()) {
                        db.createNewFile();
                        //Log.i(TAG, "file created" + db.getAbsolutePath());
                    }
                    oFile = new FileOutputStream(db, false);
                    try {
                        oFile.write(result.getBytes());
                    } finally {
                        oFile.close();
                    }
                } catch (Exception e) {
                    //Log.i(TAG, e.toString());
                }
            }
            /*root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
            File file = new File(root + "/ChangeIt");
            File[] photoFiles = file.listFiles();
            for(int i=0;i<photoFiles.length;i++)
            {
                Log.i(TAG,photoFiles[i].getAbsolutePath());
            }*/
            return null;
        }

    }

    public String fetchItems(int index) {
        String jsonString = "";
        if (index >= ExploreFragment.infoEndpoint.length)
            return jsonString;
        try {
            String url =
                    Uri.parse(ExploreFragment.infoEndpoint[index] + "/db/")
                            .buildUpon()
                            .appendQueryParameter("db",
                                    "db")
                            .build().toString();
            jsonString = getUrlString(url);
            //Log.i(TAG, "Received JSON at : " +Integer.toString(index)+jsonString);
        } catch (Exception ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        if (jsonString.compareTo("") == 0) {
            return fetchItems(++index);
        }
        return jsonString;
    }

}
