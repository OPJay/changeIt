package com.fullpower.changeit.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fullpower.changeit.AppApplication;
import com.fullpower.changeit.R;
import com.fullpower.changeit.adapters.MyRecyclerViewAdapter;
import com.fullpower.changeit.model.PhotoLab;
import com.fullpower.changeit.scrollListeners.EndlessRecyclerOnScrollListener;
import com.fullpower.changeit.service500px.Feed500px;
import com.fullpower.changeit.serviceAlphaCoders.FeedAlphaCoders;
import com.fullpower.changeit.serviceFlickr.FeedFlickr;
import com.fullpower.changeit.servicePixabay.FeedPixabay;
import com.fullpower.changeit.serviceUnSplash.PhotoUnSplash;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InstagramFragment extends Fragment {
    private static final String ARG_NUM_COL = "num_column";
    private static final String ARG_FRAGMENT_POS = "fragment_id";
    private MyRecyclerViewAdapter mMyRecyclerViewAdapter=null;
    private int numberOfColums;
    private Button mButton;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AdView mAdView;
    private int tab;
    PhotoLab mPhotoLab;
    private static Integer[] mList = {2, 2, 2, 2, 2, 2, 2, 2};
    public static boolean refreshed[] = {
            false, false, false,
            false, false, false,
            false, false, false};
    public static boolean pageLoaded[] = {
            false, false, false,
            false, false, false,
            false, false, false};
    //500px data
    public static int currentPage500px[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    public static int currentPage500pxSearch[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    public static String currentPageFlickr = "1";
    public static int currentSearch500px = 0;
    public static int termCount = 0;
    public static int featureCount = 0;
    public static String imageSize = "1,3,440,1080,1600";
    public static String sort = "_core";
    //Flickr parameters
    public static int methodtype = 0;
    public static int currentSearchFlickr = 0;
    public static String currentFlickrSearch[] = {"1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1"};
    public static String date;
    public static String sortFlickr = "relevance";
    Calendar calender;
    private static String TAG = "InstagramFragment";
    public static String method = "flickr.interestingness.getList";
    public static String methodSearch = "flickr.photos.search";
    public static String extras = "description, license, date_upload, date_taken," +
            " owner_name, icon_server, original_format, last_update, geo, tags," +
            "machine_tags, o_dims, views," + "media, path_alias, url_sq, url_t, " +
            "url_s, url_q, url_m, url_n, url_z, url_c, url_l, url_o,count_faves";
    public static String format = "json";
    public static String nonjsoncallback = "1";
    public static int dateDecCounter = 2;
    //UnSplash parameters
    public static String currentPageUnSplash = "1";
    //Pixabay parameters
    public static String image_type = "photo";
    public static Boolean editors_choice = true;
    public static String order = "popular";
    public static Boolean safesearch = true;
    public static Integer currentPagePixabay = 1;
    public static int currentAlphaCoders = 0;
    public static String info_level = "3";
    public static int currentAlphaCodersCount[] = {1, 1, 1, 1};
    public static String check_last = "1";

    public static InstagramFragment newInstance(int numberOfColums, int pos) {
        Bundle args = new Bundle();
        if (args != null) {
            args.putSerializable(ARG_NUM_COL, mList[pos]);
            args.putSerializable(ARG_FRAGMENT_POS, pos);
        }
        InstagramFragment fragment = new InstagramFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPhotoLab = PhotoLab.getPhotoLab(getActivity());
        numberOfColums = (int) getArguments().getSerializable(ARG_NUM_COL);
        tab = (int) getArguments().getSerializable(ARG_FRAGMENT_POS);
        calender = Calendar.getInstance();
        calender.add(Calendar.DATE, -dateDecCounter);
        dateDecCounter++;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        date = df.format(calender.getTime());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG,"OncreateView Called " + Integer.toString(tab));
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
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AppApplication.mContext);
        //String showAd=prefs.getString("showAds","false");
        if (Boolean.parseBoolean(ExploreFragment.showAds)) {
            mAdView = (AdView) view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("EB13E0B5908CC00EB4D1BF1FDC25B932")
                    .build();
            mAdView.loadAd(adRequest);
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
                switch (mMyRecyclerViewAdapter.getItemViewType(position)) {
                    case MyRecyclerViewAdapter.VIEW_TYPE_ITEM:
                        return 1;
                    case MyRecyclerViewAdapter.VIEW_TYPE_PROGRESSBAR:
                        return numberOfColums; //number of columns of the grid
                    default:
                        return -1;
                }
            }
        });
        recyclerView.setLayoutManager(manager);
        UpDateUI();
        RecyclerView.ItemAnimator animatorX = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animatorX).setSupportsChangeAnimations(false);
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*if (mPhotoLab.getSize(tab) > 0) {
                    if (refreshed[tab] == false) {
                        refreshed[tab] = true;
                        refreshFragment(numberOfColums, tab);
                    }
                }*/
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.dark), getResources().getColor(R.color.orange), getResources().getColor(R.color.dark));
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(manager, mMyRecyclerViewAdapter, getActivity(), tab) {
            @Override
            public void onLoadMore500px(int x) {
                if (x == 0)
                    loadData500px();
                else
                    loadData500pxSearch();
            }

            @Override
            public void onLoadMoreFlickr(String current_page, Boolean bool, int x) {
                if (x == 0)
                    loadDataFlickr();
                else
                    loadDataFlickrSearch();
            }

            @Override
            public void onLoadMoreUnSplash(String current_page) {

                loadDataUnSplash();
            }

            @Override
            public void onLoadMorePixabay(Integer current_page) {
                loadDataPixabay();
            }

            @Override
            public void onLoadMoreAlphaCoders(Integer current_page) {
                loadDataAlphaCoders();
            }
        });
        if (tab == 0) {
            if (pageLoaded[tab] == false) {
                if (mMyRecyclerViewAdapter != null) {
                    loadDataFlickr();
                    methodtype = 1;
                    loadData500px();
                    currentSearch500px = 1;
                }
            }
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"On Resume");
        if (mAdView != null) {
            mAdView.resume();
        }
        UpDateUI();
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        Log.i(TAG,"Ondestroy"+Integer.toString(tab));
        super.onDestroy();
    }
    @Override
    public void onDestroyView()
    {
     super.onDestroyView();
        Log.i(TAG,"OndestroyView "+Integer.toString(tab));
    }
    private void UpDateUI() {
        if (mMyRecyclerViewAdapter == null) {
            mMyRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(), numberOfColums, tab);
            if (recyclerView != null)
                recyclerView.setAdapter(mMyRecyclerViewAdapter);
        }
    }

    private void loadData500px() {
        final int i = (InstagramFragment.featureCount++) % ExploreFragment.feature.length;
        mMyRecyclerViewAdapter.loadData500px(ExploreFragment.feature[i], ExploreFragment.perPage500px, ExploreFragment.includeOnly, currentPage500px[i], imageSize, ExploreFragment.key500px, ExploreFragment.exclude, 1, new Callback<Feed500px>() {
            @Override
            public void success(Feed500px feed, Response response) {
                if (feed.photos != null) {
                    currentPage500px[i]++;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                //Toast.makeText(InstagramFragment.this.getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData500pxSearch() {
        final int i = (InstagramFragment.termCount++) % ExploreFragment.termArray500px.length;
        mMyRecyclerViewAdapter.loadData500pxSearch(ExploreFragment.termArray500px[i], ExploreFragment.perPage500px, currentPage500pxSearch[i], imageSize, ExploreFragment.key500px, ExploreFragment.exclude, 1, InstagramFragment.sort, new Callback<Feed500px>() {
            @Override
            public void success(Feed500px feed, Response response) {
                if (feed.photos != null) {
                    currentPage500pxSearch[i]++;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                //Toast.makeText(InstagramFragment.this.getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadDataAlphaCoders() {
        final int r = (InstagramFragment.currentAlphaCoders++) % ExploreFragment.methodAlphaCoders.length;
        mMyRecyclerViewAdapter.loadDataAlphaCoders(ExploreFragment.keyAlphaCoders, ExploreFragment.methodAlphaCoders[r], info_level, currentAlphaCodersCount[r], check_last, new Callback<FeedAlphaCoders>() {
            @Override
            public void success(FeedAlphaCoders feed, Response response) {
                if (feed.wallpapers != null) {
                    currentAlphaCodersCount[r]++;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                //Toast.makeText(InstagramFragment.this.getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataPixabay() {
        mMyRecyclerViewAdapter.loadDataPixabay(ExploreFragment.keyPixabay, image_type, editors_choice, order, safesearch, currentPagePixabay, ExploreFragment.perPagePixabay, new Callback<FeedPixabay>() {
            @Override
            public void success(FeedPixabay feed, Response response) {
                if (feed != null) {
                    currentPagePixabay++;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                //Toast.makeText(InstagramFragment.this.getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataUnSplash() {
        mMyRecyclerViewAdapter.loadDataUnSplash(ExploreFragment.keyUnsplash, ExploreFragment.perPageUnSplash, currentPageUnSplash, new Callback<List<PhotoUnSplash>>() {
            @Override
            public void success(List<PhotoUnSplash> feed, Response response) {
                if (feed != null) {
                    int val = Integer.valueOf(currentPageUnSplash);
                    val = val + 1;
                    currentPageUnSplash = Integer.toString(val);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                //Toast.makeText(InstagramFragment.this.getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //boolean b is for deciding whether to load data for the given tab or not
    private void loadDataFlickr() {
        mMyRecyclerViewAdapter.loadFlickrData(method, ExploreFragment.keyFlickr, date, extras, format, ExploreFragment.perPageFlickr,
                currentPageFlickr, nonjsoncallback, new Callback<FeedFlickr>() {
                    @Override
                    public void success(FeedFlickr feed, Response response) {
                        calender = Calendar.getInstance();
                        calender.add(Calendar.DATE, -dateDecCounter);
                        dateDecCounter++;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        date = df.format(calender.getTime());
                        if (pageLoaded[tab] == false) {
                            pageLoaded[tab] = true;
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // Toast.makeText(InstagramFragment.this.getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //boolean b is for deciding whether to load data for the given tab or not
    private void loadDataFlickrSearch() {
        final int x = (InstagramFragment.currentSearchFlickr++) % ExploreFragment.termArrayFlickr.length;
        mMyRecyclerViewAdapter.loadFlickrDataSearch(methodSearch, ExploreFragment.keyFlickr, ExploreFragment.termArrayFlickr[x], extras, format, ExploreFragment.perPageFlickr,
                currentFlickrSearch[x], sortFlickr, nonjsoncallback, new Callback<FeedFlickr>() {
                    @Override
                    public void success(FeedFlickr feed, Response response) {
                        int v = Integer.valueOf(currentFlickrSearch[x]);
                        v = v + 1;
                        currentFlickrSearch[x] = Integer.toString(v);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // Toast.makeText(InstagramFragment.this.getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void refreshFragment(int numberOfColums, int position) {
        Fragment frg = null;
        FragmentManager manager = getActivity().getSupportFragmentManager();
        WeakReference<Fragment> weakReference=ExploreFragment.mFragments.get(position);
        if (weakReference!=null && weakReference.get() != null) {
            frg = weakReference.get();
        }
        if (frg == null) {
                //int pos=position < 1 ? 0 : position - 1;
                frg = InstagramFragment.newInstance(mList[position], position);
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
