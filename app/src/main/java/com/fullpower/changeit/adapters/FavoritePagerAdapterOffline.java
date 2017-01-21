package com.fullpower.changeit.adapters;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.fullpower.changeit.fragments.FavoritePagerFragmentOffline;
import com.fullpower.changeit.model.PhotoLab;
import com.fullpower.changeit.model.PhotoObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by OJaiswal153939 on 3/16/2016.
 */
public class FavoritePagerAdapterOffline extends FragmentStatePagerAdapter {
    private PhotoLab mPhotoLab;
    public static int position = 0;
    private Context mContext;
    private int tab;
    private String TAG="FavoritePagerAdapterOff";
    private List<PhotoObject> mObjectList;
    private List<File> filesFromStorage;
    boolean internet;
    public static int getPosition() {
        return position;
    }
    public FavoritePagerAdapterOffline(FragmentManager fm, Context context, int t) {
        super(fm);
        mPhotoLab = PhotoLab.getPhotoLab(context);
        mContext = context;
        filesFromStorage = getFilesFromPhotos();
        mObjectList = mPhotoLab.getPhotos();
        //Log.i(TAG,Integer.toString(mObjectList.size()));
        addToList(mObjectList);
        tab = t;

    }
    @Override
    public int getCount() {
        return filesFromStorage.size();
    }
    public int photoObjectSize(List<PhotoObject>list)
    {
        int count=0;
        for(int i=0;i<list.size();i++)
        {
            if(!list.get(i).getUrl().contains("localImage"))
            {
                count++;
            }
        }
        return count;
    }
    @Override
    public Fragment getItem(int position) {
        this.position = position;
        //Log.i("MyFragStatePagerAdapter", Integer.toString(position));
        //Log.i(TAG,Integer.toString(position));
        return FavoritePagerFragmentOffline.newInstance(position, tab);
    }
    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)
                        mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
    public void addToList(List<PhotoObject>list)
    {
        for(int i=0;i<list.size();i++)
        {
            String str=list.get(i).getUrl();
            //Log.i(TAG,str);
            if(str.contains("localImage")) {
                //Log.i(TAG,str);
                String path=str.replace("localImage","");
                //Log.i(TAG,path);
                File file = new File(path);
                filesFromStorage.add(file);
            }
            //Log.i(TAG,Integer.toString(filesFromStorage.size()));
        }
        for(int i=0;i<filesFromStorage.size();i++) {
            String str = filesFromStorage.get(i).getAbsolutePath();
            //Log.i(TAG+"xXXX", str);
        }

    }

    public static List<File> getFilesFromPhotos() {
        String state = Environment.getExternalStorageState();

        if (state.contentEquals(Environment.MEDIA_MOUNTED) || state.contentEquals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File file = new File(root + "/ChangeIt");
            File[] photoFiles = file.listFiles();
            List<File> list= new ArrayList<>(Arrays.asList(photoFiles));
           /* for(int i=0;i<photoFiles.length;i++)
            {
                Log.i(TAG,photoFiles[i].getAbsolutePath());
            }*/
            return list;
        } else {
            Log.v("Error", "External Storage Unaccessible: " + state);
            File[] files=new File[0];
            return Arrays.asList(files);
        }
    }
}
