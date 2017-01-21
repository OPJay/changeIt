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

import com.theartofdev.fastimageloader.target.TargetAvatarImageView;
import com.fullpower.changeit.R;
import com.fullpower.changeit.model.PhotoObject;
import com.fullpower.changeit.serviceAlphaCoders.AlphaCodersService;
import com.fullpower.changeit.utils.PictureUtils;
import com.fullpower.changeit.utils.Specs;
import com.fullpower.changeit.utils.Utils;

import java.io.File;
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
public class FavoriteAdapter extends ArrayAdapter<PhotoObject> {
    ProgressDialog mProgressDialog;
    private String TAG = "ListAdapter";
    TextView mTitle;
    RelativeLayout mRelativeLayout;
    TextView mFollowOn;
    Context mContext;
    LinearLayout mLinearLayout;
    List<PhotoObject> mList;

    public FavoriteAdapter(Context context, int resource, List<PhotoObject> items) {
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
        TargetAvatarImageView mTargetAvatarImageView;
        TextView mName;
        final ImageView mSiteImageView;
        final TextView mDescription;
        Button mDownloadButton;
        Button mSetWallpaperButton;
        AlphaCodersService mAlphaCodersService;
        if (itemView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            itemView = vi.inflate(R.layout.details_user, null);
        }
        final PhotoObject p = getItem(position);
        mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.tag_texts);
        //mList= PhotoLab.getPhotoLab(mContext).getPhotos();
        final int pos = 0;
        if (p != null) {
            mTargetAvatarImageView = (TargetAvatarImageView) itemView.findViewById(R.id.avatar_image);
            Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Quicksand-Regular.otf");
            mName = (TextView) itemView.findViewById(R.id.username);
            //mName.setTypeface(custom_font);
            mTitle = (TextView) itemView.findViewById(R.id.textViewName);
            //mTitle.setTypeface(custom_font);
            mFollowOn = (TextView) itemView.findViewById(R.id.followOn);
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.tag_texts);
            //mLinearLayout=(LinearLayout)itemView.findViewById(R.id.ccs);
            mDescription = (TextView) itemView.findViewById(R.id.description);
            mSiteImageView = (ImageView) itemView.findViewById(R.id.site_imageView);
            mSetWallpaperButton = (Button) itemView.findViewById(R.id.setWallaper);
            mDownloadButton = (Button) itemView.findViewById(R.id.download);
            mSetWallpaperButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SetWallpaper setWallpaper = new SetWallpaper(p.getUrl());
                    String url = "";
                    url = p.getUrl();
                    setWallpaper.execute(url);
                }
            });
            mDownloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                    File myDir = new File(root + "/ChangeIt");
                    myDir.mkdirs();
                    String idFavorite;
                    File file;
                    String url;
                    DownloadTask downloadTask;
                    /*Random  r=new Random();
                    r.nextInt(10000);*/
                    idFavorite = p.getImageID();
                    String fname = "Image-" + idFavorite + ".jpg";
                    file = new File(myDir, fname);
                    downloadTask = new DownloadTask(getContext(), file, "Dowloading Image");
                    url = p.getUrl();
                    downloadTask.execute(url);

                }
            });
            String userName;
            Drawable myDrawable;
            final LinearLayout.LayoutParams paramsL =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            String htmlTextStr = "";
            DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            boolean isNewLine = false;
            boolean isFirstLine = true;
            double currentWidth = 0;
            int ULId = 1;
            int currentCounter = 0;
            Random random = new Random();;
            final int m = 15;
            double sOffset = 0;
            int sMargin = 0;
            final int margin;
            if (screenWidth < 780) {
                sOffset = 20;
                margin = 35;
            } else {
                sOffset = 26;
                margin = 40;
            }
            final double offset = sOffset;
            int n = 5;
            String tags[] = {};
            userName = p.getUsername();
            mTargetAvatarImageView.loadAvatar(p.getProfileUrl(), userName, Specs.INSTA_AVATAR);
            if (userName != null)
                mName.setText(userName);
            String t = p.getImageType();
            if (t.equals("500px")) {
                myDrawable = ContextCompat.getDrawable(getContext(), R.drawable.fhpx);
                if (p.getTags() != null)
                    tags = p.getTags().split(",");
            } else if (t.equals("unsplash")) {
                myDrawable = ContextCompat.getDrawable(getContext(), R.drawable.unsplash);
            } else if (t.equals("flickr")) {
                myDrawable = ContextCompat.getDrawable(getContext(), R.drawable.flickr);
                if (p.getTags() != null)
                    tags = p.getTags().split("\\s");
            } else if (t.equals("pixabay")) {
                myDrawable = ContextCompat.getDrawable(getContext(), R.drawable.pixabay);
            } else if (t.equals("alphacoders")) {
                myDrawable = ContextCompat.getDrawable(getContext(), R.drawable.alphacoders);
            } else
                myDrawable = ContextCompat.getDrawable(getContext(), R.drawable.browse);
            mSiteImageView.setImageDrawable(myDrawable);
            mName.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            if (p.getTitle() != null)
                mTitle.setText(p.getTitle());
            mSiteImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (p.getUserUrl() != null) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(p.getUserUrl()));
                        getContext().startActivity(browserIntent);
                    }

                }
            });

            paramsL.setMargins(margin, 4, margin, 4);
            mDescription.setLayoutParams(paramsL);
            if (p.getDescription() != null)
                htmlTextStr = Html.fromHtml(p.getDescription()).toString();
            mDescription.setTextSize(12);
            // mDescription.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sharp_rectangle));
            mDescription.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
            mDescription.setText(htmlTextStr);
            if (p.getTags() != null) {
                for (int i = 0; i < tags.length && i < 10; i++) {
                    final TextView textView = new TextView(getContext());
                    textView.setText("#" + tags[i]);
                    textView.setTextSize(12);
                    textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corner));
                    textView.setId(ULId + i);
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                    final RelativeLayout.LayoutParams params =
                            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                    //  m=random.nextInt(5)+10;
                    params.setMargins(m, n, m, n);
                    int padding = 12;
                    textView.setPadding(padding, padding, padding, padding);
                    double width = (tags[i].length() + 1) * offset;
                    if (currentWidth + width < screenWidth) {
                        currentWidth += width + offset;
                        isNewLine = false;
                        currentCounter++;
                    } else {
                        currentWidth = width + offset;
                        isFirstLine = false;
                        isNewLine = true;
                        currentCounter = 1;
                    }
                    if (i == 0) {
                        params.addRule(RelativeLayout.ALIGN_START);
                        textView.setLayoutParams(params);
                        mRelativeLayout.addView(textView);
                    } else if (isNewLine) {
                        params.addRule(RelativeLayout.ALIGN_LEFT);
                        params.addRule(RelativeLayout.BELOW, ULId - 1 + i);
                        textView.setLayoutParams(params);
                        mRelativeLayout.addView(textView);
                    } else if (isFirstLine) {
                        params.addRule(RelativeLayout.RIGHT_OF, ULId - 1 + i);
                        textView.setLayoutParams(params);
                        mRelativeLayout.addView(textView);
                    } else {
                        params.addRule(RelativeLayout.RIGHT_OF, ULId - 1 + i);
                        params.addRule(RelativeLayout.BELOW, ULId + i - currentCounter);
                        textView.setLayoutParams(params);
                        mRelativeLayout.addView(textView);
                    }
                }
            }

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
                bmp = BitmapFactory.decodeStream(is, null, null);
                bmp = PictureUtils.getScaledBitmap(is, width, height, ulrn, x);
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
                    WallpaperManager myWallpaperManager = WallpaperManager
                            .getInstance(getContext());
                    myWallpaperManager.setWallpaperOffsetSteps(1, 1);
                    myWallpaperManager.suggestDesiredDimensions(width, height);
                    myWallpaperManager.setBitmap(bitmap);
                    Toast.makeText(getContext(), "Wallpaper set",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(),
                            "Error setting Wallpaper", Toast.LENGTH_SHORT)
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

    class DownloadTask extends AsyncTask<String, Integer, String> {
        private ProgressDialog mPDialog;
        private Context mContext;
        private PowerManager.WakeLock mWakeLock;
        private File mTargetFile;

        //Constructor parameters :
        // @context (current Activity)
        // @targetFile (File object to write,it will be overwritten if exist)
        // @dialogMessage (message of the ProgresDialog)
        public DownloadTask(Context context, File targetFile, String dialogMessage) {
            this.mContext = context;
            this.mTargetFile = targetFile;
            if (Utils.hasLollipop()) {
                mPDialog = new ProgressDialog(context, R.style.MyDialog);
            } else {
                mPDialog = new ProgressDialog(context, R.style.MyDialogHolo);

            }
            mPDialog.setMessage(dialogMessage);
            mPDialog.setIndeterminate(true);
            mPDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mPDialog.setCancelable(true);
            // reference to instance to use inside listener
            final DownloadTask me = this;
            mPDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    me.cancel(true);
                }
            });
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 180 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(mTargetFile, false);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                    MediaScannerConnection.scanFile(mContext, new String[]{mTargetFile.toString()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                }
                            });
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mPDialog.show();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mPDialog.setIndeterminate(false);
            mPDialog.setMax(100);
            mPDialog.setProgress(progress[0]);

        }

        @Override
        protected void onPostExecute(String result) {
            //Log.i("DownloadTask", "Work Done! PostExecute");
            mWakeLock.release();
            mPDialog.dismiss();
            if (result != null)
                ;//Toast.makeText(mContext, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(mContext, "File downloaded in ChangeIt folder", Toast.LENGTH_LONG).show();
        }
    }

}