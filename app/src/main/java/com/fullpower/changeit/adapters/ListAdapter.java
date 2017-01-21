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

import com.fullpower.changeit.fragments.ExploreFragment;
import com.theartofdev.fastimageloader.target.TargetAvatarImageView;
import com.fullpower.changeit.R;
import com.fullpower.changeit.fragments.InstagramFragment;
import com.fullpower.changeit.model.ItemUser;
import com.fullpower.changeit.serviceAlphaCoders.AlphaCodersService;
import com.fullpower.changeit.serviceAlphaCoders.FeedInfo;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by OJaiswal153939 on 5/29/1816.
 */
public class ListAdapter extends ArrayAdapter<ItemUser> {
    ProgressDialog mProgressDialog;
    private String TAG = "ListAdapter";
    TextView mTitle;
    RelativeLayout mRelativeLayout;
    TextView mFollowOn;
    Context mContext;
    LinearLayout mLinearLayout;
    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mContext=context;
    }

    public ListAdapter(Context context, int resource, List<ItemUser> items) {
        super(context, resource, items);
        mContext=context;
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
        final ItemUser p = getItem(position);
        mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.tag_texts);

        final int pos = p.position;
        if (p != null) {
            mTargetAvatarImageView = (TargetAvatarImageView) itemView.findViewById(R.id.avatar_image);
            Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Quicksand-Regular.otf");
            mName = (TextView) itemView.findViewById(R.id.username);
            //mName.setTypeface(custom_font);
            mTitle = (TextView) itemView.findViewById(R.id.textViewName);
            //mTitle.setTypeface(custom_font);
            mFollowOn=(TextView)itemView.findViewById(R.id.followOn);
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.tag_texts);
            //mLinearLayout=(LinearLayout)itemView.findViewById(R.id.ccs);
            mDescription = (TextView) itemView.findViewById(R.id.description);
            mSiteImageView = (ImageView) itemView.findViewById(R.id.site_imageView);
            mSetWallpaperButton = (Button) itemView.findViewById(R.id.setWallaper);
            mDownloadButton = (Button) itemView.findViewById(R.id.download);
            mSetWallpaperButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SetWallpaper setWallpaper = new SetWallpaper(p);
                    String url="";
                    switch (p.isInstanceOf) {
                        case 0:
                            url = p.mPhoto500px.images.get(4).url;
                            break;
                        case 1:
                            url = p.mPhotoFlickr.url_o;
                            break;
                        case 2:
                            url = p.mPhotoUnSplash.urls.full;
                            break;
                        case 3:
                            url = p.mPhotoPixabay.webformatURL;
                            url.replaceAll("(_640)","_960");
                            break;
                        case  4:
                            url = p.mWallpaper.url_image;
                            break;
                    }
                    setWallpaper.execute(url);
                }
            });
            mDownloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                    File myDir = new File(root + "/ChangeIt");
                    myDir.mkdirs();
                    int id500px;
                    String idFlickr;
                    File file;
                    String url;
                    DownloadTask downloadTask;
                    switch (p.isInstanceOf) {
                        case 0:
                            id500px = p.mPhoto500px.id;
                            String fname = "Image-" + id500px + ".jpg";
                            file = new File(myDir, fname);
                            downloadTask = new DownloadTask(getContext(), file, "Dowloading Image");
                            url = p.mPhoto500px.images.get(4).url;
                            downloadTask.execute(url);
                            break;
                        case 1:
                            idFlickr = p.mPhotoFlickr.id;
                            fname = "Image-" + idFlickr + ".jpg";
                            file = new File(myDir, fname);
                            downloadTask = new DownloadTask(getContext(), file, "Dowloading Image");
                            url = p.mPhotoFlickr.url_o;
                            downloadTask.execute(url);
                            break;
                        case 2:
                            idFlickr = p.mPhotoUnSplash.id;
                            fname = "Image-" + idFlickr + ".jpg";
                            file = new File(myDir, fname);
                            downloadTask = new DownloadTask(getContext(), file, "Dowloading Image");
                            url = p.mPhotoUnSplash.urls.regular;
                            downloadTask.execute(url);
                            break;
                        case 3:
                            id500px = p.mPhotoPixabay.id;
                            fname = "Image-" + id500px + ".jpg";
                            file = new File(myDir, fname);
                            downloadTask = new DownloadTask(getContext(), file, "Dowloading Image");
                            url = p.mPhotoPixabay.webformatURL;
                            url.replaceAll("(_640)","_960");
                            downloadTask.execute(url);
                            break;
                        case 4:
                            idFlickr = p.mWallpaper.id;
                            fname = "Image-" + idFlickr + ".jpg";
                            file = new File(myDir, fname);
                            downloadTask = new DownloadTask(getContext(), file, "Dowloading Image");
                            url = p.mWallpaper.url_image;
                            downloadTask.execute(url);
                            break;
                    }

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
            final int m = 15;
            double sOffset = 0;
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
            switch (p.isInstanceOf) {
                case 0:
                    userName = p.mPhoto500px.user.username != null ? p.mPhoto500px.user.firstname : p.mPhoto500px.user.username;
                    mTargetAvatarImageView.loadAvatar(p.mPhoto500px.user.avatars.large.https, userName, Specs.INSTA_AVATAR);
                    mName.setText(p.mPhoto500px.user.firstname);
                    myDrawable = ContextCompat.getDrawable(getContext(), R.drawable.fhpx);
                    mSiteImageView.setImageDrawable(myDrawable);
                    mName.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    mTitle.setText(p.mPhoto500px.name);
                    mSiteImageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://500px.com" + p.mPhoto500px.url));
                            getContext().startActivity(browserIntent);
                            //Log.i(TAG, "Clicke Clicked");

                        }
                    });
                    paramsL.setMargins(margin, 4, margin, 4);
                    mDescription.setLayoutParams(paramsL);
                    if (p.mPhoto500px.description != null)
                        htmlTextStr = Html.fromHtml(p.mPhoto500px.description).toString();
                    mDescription.setTextSize(12);
                    // mDescription.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sharp_rectangle));
                    mDescription.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                    mDescription.setText(htmlTextStr);
                    for (int i = 0; i < p.mPhoto500px.tags.size() && i < 10; i++) {
                        final TextView textView = new TextView(getContext());
                        textView.setText("#" + p.mPhoto500px.tags.get(i));
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
                        double width = (p.mPhoto500px.tags.get(i).length() + 1) * offset;
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
                    break;
                case 1:
                    userName = p.mPhotoFlickr.ownername;
                    mTargetAvatarImageView.loadAvatar(p.mPhotoFlickr.getUserProfileUrl(), userName, Specs.INSTA_AVATAR);
                    mName.setText(userName);
                    myDrawable = ContextCompat.getDrawable(getContext(), R.drawable.flickr);
                    mSiteImageView.setImageDrawable(myDrawable);
                    mName.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    mTitle.setText(p.mPhotoFlickr.title);
                    mSiteImageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://flickr.com/photos/" + p.mPhotoFlickr.pathalias));
                            getContext().startActivity(browserIntent);

                        }
                    });
                    paramsL.setMargins(margin, 4, margin, 4);
                    mDescription.setLayoutParams(paramsL);
                    if (p.mPhotoFlickr.description._content != null)
                        htmlTextStr = Html.fromHtml(p.mPhotoFlickr.description._content).toString();
                    mDescription.setTextSize(12);
                    // mDescription.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sharp_rectangle));
                    mDescription.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                    mDescription.setText(htmlTextStr);
                    tags = p.mPhotoFlickr.tags.split("\\s+");
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
                    break;
                case 2:
                    userName = p.mPhotoUnSplash.user.name;
                    mTargetAvatarImageView.loadAvatar(p.mPhotoUnSplash.user.profile_image.large, userName, Specs.INSTA_AVATAR);
                    mName.setText(userName);
                    myDrawable = ContextCompat.getDrawable(p.mContext, R.drawable.unsplash);
                    mSiteImageView.setImageDrawable(myDrawable);
                    mName.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    mSiteImageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://unsplash.com/@" + p.mPhotoUnSplash.user.username));
                            getContext().startActivity(browserIntent);
                        }
                    });
                    break;
                case 3:
                    userName = p.mPhotoPixabay.user;
                    mTargetAvatarImageView.loadAvatar(p.mPhotoPixabay.userImageURL, userName, Specs.INSTA_AVATAR);
                    mName.setText(userName);
                    myDrawable = ContextCompat.getDrawable(getContext(), R.drawable.pixabay);
                    mSiteImageView.setImageDrawable(myDrawable);
                    mName.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    tags = p.mPhotoPixabay.tags.split(",");
                    if (tags.length > 0)
                        mTitle.setText(tags[0]);
                    mSiteImageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(p.mPhotoPixabay.pageURL));
                            getContext().startActivity(browserIntent);

                        }
                    });
                    paramsL.setMargins(margin, 4, margin, 4);
                    mDescription.setLayoutParams(paramsL);
                    if (tags.length > 1) {
                        htmlTextStr = Html.fromHtml(tags[1]).toString();
                    }
                    mDescription.setTextSize(12);
                    // mDescription.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sharp_rectangle));
                    mDescription.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                    mDescription.setText(htmlTextStr);
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
                    break;
                case 4:
                    userName = p.mWallpaper.user_name;
                    String userProfielUrl = "http://static.alphacoders.com/avatars/" + p.mWallpaper.user_id + ".jpg";
                    mTargetAvatarImageView.loadAvatar(userProfielUrl, userName, Specs.INSTA_AVATAR);
                    mName.setText(userName);
                    myDrawable = ContextCompat.getDrawable(getContext(), R.drawable.alphacoders);
                    mSiteImageView.setImageDrawable(myDrawable);
                    mName.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    RestAdapter restAdapterAlpahCoders = new RestAdapter.Builder()
                            .setEndpoint("https://wall.alphacoders.com/api2.0")
                            .build();
                    mAlphaCodersService = restAdapterAlpahCoders.create(AlphaCodersService.class);
                    mAlphaCodersService.getFeedInfo(ExploreFragment.keyAlphaCoders, "wallpaper_info", p.mWallpaper.id, new Callback<FeedInfo>() {
                        @Override
                        public void success(final FeedInfo feed, Response response) {
                            p.mWallpaper.setFeedInfo(feed);
                            mTitle.setText(feed.wallpaper.name);
                            mSiteImageView.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://photos.alphacoders.com/users/profile/" + p.mWallpaper.user_id));
                                    getContext().startActivity(browserIntent);

                                }
                            });
                            paramsL.setMargins(margin, 4, margin, 4);
                            String htmlTextStr = " ";
                            mDescription.setLayoutParams(paramsL);
                            if (feed != null && feed.tags.size() > 1)
                                htmlTextStr = Html.fromHtml(feed.tags.get(1).name).toString();
                            mDescription.setTextSize(12);
                            // mDescription.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sharp_rectangle));
                            mDescription.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                            mDescription.setText(htmlTextStr);
                            double currentWidth = 0;
                            int ULId = 1;
                            int currentCounter = 0;
                            Random random = new Random();
                            DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
                            int screenWidth = metrics.widthPixels;
                            int factor = (screenWidth / 1080);
                            final int m = 15;
                            int n = 5;
                            boolean isNewLine = false;
                            boolean isFirstLine = true;
                            for (int i = 0; i < feed.tags.size() && i < 10; i++) {
                                final TextView textView = new TextView(getContext());
                                textView.setText("#" + feed.tags.get(i).name);
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
                                double width = (feed.tags.get(i).name.length() + 1) * offset;
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
                            mRelativeLayout.invalidate();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });
                    break;
            }
        }
        itemView.postInvalidate();
        return itemView;
    }

    public class SetWallpaper extends AsyncTask<String, Void, Bitmap> {
        int height;
        int width;
        Object mObject;
        SetWallpaper(Object object)
        {
            this.mObject=object;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
            width = metrics.widthPixels*2;//(metrics.densityDpi/160);
            Bitmap bmp = null;
            ItemUser hit=(ItemUser) mObject;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            if(hit.isInstanceOf==3)
            {
                height=720;
                width=960;
            }
            options.outHeight=height;
            options.outWidth=width;
            try {
                URL ulrn = new URL(imageURL);
                HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
                InputStream is = con.getInputStream();
                int x = 5;
                //Log.i(TAG, Integer.toString(width) + " " + Integer.toString(height));
                if(hit.isInstanceOf==3)
                bmp = BitmapFactory.decodeStream(is, null, null);
                else
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
                //bitmap= BlurBuilder.blur(mContext,result);
                /*ImageView imageView = new ImageView(getContext());
                imageView.setImageBitmap(result);
                BitmapDrawable drawable = (BitmapDrawable) imageView
                        .getDrawable();
                bitmap = drawable.getBitmap();*/
                try {
                    WallpaperManager myWallpaperManager = WallpaperManager
                            .getInstance(getContext());
                    myWallpaperManager.setWallpaperOffsetSteps(1, 1);
                    myWallpaperManager.suggestDesiredDimensions(width, height);
                    myWallpaperManager.setBitmap(bitmap);
                    Toast.makeText(getContext(), "Wallpaper set",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    //Toast.makeText(getContext(),"Error setting Wallpaper", Toast.LENGTH_SHORT).show();
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

    public static class DownloadTask extends AsyncTask<String, Integer, String> {
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
            //Log.i("DownloadTask", "Constructor done");
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
                //Log.i("DownloadTask", "Response " + connection.getResponseCode());

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
                        //Log.i("DownloadTask", "Cancelled");
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
                                    //Log.i("ExternalStorage", "Scanned " + path + ":");
                                    //Log.i("ExternalStorage", "-> uri=" + uri);
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
                Toast.makeText(mContext, "File downloaded in CHANGEit folder", Toast.LENGTH_SHORT).show();
        }
    }
}