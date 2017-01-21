package com.fullpower.changeit.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.fullpower.changeit.activities.FavoriteActivity;
import com.fullpower.changeit.activities.FavoritePagerActivity;
import com.fullpower.changeit.adapters.FavoriteOfflineAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;
import com.theartofdev.fastimageloader.target.TargetImageView;
import com.fullpower.changeit.R;
import com.fullpower.changeit.adapters.FavoriteAdapter;
import com.fullpower.changeit.model.PhotoLab;
import com.fullpower.changeit.model.PhotoObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OJaiswal153939 on 1/1/2016.
 */
public class FavoritePagerFragment extends Fragment {
    private TargetImageView mImage;
    private static final String ARG_TAB_ID = "tab";
    private static final String ARG_POSITION_ID = "position";
    private static final String ARG_INTERNET_ID = "internet";
    private String TAG = "PagerFragmentSearch";
    private ProgressBar mProgressBar;
    private int position;
    private PhotoLab mPhotoLab;
    private int width;
    private View v;
    private View stickyViewSpacer;
    private ListView mListView;
    private TextView stickyView;
    private PhotoObject mPhotoObject;
    private List<PhotoObject> mObjectList;
    private boolean isInternetAvailable;
    private File mFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        position = (int) getArguments().getSerializable(ARG_POSITION_ID);
        //Log.i(TAG, Integer.toString(position));
        mPhotoLab = PhotoLab.getPhotoLab(getContext());
        mObjectList = FavoritePagerActivity.list;
        mPhotoObject = (PhotoObject) mObjectList.get(position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pager, container, false);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBarPager);
        final ViewGroup.MarginLayoutParams lpt = (ViewGroup.MarginLayoutParams) mProgressBar.getLayoutParams();
        lpt.setMargins(lpt.leftMargin, (width / 2), lpt.rightMargin, lpt.bottomMargin);
        mProgressBar.setLayoutParams(lpt);
        mImage = (TargetImageView) v.findViewById(R.id.zoom_image);
        mListView = (ListView) v.findViewById(R.id.listView);
        stickyView = (TextView) v.findViewById(R.id.stickyView);
        Toolbar tb = (Toolbar) v.findViewById(R.id.toolbarPager);
        tb.inflateMenu(R.menu.menu_remove);
        ((AppCompatActivity) getActivity()).setSupportActionBar(tb);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        View listHeader = getActivity().getLayoutInflater().inflate(R.layout.list_header, null);
        stickyViewSpacer = (Space) listHeader.findViewById(R.id.stickyViewPlaceholder);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        //stickyImagePlaceholder.setLayoutParams(params);
        params.setMargins(0, 0, 0, 0);
        //stickyViewSpacer.setLayoutParams(params);
        mListView.addHeaderView(listHeader);
        setDataSearchResult(mPhotoObject);
        List<PhotoObject> listItem = new ArrayList<PhotoObject>();
        listItem.add(mPhotoObject);
        mListView.setAdapter(new FavoriteAdapter(getContext(), 0, listItem));
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

    public void setDataSearchResult(PhotoObject item) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, width);
        mImage.setLayoutParams(params);
        mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageAware imageAware = new ImageViewAware(mImage, false);
        imageLoader.displayImage(item.getUrl(), imageAware, new ImageLoadingListener() {
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

    public void setDataFileResult(File item) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, width);
        mImage.setLayoutParams(params);
        mProgressBar.setVisibility(View.GONE);
        mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageAware imageAware = new ImageViewAware(mImage, false);
        if (item != null) {
            Bitmap bitmap = null;
            File f = new File(item.getAbsolutePath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageAware.setImageBitmap(bitmap);
        }
    }

    public static FavoritePagerFragment newInstance(int position, int t) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_POSITION_ID, position);
        args.putSerializable(ARG_TAB_ID, t);
        FavoritePagerFragment fragment = new FavoritePagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();

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
        menuInflater.inflate(R.menu.menu_remove, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.remove) {
                if (mPhotoObject != null) {
                    try {
                        mPhotoLab.removePhoto(mPhotoObject);
                    } catch (Exception e) {
                        //Log.i(TAG, e.toString());
                    }
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

}
