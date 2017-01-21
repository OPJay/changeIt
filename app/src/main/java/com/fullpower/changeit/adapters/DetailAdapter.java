package com.fullpower.changeit.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.theartofdev.fastimageloader.target.TargetAvatarImageView;
import com.fullpower.changeit.R;
import com.fullpower.changeit.service500px.Photo500px;
import com.fullpower.changeit.serviceAlphaCoders.Wallpaper;
import com.fullpower.changeit.serviceFlickr.PhotoFlickr;
import com.fullpower.changeit.servicePixabay.Hit;
import com.fullpower.changeit.serviceUnSplash.PhotoUnSplash;
import com.fullpower.changeit.utils.Specs;
import com.fullpower.changeit.utils.Utils;

import java.util.Random;

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Photo500px mPhoto500px;
    private PhotoUnSplash mPhotoUnSplash;
    private PhotoFlickr mPhotoFlickr;
    private Hit mPhotoPixabay;
    private Wallpaper mWallpaper;
    private ImageView mSiteImageView;
    private static String LOG_TAG = "DetailAdapter";
    public Context mContext;
    public String TAG = "DetailAdapter";
    ProgressDialog mProgressDialog;
    private int isInstanceOf;

    public DetailAdapter(Object photo, Context context) {
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
        if (Utils.hasLollipop()) {
            mProgressDialog = new ProgressDialog(mContext, R.style.MyDialog);
        } else {
            mProgressDialog = new ProgressDialog(mContext, R.style.MyDialogHolo);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder {
        public TargetAvatarImageView mTargetAvatarImageView;
        public TextView mTitle;
        public TextView mName;
        public RelativeLayout mRelativeLayout;
        public TextView mDescription;

        public DataObjectHolder(View itemView) {
            super(itemView);
            mTargetAvatarImageView = (TargetAvatarImageView) itemView.findViewById(R.id.avatar_image);
            Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Quicksand-Regular.otf");
            mTitle = (TextView) itemView.findViewById(R.id.username);
            //mTitle.setTypeface(custom_font);
            mName = (TextView) itemView.findViewById(R.id.textViewName);
            //mName.setTypeface(custom_font);
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.tag_texts);
            mDescription = (TextView) itemView.findViewById(R.id.description);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.details_user, parent, false);
        mSiteImageView = (ImageView) v.findViewById(R.id.site_imageView);
        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Quicksand-Regular.otf");
        RecyclerView.ViewHolder vh;
        vh = new DataObjectHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        DataObjectHolder mHolder = (DataObjectHolder) holder;
        String userName;
        Drawable myDrawable;
        switch (isInstanceOf) {
            case 0:
                userName = mPhoto500px.user.username != null ? mPhoto500px.user.firstname : mPhoto500px.user.username;
                mHolder.mTargetAvatarImageView.loadAvatar(mPhoto500px.user.avatars.large.https, userName, Specs.INSTA_AVATAR);
                mHolder.mTitle.setText(mPhoto500px.user.firstname);
                mHolder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                mHolder.mName.setText(mPhoto500px.name);
                myDrawable = ContextCompat.getDrawable(mContext, R.drawable.fhpx);
                mSiteImageView.setImageDrawable(myDrawable);
                mSiteImageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://500px.com" + mPhoto500px.url));
                        mContext.startActivity(browserIntent);

                    }
                });
                final LinearLayout.LayoutParams paramsL =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsL.setMargins(50, 4, 50, 4);
                mHolder.mDescription.setLayoutParams(paramsL);
                String htmlTextStr = "";
                if (mPhoto500px.description != null)
                    htmlTextStr = Html.fromHtml(mPhoto500px.description).toString();
                mHolder.mDescription.setTextSize(12);
                //mHolder.mDescription.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sharp_rectangle));
                mHolder.mDescription.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
                mHolder.mDescription.setText(htmlTextStr);
                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                int screenWidth = metrics.widthPixels;
                boolean isNewLine = false;
                boolean isFirstLine = true;
                int currentWidth = 0;
                int ULId = 1;
                int currentCounter = 0;
                Random random = new Random();
                int m = 10;
                for (int i = 0; i < mPhoto500px.tags.size() && i < 10; i++) {
                    final TextView textView = new TextView(mContext);
                    textView.setText("#" + mPhoto500px.tags.get(i));
                    textView.setTextSize(12);
                    textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_corner));
                    textView.setId(ULId + i);
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
                    final RelativeLayout.LayoutParams params =
                            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(m, 5, m, 5);
                    textView.setPadding(8, 8, 8, 8);
                    int width = (mPhoto500px.tags.get(i).length() + 1) * 15;
                    if (currentWidth + width + 100 < screenWidth) {
                        currentWidth += width + 15;
                        isNewLine = false;
                        currentCounter++;
                    } else {
                        currentWidth = width + 15;
                        isFirstLine = false;
                        isNewLine = true;
                        currentCounter = 1;
                    }
                    if (i == 0) {
                        params.addRule(RelativeLayout.ALIGN_START);
                        textView.setLayoutParams(params);
                        mHolder.mRelativeLayout.addView(textView);
                    } else if (isNewLine) {
                        params.addRule(RelativeLayout.ALIGN_LEFT);
                        params.addRule(RelativeLayout.BELOW, ULId - 1 + i);
                        textView.setLayoutParams(params);
                        mHolder.mRelativeLayout.addView(textView);
                    } else if (isFirstLine) {
                        params.addRule(RelativeLayout.RIGHT_OF, ULId - 1 + i);
                        textView.setLayoutParams(params);
                        mHolder.mRelativeLayout.addView(textView);
                    } else {
                        params.addRule(RelativeLayout.RIGHT_OF, ULId - 1 + i);
                        params.addRule(RelativeLayout.BELOW, ULId + i - currentCounter);
                        textView.setLayoutParams(params);
                        mHolder.mRelativeLayout.addView(textView);
                    }
                }
                break;
            case 1:
                userName = mPhotoFlickr.ownername;
                mHolder.mTargetAvatarImageView.loadAvatar(mPhotoFlickr.getUserProfileUrl(), userName, Specs.INSTA_AVATAR);
                mHolder.mTitle.setText(userName);
                myDrawable = ContextCompat.getDrawable(mContext, R.drawable.flickr);
                mSiteImageView.setImageDrawable(myDrawable);
                break;
            case 2:
                userName = mPhotoUnSplash.user.name;
                mHolder.mTargetAvatarImageView.loadAvatar(mPhotoUnSplash.user.profile_image.small, userName, Specs.INSTA_AVATAR);
                mHolder.mTitle.setText(userName);
                myDrawable = ContextCompat.getDrawable(mContext, R.drawable.unsplash);
                mSiteImageView.setImageDrawable(myDrawable);
                break;
            case 3:
                userName = mPhotoPixabay.user;
                mHolder.mTargetAvatarImageView.loadAvatar(mPhotoPixabay.pageURL, userName, Specs.INSTA_AVATAR);
                mHolder.mTitle.setText(userName);
                myDrawable = ContextCompat.getDrawable(mContext, R.drawable.pixabay);
                mSiteImageView.setImageDrawable(myDrawable);
                break;
            case 4:
                userName = mWallpaper.user_name;
                mHolder.mTitle.setText(userName);
                mHolder.mTitle.setText(userName);
                myDrawable = ContextCompat.getDrawable(mContext, R.drawable.alphacoders);
                mSiteImageView.setImageDrawable(myDrawable);
                break;
        }
    }

}