package com.fullpower.changeit.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fullpower.changeit.databases.PhotoBaseHelper;
import com.fullpower.changeit.databases.PhotoCursorWrapper;
import com.fullpower.changeit.databases.PhotoDbSchema.PhotoTable;
import com.fullpower.changeit.fragments.ExploreFragment;
import com.fullpower.changeit.serviceAlphaCoders.Wallpaper;
import com.fullpower.changeit.serviceFlickr.PhotoFlickr;
import com.fullpower.changeit.servicePixabay.Hit;
import com.fullpower.changeit.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OJaiswal153939 on 3/16/2016.
 */
public class PhotoLab<T> {
    public static PhotoLab sPhotoLab;
    public static int counter = 0;
    public static int numberOfTabs = 3;
    public List<List<T>> photoList;
    private SQLiteDatabase mDatabase;

    public static PhotoLab getPhotoLab(Context context) {
        if (sPhotoLab == null) {
            sPhotoLab = new PhotoLab(context);
        }
        return sPhotoLab;
    }

    public PhotoLab(Context context) {
        photoList = new ArrayList<>();
        Context mContext = context.getApplicationContext();
        mDatabase = new PhotoBaseHelper(mContext)
                .getWritableDatabase();
        for (int i = 0; i < numberOfTabs; i++) {
            photoList.add((List<T>) new ArrayList<T>());
        }
    }

    public void addObjects(List<T> photo) {

        int x = 0;
        for (int i = 0; i < photo.size(); i++) {
            if (counter < ExploreFragment.maxCountOfPhotosApi) {
                int b = Utils.isInstanceOf(photo.get(i));
               /* if (b == 0) {
                Photo500px photo500px = (Photo500px) photo.get(i);
                if (Integer.valueOf(photo500px.favorites_count) >= 50) {
                    x = (counter) % numberOfTabs;
                    counter = counter + 1;
                    photoList.get(x).add(photo.get(i));
                }
               }*/
                if (b == 1) {
                    PhotoFlickr photoFlickr = (PhotoFlickr) photo.get(i);
                    if (Integer.valueOf(photoFlickr.count_faves) >= 500 && photoFlickr.url_o != null) {
                        x = (counter) % numberOfTabs;
                        counter = counter + 1;
                        photoList.get(x).add(photo.get(i));
                    }
                } else if (b == 3) {
                    Hit photoPixabay = (Hit) photo.get(i);
                    if (Integer.valueOf(photoPixabay.favorites) >= 40) {
                        x = (counter) % numberOfTabs;
                        counter = counter + 1;
                        photoList.get(x).add(photo.get(i));
                    }
                } else if (b == 4) {
                    Wallpaper mWallpaper = (Wallpaper) photo.get(i);
                    boolean v = true;
                    if (mWallpaper.category != null) {
                        v = !mWallpaper.category.equals("Anime") && !mWallpaper.category.equals("Movie") && !mWallpaper.category.equals("Video Game")
                                && !mWallpaper.sub_category.equals("Love")
                                && !mWallpaper.category.equals("Fantasy");
                    }
               /* if(mWallpaper.category!=null)
                v=checkAlphacoders(mWallpaper.category);*/
                    if (v) {
                        x = (counter) % numberOfTabs;
                        counter = counter + 1;
                        photoList.get(x).add(photo.get(i));
                    }
                } else {
                    x = (counter) % numberOfTabs;
                    counter = counter + 1;
                    photoList.get(x).add(photo.get(i));
                }
            }
        }
    }

    public boolean checkAlphacoders(String str) {
        boolean v = true;
        for (int i = 0; i < ExploreFragment.excludeAlphacoders.length; i++) {
            v = v && !(str.compareTo(ExploreFragment.excludeAlphacoders[i]) == 0);
            if (!v)
                return false;
        }
        return v;
    }

    public int getSize(int t) {
        return photoList.get(t).size();
    }

    public T getNextPhoto(int tab, int position) {
        return photoList.get(tab).get(position);
    }

    public List<PhotoObject> getPhotos() {
        List<PhotoObject> photos = new ArrayList<>();
        PhotoCursorWrapper cursor = queryPhotos(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                photos.add(cursor.getPhoto());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return photos;
    }

    public PhotoObject getPhoto(String id) {
        PhotoCursorWrapper cursor = queryPhotos(
                PhotoTable.Cols.IMAGEID + " = ?",
                new String[]{id.toString()}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getPhoto();
        } finally {
            cursor.close();
        }
    }

    public void addPhoto(PhotoObject photoObject) {
        ContentValues values = getContentValues(photoObject);
        mDatabase.insert(PhotoTable.NAME, null, values);
    }

    private static ContentValues getContentValues(PhotoObject photoObject) {
        ContentValues values = new ContentValues();
        values.put(PhotoTable.Cols.IMAGEID, photoObject.getImageID());
        values.put(PhotoTable.Cols.TITLE, photoObject.getTitle());
        values.put(PhotoTable.Cols.URL, photoObject.getUrl());
        values.put(PhotoTable.Cols.PROFILE_URL, photoObject.getProfileUrl());
        values.put(PhotoTable.Cols.USER_URL, photoObject.getUserUrl());
        values.put(PhotoTable.Cols.TAGS, photoObject.getTags());
        values.put(PhotoTable.Cols.DESCRIPTION, photoObject.getDescription());
        values.put(PhotoTable.Cols.IMAGETYPE, photoObject.getImageType());
        values.put(PhotoTable.Cols.USERNAME, photoObject.getUsername());
        values.put(PhotoTable.Cols.SMALLIMAGEURL, photoObject.getSmallUrl());
        return values;
    }

    public void updatePhoto(PhotoObject photoObject) {
        String uuidString = photoObject.getImageID();
        ContentValues values = getContentValues(photoObject);
        mDatabase.update(PhotoTable.NAME, values,
                PhotoTable.Cols.IMAGEID + " = ?",
                new String[]{uuidString});
    }

    public void removePhoto(PhotoObject photoObject) {
        String uuidString = photoObject.getImageID();
        //ContentValues values = getContentValues(photoObject);
        mDatabase.delete(PhotoTable.NAME,
                PhotoTable.Cols.IMAGEID + "= ?",
                new String[]{uuidString});
    }

    public void removePhotoWithPath(String path) {
        //ContentValues values = getContentValues(photoObject);
        mDatabase.delete(PhotoTable.NAME,
                PhotoTable.Cols.URL + "= ?",
                new String[]{path});
    }

    private PhotoCursorWrapper queryPhotos(String whereClause, String[]
            whereArgs) {
        Cursor cursor = mDatabase.query(
                PhotoTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new PhotoCursorWrapper(cursor);
    }
}
