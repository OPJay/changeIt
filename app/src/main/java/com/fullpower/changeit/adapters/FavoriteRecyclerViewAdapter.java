package com.fullpower.changeit.adapters;

/**
 * Created by OJaiswal153939 on 2/25/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
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

import com.fullpower.changeit.activities.WallpaperPagerActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.fullpower.changeit.AppApplication;
import com.fullpower.changeit.R;
import com.fullpower.changeit.activities.FavoritePagerActivity;
import com.fullpower.changeit.model.PhotoLab;
import com.fullpower.changeit.model.PhotoObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoriteRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_PROGRESSBAR = 0;
    private boolean isFooterEnabled = false;
    private int numberOfCols;
    private static String TAG = "FavoriteRecyclerViewAdapter";
    private Context mContext;
    private PhotoLab mPhotoLab;
    private List<PhotoObject> photoList;
    private List<File> filesFromStorage;
    public FavoriteRecyclerViewAdapter(Context context, int numberOfCols) {
        mContext = context;
        mPhotoLab = PhotoLab.getPhotoLab(context);
        photoList = mPhotoLab.getPhotos();
        removefromList(photoList);
        this.numberOfCols = numberOfCols;

    }
    public void removefromList(List<PhotoObject>list)
    {
        for(int i=0;i<list.size();i++)
        {
            String str=list.get(i).getUrl();
            //Log.i(TAG,str);
            if(str!=null && str.contains("localImage")) {
                //Log.i(TAG,str);
                photoList.remove(i);
            }
            //Log.i(TAG,Integer.toString(filesFromStorage.size()));
        }
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
    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public GridLayout progressBar;
        public ProgressBar mProgressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (GridLayout) v.findViewById(R.id.progress_bar);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        }
    }
    public class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        public ImageView mTargetImageView;
        private int position;
        private PhotoObject mPhotoObject;
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

        public PhotoObject getSearchedResult() {
            return mPhotoObject;
        }

        @Override
        public void onClick(View v) {
            //Log.i(TAG, Integer.toString(position));
            Intent intent = FavoritePagerActivity.newIntent(mContext, position, 0);
            mContext.startActivity(intent);
        }

        public void setPhoto(PhotoObject photo) {
            mPhotoObject = photo;
        }


        public void setPosition(int pos) {
            position = pos;
        }
    }
    @Override
    public int getItemCount() {
        int size;
        size = photoList.size();
        return (isFooterEnabled) ? size + 1 : size;
    }

    @Override
    public int getItemViewType(int position) {
        return (isFooterEnabled && position >= photoList.size()) ? VIEW_TYPE_PROGRESSBAR : VIEW_TYPE_ITEM;
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

    public void setPhotoList(List<PhotoObject>list)
    {
        this.photoList=list;
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        int t;
        t= photoList.size();
        if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).mProgressBar.setIndeterminate(true);
        } else if (t > 0 && position < t) {
                ((DataObjectHolder) holder).setPosition(position);
                final ImageViewAware imageAware = new ImageViewAware(((DataObjectHolder) holder).mTargetImageView, false);
                PhotoObject ob = (PhotoObject) photoList.get(position);
                if (ob != null && ob.getUrl()!=null && !ob.getUrl().contains("localImage")) {
                    AppApplication.loadImageByPath(ob.getSmallUrl(), imageAware, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            ImageView imageView=(ImageView)view;
                            imageAware.setImageBitmap(loadedImage);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                }
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