// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"
package com.fullpower.changeit.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fullpower.changeit.R;
import com.fullpower.changeit.adapters.FavoriteRecyclerViewAdapter;
import com.fullpower.changeit.adapters.FavoriteRecyclerViewAdapterOffline;
import com.fullpower.changeit.model.PhotoLab;
import com.fullpower.changeit.model.PhotoObject;

import java.util.List;

public class FavouriteFragmentOffline extends Fragment {
    private FavoriteRecyclerViewAdapterOffline mFavoriteRecyclerViewAdapter;
    private int numberOfColums=2;
    private Button mButton;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int tab = 0;
    PhotoLab mPhotoLab;
    private static Integer[] mList = {2, 2, 2, 2, 2, 2, 2, 2};
    public static boolean refreshed=true;
    //
    public String TAG = "FavouriteFragment";
    public static Fragment newInstance() {
        Bundle args = new Bundle();
        FavouriteFragmentOffline fragment = new FavouriteFragmentOffline();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPhotoLab = PhotoLab.getPhotoLab(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       /* if (!isNetworkAvailableAndConnected()) {
            View view = inflater.inflate(R.layout.activity_internet, container, false);
            mButton = (Button) view.findViewById(R.id.retry_buton);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshFragment(numberOfColums, tab);
                }
            });
            return view;
        }*/
        isStoragePermissionGranted();
        //Log.i(TAG + " " + Integer.toString(tab), "onCreateView");
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        Toolbar tb = (Toolbar) view.findViewById(R.id.toolbarPager);
        tb.inflateMenu(R.menu.menu_add);
        //mFrameLayout = (FrameLayout) view.findViewById(R.id.searchFrameLayout);
        ((AppCompatActivity) getActivity()).setSupportActionBar(tb);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        tb.setTitle("Favourite");
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), numberOfColums);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mFavoriteRecyclerViewAdapter.getItemViewType(position)) {
                    case FavoriteRecyclerViewAdapter.VIEW_TYPE_ITEM:
                        return 1;
                    case FavoriteRecyclerViewAdapter.VIEW_TYPE_PROGRESSBAR:
                        return numberOfColums; //number of columns of the grid
                    default:
                        return -1;
                }
            }
        });
        recyclerView.setLayoutManager(manager);
        recyclerView.setNestedScrollingEnabled(false);
        UpdateUI();
        if(isStoragePermissionGranted()) {
            mFavoriteRecyclerViewAdapter = new FavoriteRecyclerViewAdapterOffline(getActivity(), numberOfColums);
            recyclerView.setAdapter(mFavoriteRecyclerViewAdapter);
            RecyclerView.ItemAnimator animatorX = recyclerView.getItemAnimator();
            if (animator instanceof SimpleItemAnimator) {
                ((SimpleItemAnimator) animatorX).setSupportsChangeAnimations(false);
            }
        }
            //mSwipeRefreshLayout.setColorSchemeColors(R.color.dark, R.color.orange, R.color.dark);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        UpdateUI();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    private void UpdateUI()
    {
        //List<PhotoObject> photoList=mPhotoLab.getPhotos();
        if(isStoragePermissionGranted()) {
            if (mFavoriteRecyclerViewAdapter == null) {
                mFavoriteRecyclerViewAdapter = new FavoriteRecyclerViewAdapterOffline(getActivity(), numberOfColums);
                if (mFavoriteRecyclerViewAdapter == null) {
                    recyclerView.setAdapter(mFavoriteRecyclerViewAdapter);
                }
            } else {
                //mFavoriteRecyclerViewAdapter.setPhotoList(photoList);
                //mFavoriteRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
        //updateSubtitle();
    }

    public void refreshFragment(int numberOfColums, int position) {
        Fragment frg = null;
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        frg= fm.findFragmentById(R.id.fragment_container_favourite);
        if (frg == null) {
            frg = FavouriteFragmentOffline.newInstance();
        }
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)
                        getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

}
