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

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ExploreByTouchHelper;
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
import android.widget.ImageView;

import com.fullpower.changeit.AppApplication;
import com.fullpower.changeit.R;
import com.fullpower.changeit.adapters.FavoriteRecyclerViewAdapter;
import com.fullpower.changeit.model.PhotoLab;
import com.fullpower.changeit.model.PhotoObject;

import java.lang.ref.WeakReference;
import java.util.List;

public class FavouriteFragment extends Fragment {
    private FavoriteRecyclerViewAdapter mFavoriteRecyclerViewAdapter;
    private int numberOfColums = 2;
    private Button mButton;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int tab = 3;
    private boolean show = true;
    PhotoLab mPhotoLab;
    private static Integer[] mList = {2, 2, 2, 2, 2, 2, 2, 2};
    public static boolean refreshed = true;
    private List<PhotoObject> photoList;
    //
    public String TAG = "FavouriteFragment";

    public static Fragment newInstance() {
        Bundle args = new Bundle();
        FavouriteFragment fragment = new FavouriteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPhotoLab = PhotoLab.getPhotoLab(getActivity());
        photoList = mPhotoLab.getPhotos();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AppApplication.mContext);
        show = prefs.getBoolean("isBookMarkShown", true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!isNetworkAvailableAndConnected()) {
            View view = inflater.inflate(R.layout.activity_internet, container, false);
            mButton = (Button) view.findViewById(R.id.retry_buton);
            mButton.setVisibility(View.INVISIBLE);
            /*mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshFragment(numberOfColums, tab);
                }
            });*/
            return view;
        }
        //Log.i(TAG + " " + Integer.toString(tab), "onCreateView");
        View view = inflater.inflate(R.layout.fragment_favourite_without_toolbar, container, false);
        /*Toolbar tb = (Toolbar) view.findViewById(R.id.toolbarPager);
        tb.inflateMenu(R.menu.menu_add);
        //mFrameLayout = (FrameLayout) view.findViewById(R.id.searchFrameLayout);
        ((AppCompatActivity) getActivity()).setSupportActionBar(tb);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        tb.setTitle("Favourite");*/
        if (!show) {
            ImageView imageView = (ImageView) view.findViewById(R.id.bookmark_back);
            imageView.setVisibility(View.INVISIBLE);
        }
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
        //UpdateUI();
        mFavoriteRecyclerViewAdapter = new FavoriteRecyclerViewAdapter(getActivity(), numberOfColums);
        recyclerView.setAdapter(mFavoriteRecyclerViewAdapter);
        RecyclerView.ItemAnimator animatorX = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animatorX).setSupportsChangeAnimations(false);
        }
        //mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.dark), getResources().getColor(R.color.orange), getResources().getColor(R.color.dark));
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        UpdateUI();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void UpdateUI() {
        if (mFavoriteRecyclerViewAdapter == null) {
            mFavoriteRecyclerViewAdapter = new FavoriteRecyclerViewAdapter(getActivity(), numberOfColums);
            if (mFavoriteRecyclerViewAdapter == null) {
                recyclerView.setAdapter(mFavoriteRecyclerViewAdapter);
            }
        }
        //updateSubtitle();
    }

    public void refreshFragment(int numberOfColums, int position) {
        Fragment frg = null;
        FragmentManager manager = getActivity().getSupportFragmentManager();
        WeakReference<Fragment> weakReference=ExploreFragment.mFragments.get(position);
        if (weakReference!=null && weakReference.get() != null) {
            frg = weakReference.get();
        }
        if (frg == null) {
            frg = FavouriteFragment.newInstance();
        }
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
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

}
