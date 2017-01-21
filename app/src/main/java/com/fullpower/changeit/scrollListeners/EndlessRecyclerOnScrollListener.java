package com.fullpower.changeit.scrollListeners;

/**
 * Created by OJaiswal153939 on 3/7/2016.
 */

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.fullpower.changeit.adapters.MyRecyclerViewAdapter;
import com.fullpower.changeit.fragments.ExploreFragment;
import com.fullpower.changeit.fragments.InstagramFragment;
import com.fullpower.changeit.model.PhotoLab;
import com.fullpower.changeit.utils.Utils;

public abstract class EndlessRecyclerOnScrollListener extends
        RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class
            .getSimpleName();
    public static boolean loading = false;
    private boolean wait = false;
    private int visibleThreshold = 5;
    private int lastVisibleItemPostion = 0;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private GridLayoutManager mGridLayoutManager;
    private Context mContext;
    private PhotoLab mPhotoLab;
    private int tab;

    public EndlessRecyclerOnScrollListener(
            GridLayoutManager gridLayoutManager, MyRecyclerViewAdapter recyclerViewAdapter, Context context, int t) {
        this.mGridLayoutManager = gridLayoutManager;
        mContext = context;
        mPhotoLab = PhotoLab.getPhotoLab(mContext);
        tab = t;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mPhotoLab.getSize(tab);
        firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();
        lastVisibleItemPostion = mGridLayoutManager.findLastVisibleItemPosition();
        //Log.i("Total Item Count", Integer.toString(totalItemCount));
        //Log.i("Last Visible Item", Integer.toString(mGridLayoutManager.findLastVisibleItemPosition()));
        if (totalItemCount <= lastVisibleItemPostion + visibleThreshold && !loading && totalItemCount<ExploreFragment.maxCountOfPhotosApi) {
            loading = true;
            int x=(InstagramFragment.currentSearch500px++)%2;
            onLoadMore500px(x);
            if(Utils.getResolution()==1)
            onLoadMorePixabay(InstagramFragment.currentPagePixabay);
            onLoadMoreAlphaCoders(0);
            onLoadMoreUnSplash(InstagramFragment.currentPageUnSplash);
            int y=(InstagramFragment.methodtype++)%2;
            onLoadMoreFlickr(InstagramFragment.currentPageFlickr, true,y);

        }
    }

    public abstract void onLoadMore500px(int current_page);

    public abstract void onLoadMoreFlickr(String current_page, Boolean bool,int x);

    public abstract void onLoadMoreUnSplash(String current_page);

    public abstract void onLoadMorePixabay(Integer current_page);

    public abstract void onLoadMoreAlphaCoders(Integer current_page);

}