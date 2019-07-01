package com.wof.videoplayerpoc;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.prabhat1707.verticalpager.VerticalViewPager;
import com.wof.videoplayerpoc.fragment.CachingFragment;
import com.wof.videoplayerpoc.fragment.CustomViewPager;
import com.wof.videoplayerpoc.fragment.ExoplayerFragment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String YOUTUBE_VIDEO_ID = "uZnWUZW1hQo";
    private String BASE_URL = "https://www.youtube.com";
    private String mYoutubeLink = BASE_URL + "/watch?v=" + YOUTUBE_VIDEO_ID;
    static int currentpostion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    Toast.makeText(this,"Helooo",Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_main);
        //   init();


        /* YouTubeExtractor mExtractor = new YouTubeExtractor(this) {
            @Override
            protected void onExtractionComplete(SparseArray<YtFile> sparseArray, VideoMeta videoMeta) {
                if (sparseArray != null) {
                    //   playVideo(sparseArray.get(17).getUrl());
                    play(sparseArray.get(18).getUrl());
                }
            }





        };
        mExtractor.extract(mYoutubeLink, true, true);*/

        play("");
        init();
    }

    private void play(String url) {
        Log.d("", url);
        final CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(getSupportFragmentManager());
        VerticalViewPager viewPager = findViewById(R.id.viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int currentPosition) {


                if (adapter.getMapFragment(currentPosition) != null) {
                    (adapter.getMapFragment(currentPosition)).onPauseFragment();

                }


                Map<Integer, ExoplayerFragment> map = adapter.getMapFragmentList(currentPosition);
                for (Map.Entry<Integer, ExoplayerFragment> entry : map.entrySet()) {


                    if (entry.getValue() != null) {
                        entry.getValue().onPauseFragment();

                     //   Toast.makeText(MainActivity.this, ""+currentPosition, Toast.LENGTH_SHORT).show();

                    }
                }
                //  currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int i) {


            }
        });
        // viewPager.setSwipeOrientation(CustomViewPager.VERTICAL);
        //   viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);


    }


    private void init() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // transaction.add(R.id.container, new ExoplayerFragment()).commit();
    }


    class CustomViewPagerAdapter extends FragmentStatePagerAdapter {

        HashMap<Integer, ExoplayerFragment> map = new HashMap<>();

        public CustomViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            ExoplayerFragment fragment = ExoplayerFragment.newInstance(i);
            map.put(i, fragment);
            return fragment;

        }

        public ExoplayerFragment getMapFragment(int pos) {
            return map.get(pos);
        }

        public HashMap<Integer, ExoplayerFragment> getMapFragmentList(int pos) {
            return map;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    int count = 0;

    public SimpleCache getSimpleCahce() {
        count++;
        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024);
        SimpleCache simpleCache = new SimpleCache(new File(getCacheDir(), "media_cache" + count), evictor);
        return simpleCache;

    }

    interface FragmentLifecycle {
        void onPauseFragment();
    }
}
