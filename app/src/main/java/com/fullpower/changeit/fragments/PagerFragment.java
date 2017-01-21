package com.fullpower.changeit.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.Space;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fullpower.changeit.AppApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.theartofdev.fastimageloader.target.TargetImageView;
import com.fullpower.changeit.R;
import com.fullpower.changeit.adapters.ListAdapter;
import com.fullpower.changeit.model.ItemUser;
import com.fullpower.changeit.model.PhotoLab;
import com.fullpower.changeit.model.PhotoObject;
import com.fullpower.changeit.service500px.Photo500px;
import com.fullpower.changeit.serviceAlphaCoders.Wallpaper;
import com.fullpower.changeit.serviceFlickr.PhotoFlickr;
import com.fullpower.changeit.servicePixabay.Hit;
import com.fullpower.changeit.serviceUnSplash.PhotoUnSplash;
import com.fullpower.changeit.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OJaiswal153939 on 1/1/2016.
 */
public class PagerFragment extends Fragment {
    private Photo500px mPhoto500px;
    private PhotoFlickr mPhotoFlickr;
    private PhotoUnSplash mPhotoUnSplash;
    private PhotoObject photoObject;
    private Hit mPhotoPixabay;
    private Wallpaper mWallpaper;
    private TargetImageView mImage;
    private static final String ARG_TAB_ID = "tab";
    private static final String ARG_POSITION_ID = "position";
    private Toolbar tb;
    private String TAG = "PagerFragment";
    private ProgressBar mProgressBar;
    private int position;
    private int tab;
    private int isInstanceOf;
    private PhotoLab mPhotoLab;
    private int width;
    private FrameLayout mFrameLayout;
    private int imageWidth, imageHeight;
    private View v;
    private View stickyViewSpacer;
    private View stickyImagePlaceholder;
    private ListView mListView;
    private TextView stickyView;
    private PhotoObject mPhotoObject;
    private Object ob;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        position = (int) getArguments().getSerializable(ARG_POSITION_ID);
        tab = (int) getArguments().getSerializable((ARG_TAB_ID));
        mPhotoLab = PhotoLab.getPhotoLab(getActivity());
        ob = mPhotoLab.getNextPhoto(tab, position);
        isInstanceOf = Utils.isInstanceOf(ob);
        switch (isInstanceOf) {
            case 0:
                mPhoto500px = (Photo500px) ob;
                break;
            case 1:
                mPhotoFlickr = (PhotoFlickr) ob;
                break;
            case 2:
                mPhotoUnSplash = (PhotoUnSplash) ob;
                break;
            case 3:
                mPhotoPixabay = (Hit) ob;
                break;
            case 4:
                mWallpaper = (Wallpaper) ob;
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pager, container, false);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBarPager);
        final ViewGroup.MarginLayoutParams lpt =(ViewGroup.MarginLayoutParams)mProgressBar.getLayoutParams();
        lpt.setMargins(lpt.leftMargin,(width/2),lpt.rightMargin,lpt.bottomMargin);
        mProgressBar.setLayoutParams(lpt);
        mImage = (TargetImageView) v.findViewById(R.id.zoom_image);
        mListView = (ListView) v.findViewById(R.id.listView);
        stickyView = (TextView) v.findViewById(R.id.stickyView);
        Toolbar tb = (Toolbar) v.findViewById(R.id.toolbarPager);
        tb.inflateMenu(R.menu.menu_add);
        mFrameLayout = (FrameLayout) v.findViewById(R.id.flWrapper);
        ((AppCompatActivity) getActivity()).setSupportActionBar(tb);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        View listHeader = getActivity().getLayoutInflater().inflate(R.layout.list_header, null);
        stickyViewSpacer = (Space) listHeader.findViewById(R.id.stickyViewPlaceholder);
        stickyImagePlaceholder = (Space) listHeader.findViewById(R.id.stickyImagePlaceholder);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        //stickyImagePlaceholder.setLayoutParams(params);
        params.setMargins(0, 0, 0, 0);
        //stickyViewSpacer.setLayoutParams(params);
        mListView.addHeaderView(listHeader);
        ItemUser itemUser = null;
        switch (isInstanceOf) {
            case 0:
                itemUser = new ItemUser(mPhoto500px, getActivity(), position);
                setData500px(mPhoto500px);
                break;
            case 1:
                itemUser = new ItemUser(mPhotoFlickr, getActivity(), position);
                setDataFlickr(mPhotoFlickr);
                break;
            case 2:
                itemUser = new ItemUser(mPhotoUnSplash, getActivity(), position);
                setDataUnSplash(mPhotoUnSplash);
                break;
            case 3:
                itemUser = new ItemUser(mPhotoPixabay, getActivity(), position);
                setDataPixabay(mPhotoPixabay);
                break;
            case 4:
                itemUser = new ItemUser(mWallpaper, getActivity(), position);
                setPhotoAlphaCoders(mWallpaper);
                break;
            default:
        }
        List<ItemUser> listItem = new ArrayList<ItemUser>();
        listItem.add(itemUser);
        mListView.setAdapter(new ListAdapter(getContext(), 0, listItem));
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                /* Check if the first item is already reached to top.*/
                if (mListView.getFirstVisiblePosition() == 0) {
                    View firstChild = mListView.getChildAt(0);
                    int topY = 0;
                    if (firstChild != null) {
                        topY = firstChild.getTop();
                    }
                    int heroTopY = stickyViewSpacer.getTop();
                    stickyView.setY(Math.max(0, heroTopY + topY));
                    /* Set the image to scroll half of the amount that of ListView */
                    mImage.setY(topY * 0.5f);
                }
            }
        });

            /*DownloadAndSetBitmapTask task=new DownloadAndSetBitmapTask((WallpaperPagerActivity)getActivi`ty());
        task.execute(mPhoto);*/
        return v;
    }
    public void setData500px(Photo500px item) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, width);
        mImage.setLayoutParams(params);
        mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageAware imageAware = new ImageViewAware(mImage, false);
        imageLoader.displayImage(item.images.get(2).url, imageAware, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public void setPhotoAlphaCoders(Wallpaper item) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, width);
        mImage.setLayoutParams(params);
        mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageAware imageAware = new ImageViewAware(mImage, false);
        imageLoader.displayImage(item.url_image, imageAware, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public void setDataPixabay(Hit item) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, width);
        mImage.setLayoutParams(params);
        mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageAware imageAware = new ImageViewAware(mImage, false);
        imageLoader.displayImage(item.webformatURL, imageAware, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public void setDataFlickr(final PhotoFlickr item) {
        final ImageLoader imageLoader = ImageLoader.getInstance();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, width);
        mImage.setLayoutParams(params);
        mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final ImageAware imageAware = new ImageViewAware(mImage, false);
        imageLoader.displayImage(item.url_c, imageAware, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                imageLoader.displayImage(item.url_l, imageAware, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                    });
                        mProgressBar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public void setDataUnSplash(PhotoUnSplash item) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, width);
        mImage.setLayoutParams(params);
        mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageAware imageAware = new ImageViewAware(mImage, false);
        imageLoader.displayImage(item.urls.regular, imageAware, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public static PagerFragment newInstance(int position, int t) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_POSITION_ID, position);
        args.putSerializable(ARG_TAB_ID, t);
        PagerFragment fragment = new PagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
         if(photoObject!=null) {
            mPhotoLab.updatePhoto(photoObject);
            List<PhotoObject> photoList=mPhotoLab.getPhotos();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.clear();
        menuInflater.inflate(R.menu.menu_add, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = NavUtils.getParentActivityIntent(getActivity());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Log.i(TAG,"BackButtonPressed");
            NavUtils.navigateUpTo(getActivity(), intent);
            return true;
        }else if(item.getItemId() == R.id.favorite)
        {
            mPhotoObject=new PhotoObject();
            mPhotoObject.setPhotoObject(mPhotoObject,ob);
            mPhotoLab.addPhoto(mPhotoObject);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AppApplication.mContext);
            prefs.edit().putBoolean("isBookMarkShown", false).apply();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
