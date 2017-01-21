package com.fullpower.changeit.model;

import android.content.Context;
import android.widget.ImageView;
import com.fullpower.changeit.utils.Utils;
import com.fullpower.changeit.service500px.Photo500px;
import com.fullpower.changeit.serviceAlphaCoders.Wallpaper;
import com.fullpower.changeit.serviceFlickr.PhotoFlickr;
import com.fullpower.changeit.servicePixabay.Hit;
import com.fullpower.changeit.serviceUnSplash.PhotoUnSplash;

/**
 * Created by OJaiswal153939 on 5/29/2016.
 */
public class ItemUser {
    public Photo500px mPhoto500px;
    public PhotoUnSplash mPhotoUnSplash;
    public PhotoFlickr mPhotoFlickr;
    public Hit mPhotoPixabay;
    public Wallpaper mWallpaper;
    public ImageView mSiteImageView;
    public Context mContext;
    public String TAG = "ItemUser";
    public int isInstanceOf;
    public int position;

    public ItemUser(Object photo, Context context, int pos) {
        position = pos;
        isInstanceOf = Utils.isInstanceOf(photo);
        if (isInstanceOf == 0)
            mPhoto500px = (Photo500px) photo;
        else if (isInstanceOf == 1)
            mPhotoFlickr = (PhotoFlickr) photo;
        else if (isInstanceOf == 2)
            mPhotoUnSplash = (PhotoUnSplash) photo;
        else if (isInstanceOf == 3)
            mPhotoPixabay = (Hit) photo;
        else if (isInstanceOf == 4)
            mWallpaper = (Wallpaper) photo;
        mContext = context;
    }
}
