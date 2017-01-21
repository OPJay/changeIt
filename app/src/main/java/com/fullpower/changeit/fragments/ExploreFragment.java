package com.fullpower.changeit.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.fullpower.changeit.R;
import com.fullpower.changeit.activities.MainActivity;
import com.fullpower.changeit.serviceAlphaCoders.AlphaCodersService;
import com.fullpower.changeit.servicePageInfo.FeedPageInfo;
import com.fullpower.changeit.servicePageInfo.InfoService;
import com.fullpower.changeit.servicePageInfo.PageInfo;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by OJaiswal153939 on 3/3/2016.
 */
public class ExploreFragment extends Fragment {
    //alphacoders parameters
    public static String showAds = "true";
    public static String[] methodAlphaCoders = {
            "featured",
            "highest_rated",
            "by_views",
            "by_favorites"
    };
    public static String[] excludeAlphacoders = {
            "Anime", "Movie", "Video Game", "Love", "Fantasy"
    };
    public static String[] termArrayFlickr = {"nature", "abstract wallpapers", "sky wallpapers", "winter", "macro wallpapers", "landscape wallpapers", "travel", "colorful wallpapers", "autumn wallpapers"};
    public static String[] feature = {"popular", "editors", "highest_rated", "upcoming"};
    public static String[] termArray500px = {"Abstract", "Macro", "Nature", "autumn", "sky", "mountain", "sun", "winter", "vintage", "colorful", "Black and White", "Still Life", "Transportation", "Travel", "Landscapes"};
    public static String includeOnly = "Abstract,Macro,Animals,Nature,Still Life,Transportation,Travel,Underwater,Urban Exploration,Landscapes";
    public static String exclude = "Nude,People,Celebrities,Performing Arts,Black and White,City & Architecture,Sport,Commercial,Concert,Street,Family,Fashion,Film,Fine Art,Food,Wedding,Journalism,Uncategorized";
    /*public static String[] tagArrayI = {
            "fractals", "material design", "street", "winter", "spring", "flowers", "beach", "island", "traffic", "hd", "3d", "texture",
            "fantasy", "colorful", "beautiful", "wind", "robots", "dragon", "windows", "fish", "monocrome", "nature",
            "mountains", "river", "waterfall", "glowing", "abstract", "forest", "space", "city", "earth", "moon", "cute", "artistic", "desktop","cuboid", "awesome", "dance", "music", "night", "landscape", "quotes", "sea", "ocean", "grass", "summer", "sunset",
            "trees", "highway", "universe", "paintings", "retina", "devianart", "butterfly", "digital", "art", "scifi", "infrared", "architecture",
    };*/
    public static String[] infoEndpoint = {"http://ec2-52-23-186-182.compute-1.amazonaws.com"};
    private String TAG = "ExploreFragment";
    ViewPager pager;
    FragmentActivity mContext;
    public static SparseArray<WeakReference<Fragment>> mFragments;
    private Integer[] mList = {2, 2, 2, 2, 2, 2, 2, 2};
    public static String perPageFlickr = "500";
    public static String perPageUnSplash = "30";
    public static Integer perPagePixabay = 30;
    public static int perPage500px = 30;
    public static String keyUnsplash = "c288827579791e3fe71e35bdd6543d18fc2c594a0d94a8af055ce42efb726b0e";
    public static String keyPixabay = "2389471-fe22bb338c8cc4f593426a26d";
    public static String keyAlphaCoders = "496a2ee50b1fffc11ce9b4d3e0238827";
    public static String key500px = "B2B7Q2CvFc5AEbuGq0KsWgZZXANmsDax0se2QyRo";
    public static String keyFlickr = "4f42d84e2501ec69284614036f9fa39a";
    public static int maxCountOfPhotos = 10000;
    public static int maxCountOfPhotosApi = 10000;
    FragmentManager fragmentManager;

    public ExploreFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main_drawer, container, false);
        // Initialize the ViewPager and set an adapter
        pager = (ViewPager) rootView.findViewById(R.id.pager);
        final PagerAdapter mPagerAdapter = new PagerAdapter(fragmentManager);
        pager.setAdapter(mPagerAdapter);
        //pager.setOffscreenPageLimit(2);
        // Bind the tabs to the ViewPager
        final PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.pager_tabs);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Regular.otf");
        //tabs.setTypeface(custom_font, Typeface.BOLD);
        tabs.setViewPager(pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mContext = (FragmentActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    class PagerAdapter extends FragmentStatePagerAdapter {
        FragmentManager mFragmentManager;
        private final String[] TITLES = {"Featured", "Favourite", "Popular", "Recent"
                /*"Daily Popular", "Weekly Popular", "All time popular", "Favourite"*/};

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments = new SparseArray<WeakReference<Fragment>>();
            mFragmentManager = fm;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object f = (Fragment) super.instantiateItem(container, position);
            if (f instanceof InstagramFragment || f instanceof FavouriteFragment)
                mFragments.put(position, new WeakReference<Fragment>((Fragment) f));  // Remember what fragment was in position
            return f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mFragments.setValueAt(position, null);
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (getFragment(position) != null && getFragment(position).get() != null)
                return getFragment(position).get();
            if (position != 1) {
                position = position < 1 ? 0 : position - 1;
                return InstagramFragment.newInstance(mList[position], position);
            } else
                return FavouriteFragment.newInstance();
        }
    }

    public WeakReference<Fragment> getFragment(int position) {
        return mFragments.get(position);
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

    public void refreshFragment(int position) {
        Fragment frg = null;
        FragmentManager manager = mContext.getSupportFragmentManager();
        WeakReference<Fragment> weakReference=mFragments.get(position);
        if (weakReference!=null && weakReference.get() != null) {
            frg = weakReference.get();
        }
        else
        return;
        if (frg == null) {
            if (position == 3)
                frg = FavouriteFragment.newInstance();
            else
                frg = InstagramFragment.newInstance(mList[position], position);
        }
        FragmentTransaction ft = mContext.getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.inflated = false;
    }

}
