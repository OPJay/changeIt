package com.fullpower.changeit.activities;

/**
 * Created by OJaiswal153939 on 3/3/2016.
 */

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fullpower.changeit.AppApplication;
import com.fullpower.changeit.model.PhotoLab;
import com.fullpower.changeit.model.PhotoObject;
import com.fullpower.changeit.serviceAutoChange.NotificationService;
import com.fullpower.changeit.R;
import com.fullpower.changeit.fragments.ExploreFragment;
import com.fullpower.changeit.fragments.SettingsFragment;
import com.fullpower.changeit.servicePageInfo.FeedPageInfo;
import com.fullpower.changeit.servicePageInfo.InfoService;
import com.fullpower.changeit.servicePageInfo.PageInfo;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {
    public static boolean inflated = false;
    private static String TAG = "MainActivity";
    private Toolbar mToolbar;
    private int PICK_IMAGE_REQUEST = 1;
    Menu mMenu;
    private Uri uri;
    private NavigationView mNavigationView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    ExploreFragment fragmentExplore = null;
    SettingsFragment mSettingsFragment = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction, mFragmentTransaction;
    public static Context mContentA;
    private Activity mActivity = null;
    private long daysToReapeatNotification = 2l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //isStoragePermissionGranted();
        boolean b = NotificationService.isServiceAlarmOn(AppApplication.mContext);
        if (!b)
            NotificationService.setServiceAlarm(AppApplication.mContext, daysToReapeatNotification * 24 * 60 * 60 * 1000l, true);
        mActivity = this;
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        RelativeLayout mRelativelayout = (RelativeLayout) mNavigationView.getHeaderView(0).findViewById(R.id.header);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
        TextView textView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        //textView.setTypeface(custom_font);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentExplore = new ExploreFragment();
        fragmentTransaction.add(R.id.container_body_explore, fragmentExplore);
        fragmentTransaction.commit();
        mContentA = this;
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        //navigationView.setCheckedItem(R.id.image_spec);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                String title = getString(R.string.app_name);
                fragmentExplore = (ExploreFragment) fragmentManager.findFragmentById(R.id.container_body_explore);
                mSettingsFragment = (SettingsFragment) fragmentManager.findFragmentById(R.id.container_body_settings);
                //Closing drawer on item click
                drawerLayout.closeDrawers();
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.explore:
                        navigationView.getMenu().getItem(0).setChecked(true);
                        if (fragmentExplore == null) {
                            fragmentExplore = new ExploreFragment();
                            fragmentTransaction.add(R.id.container_body_explore, fragmentExplore);
                        }
                        if (mSettingsFragment != null) {
                            if (!mSettingsFragment.isHidden()) {
                                fragmentTransaction.hide(mSettingsFragment);
                            }
                        }
                        fragmentTransaction.commit();
                        title = getString(R.string.title_discover);
                        getSupportActionBar().setTitle(title);
                        return true;
                    case R.id.favorite:
                        navigationView.getMenu().getItem(1).setChecked(true);
                        Intent intentFavourite = FavoriteActivity.newIntent(mActivity);
                        mActivity.startActivity(intentFavourite);
                        return true;
                    case R.id.localfavorite:
                        Intent intent = new Intent();
                        // Show only images, no videos or anything else
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        // Always show the chooser (if there are multiple options available)
                        startActivityForResult(intent, PICK_IMAGE_REQUEST);
                        return true;
                    case R.id.rateUs:
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()))) ;
                        }
                        navigationView.getMenu().getItem(2).setChecked(true);
                        return true;
                    case R.id.feedback:
                        Intent i = new Intent(Intent.ACTION_SENDTO);
                        i.setType("text/plain");
                        i.setData(Uri.parse("mailto:omp899@gmail.com"));
                        i.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                        i.putExtra(Intent.EXTRA_TEXT, "Your feedback:");
                        //intent.putExtra(Intent.EXTRA_EMAIL, "omp899@gmail.com");
                        if (i.resolveActivity(getPackageManager()) != null) {
                            startActivity(i);
                        }
                        navigationView.getMenu().getItem(3).setChecked(true);
                        return true;
                    case R.id.facebook:
                        navigationView.getMenu().getItem(4).setChecked(true);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/itchange/"));
                        startActivity(browserIntent);
                        return true;
                    case R.id.share:
                        navigationView.getMenu().getItem(5).setChecked(true);
                        String appLink = "";
                        String text = "https://play.google.com/store/apps/details?id=com.fullpower.changeit";
                        Intent in = ShareCompat.IntentBuilder.from(mActivity)
                                .setText(text + appLink)
                                .setSubject(getString(R.string.title_share))
                                .setType("text/plain")
                                .getIntent()
                                .setPackage(null);
                        startActivity(in);
                        return true;
                    case R.id.settings:
                        navigationView.getMenu().getItem(6).setChecked(true);
                        if (mSettingsFragment == null) {
                            mSettingsFragment = new SettingsFragment();
                            fragmentTransaction.add(R.id.container_body_settings, mSettingsFragment);
                        } else if (mSettingsFragment != null) {
                            if (mSettingsFragment.isHidden()) {
                                fragmentTransaction.show(mSettingsFragment);
                            }
                        }
                        title = getString(R.string.title_settings);
                        getSupportActionBar().setTitle(title);
                        fragmentTransaction.commit();
                        return true;
                    default:
                        navigationView.getMenu().getItem(7).setChecked(true);
                        return true;

                }
            }
        });
        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        Boolean configDataLoaded=false;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AppApplication.mContext);
        configDataLoaded=preferences.getBoolean("ispermissiongranted",false);
        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
        if (!configDataLoaded) {
            /*boolean isPermissionGranted = false;
            if (Build.VERSION.SDK_INT >= 23) {
                isPermissionGranted = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
            }*/
            //if (isPermissionGranted) {
            FetchItemsTask fetchItemsTask = new FetchItemsTask();
            fetchItemsTask.execute();
            //}
            /*else
            {
                String result=fetchItems(0);
                Gson gson = new Gson();
                FeedPageInfo info = gson.fromJson(result, FeedPageInfo.class);
                updateAPIParams(info);
            }*/
        } else {
            String result = readFromJsonFile();
            if (result.compareTo("") != 0) {
                Gson gson = new Gson();
                FeedPageInfo info = gson.fromJson(result, FeedPageInfo.class);
                updateAPIParams(info);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri u = data.getData();
            PhotoObject photoObject = new PhotoObject();
            //String imagePath=u.getPath();
            String imagePath = getRealPathFromURI(u);
            imagePath = "localImage" + imagePath;
            //Log.i(TAG,imagePath);
            photoObject.setUrl(imagePath);
            PhotoLab photoLab = PhotoLab.getPhotoLab(this);
            photoLab.addPhoto(photoObject);
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentURI, projection, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            //int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(0);
            cursor.close();
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clear_disk_cache) {
            try {
                //FastImageLoader.clearDiskCache();
                AppApplication.deleteCache(getApplicationContext());
            } catch (Exception e) {
                //Log.i(TAG, e.toString());
            }
            Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.menu_item_settings) {
            String title = getString(R.string.title_settings);
            getSupportActionBar().setTitle(title);
            mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (mSettingsFragment == null) {
                mSettingsFragment = new SettingsFragment();
                mFragmentTransaction.add(R.id.container_body_settings, mSettingsFragment);
            } else if (mSettingsFragment != null) {
                if (mSettingsFragment.isHidden()) {
                    mFragmentTransaction.show(mSettingsFragment);
                }
            }
            navigationView.getMenu().getItem(6).setChecked(true);
            mFragmentTransaction.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_main_search, menu);
        mMenu = menu;
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            //AppApplication.deleteCache(this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, MainActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
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

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
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

    private class FetchItemsTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String result = fetchItems(0);
            boolean isPermissionGranted = true;
            if (Build.VERSION.SDK_INT >= 23) {
                isPermissionGranted = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
            }
            if (isPermissionGranted) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AppApplication.mContext);
                prefs.edit().putBoolean("ispermissiongranted", true).apply();
                String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
                File myDir = new File(root + "/ChangeIt");
                myDir.mkdirs();
                File db = new File(myDir, "database.txt");
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
            }
            /*root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
            File file = new File(root + "/ChangeIt");
            File[] photoFiles = file.listFiles();
            for(int i=0;i<photoFiles.length;i++)
            {
                Log.i(TAG,photoFiles[i].getAbsolutePath());
            }*/
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            FeedPageInfo info = gson.fromJson(result, FeedPageInfo.class);
            updateAPIParams(info);
        }
    }

    public void updateAPIParams(FeedPageInfo feedPageInfo) {
        if (feedPageInfo != null) {
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
            ExploreFragment.maxCountOfPhotosApi = Integer.parseInt(feedPageInfo.tags.get(0).maxCountOfPhotosApi);
            ExploreFragment.showAds = feedPageInfo.tags.get(0).showAds;
            ExploreFragment.infoEndpoint = feedPageInfo.tags.get(0).links.split("\\s");
        }
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
}