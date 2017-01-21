package com.fullpower.changeit.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by OJaiswal153939 on 3/20/2016.
 */
public class PictureUtils {
    public static Bitmap getScaledBitmap(InputStream is, int destWidth,
                                         int destHeight,URL url,int length) {
        // Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int inSampleSize = 1;
        //
        if (srcHeight > destHeight || srcWidth > destWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) srcHeight / (float) destHeight);
            final int widthRatio = Math.round((float) srcWidth / (float) destWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            final float totalPixels = srcHeight * srcWidth;
            final float totalReqPixelsCap = destHeight * destWidth * 2;
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        try {
            is.close();
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            is = con.getInputStream();
        }
        catch (Exception e)
        {

        }
        return  BitmapFactory.decodeStream(is, null, options);
    }
    public static Bitmap getScaledBitmap(InputStream is, Activity
            activity,URL url,int l) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);
        return getScaledBitmap(is, size.x, size.y,url,l);
    }
}
