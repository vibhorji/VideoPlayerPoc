package com.wof.videoplayerpoc;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.wof.videoplayerpoc.fragment.CachingFragment;
import com.wof.videoplayerpoc.fragment.CustomViewPager;
import com.wof.videoplayerpoc.fragment.ExoplayerFragment;
import com.wof.videoplayerpoc.fragment.FragmentOnce;
import com.wof.videoplayerpoc.fragment.MainPagerFragment;
import com.wof.videoplayerpoc.fragment.VerticalViewPager;

import java.io.File;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MainActivity extends AppCompatActivity {

    private String YOUTUBE_VIDEO_ID = "uZnWUZW1hQo";
    private String BASE_URL = "https://www.youtube.com";
    private String mYoutubeLink = BASE_URL + "/watch?v=" + YOUTUBE_VIDEO_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this,"Helooo",Toast.LENGTH_LONG).show();
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
      //  play("https://mobiletak-pdelivery.akamaized.net/mobiletv/video/2019_04/vod_26_apr_19_chacha_2704_mt_1024/vod_26_apr_19_chacha_2704_mt_1024.m3u8");
        init();
    }

    private void play(String url){
        Log.d("",url);
        final CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(getSupportFragmentManager());
        final VerticalViewPager viewPager = findViewById(R.id.viewPager);
       // viewPager.setSwipeOrientation(CustomViewPager.VERTICAL);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

             //    = getSupportFragmentManager().findFragmentById(viewPager.getCurrentItem());

                Fragment page =  adapter.getItem(viewPager.getCurrentItem());

                if (page instanceof ExoplayerFragment)
                ((ExoplayerFragment) page).testVideoStop();
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });




    }



    private void init() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
       // transaction.add(R.id.container, new ExoplayerFragment()).commit();
    }


    class CustomViewPagerAdapter extends FragmentPagerAdapter {

        public CustomViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return new ExoplayerFragment();
        }

        @Override
        public int getCount() {
            return 5;
        }



    }

    int count = 0;
    public SimpleCache getSimpleCahce(){
        count++;
               LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024);
        SimpleCache simpleCache = new SimpleCache(new File(getCacheDir(), "media_cache"+count), evictor);
        return simpleCache;

    }
}
