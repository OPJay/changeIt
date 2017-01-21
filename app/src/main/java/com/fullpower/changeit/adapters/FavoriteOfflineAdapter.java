package com.fullpower.changeit.adapters;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fullpower.changeit.R;
import com.fullpower.changeit.model.PhotoObject;
import com.fullpower.changeit.serviceAlphaCoders.AlphaCodersService;
import com.fullpower.changeit.utils.PictureUtils;
import com.fullpower.changeit.utils.Specs;
import com.fullpower.changeit.utils.Utils;
import com.theartofdev.fastimageloader.target.TargetAvatarImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;

/**
 * Created by OJaiswal153939 on 5/29/1816.
 */
public class FavoriteOfflineAdapter extends ArrayAdapter<File> {
    ProgressDialog mProgressDialog;
    private String TAG = "ListAdapter";
    TextView mTitle;
    Context mContext;
    LinearLayout mLinearLayout;
    List<File> mList;

    public FavoriteOfflineAdapter(Context context, int resource, List<File> items) {
        super(context, resource, items);
        mList = items;
        mContext = context;
        if (Utils.hasLollipop()) {
            mProgressDialog = new ProgressDialog(getContext(), R.style.MyDialog);
        } else {
            mProgressDialog = new ProgressDialog(getContext(), R.style.MyDialogHolo);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        //TargetAvatarImageView mTargetAvatarImageView;
        //TextView mName;
        //final ImageView mSiteImageView;
        //final TextView mDescription;
        //Button mDownloadButton;
        Button mSetWallpaperButton;
        //AlphaCodersService mAlphaCodersService;
        if (itemView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            itemView = vi.inflate(R.layout.details_user_no_internet, null);
        }
        final File p = getItem(position);
        //mList= PhotoLab.getPhotoLab(mContext).getPhotos();
        final int pos = 0;
        if (p != null) {
            //mLinearLayout=(LinearLayout)itemView.findViewById(R.id.ccs);
            mSetWallpaperButton = (Button) itemView.findViewById(R.id.setWallaper);
            mSetWallpaperButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SetWallpaper setWallpaper = new SetWallpaper(p.getAbsolutePath());
                    String url = "";
                    url = p.getAbsolutePath();
                    setWallpaper.execute(url);
                }
            });
        }
        itemView.postInvalidate();
        return itemView;
    }


     class SetWallpaper extends AsyncTask<String, Void, Bitmap> {
        int height;
        int width;
        String url;

        SetWallpaper(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // Set progressdialog title
            mProgressDialog.setTitle("");
            // Set progressdialog message
            mProgressDialog.setMessage("Setting Wallpaper...");
            mProgressDialog.setMessage("Setting Wallpaper...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageURL = params[0];
           DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            height = metrics.heightPixels;//(metrics.densityDpi/160);
            width = metrics.widthPixels * 2;//(metrics.densityDpi/160);
            File f= new File(imageURL);
            Bitmap bitmap=null;
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
                            .getInstance(getContext());
                    myWallpaperManager.setWallpaperOffsetSteps(1, 1);
                    myWallpaperManager.suggestDesiredDimensions(width, height);
                    myWallpaperManager.setBitmap(bitmap);
                    Toast.makeText(getContext(), "Wallpaper set",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(),
                            e.toString(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
                //Log.i(TAG, e.toString());
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }
}