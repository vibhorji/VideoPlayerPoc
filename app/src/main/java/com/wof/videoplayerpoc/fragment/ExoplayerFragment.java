package com.wof.videoplayerpoc.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.wof.videoplayerpoc.MainActivity;
import com.wof.videoplayerpoc.R;

import java.io.File;

public class ExoplayerFragment extends Fragment implements TransferListener {

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 2000;
    private PlayerView playerView;
    private ProgressBar loading;
    private Context mContext;
    private ExoPlayer player;

    RenderersFactory renderersFactory;
    TrackSelector trackSelector;
    LoadControl loadControl;
    private boolean isViewShown = false;

    private String mUrl = "https://mobiletak-pdelivery.akamaized.net/mobiletv/video/2019_04/vod_26_apr_19_chacha_2704_mt_1024/vod_26_apr_19_chacha_2704_mt_1024.m3u8";
    //  private String mUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    //  private String mUrl = "https://www.youtube.com/watch?v=zKO0-J7bDcQ";
    //  public static String mUrl = "https://www.youtube.com/watch?v=HdccMD1D9dM";
    private DataSource.Factory mediaDataSourceFactory;


    private static final DefaultBandwidthMeter BANDWIDTH_METER =
            new DefaultBandwidthMeter();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.freagment_main_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        playerView = view.findViewById(R.id.video_view);
        loading = view.findViewById(R.id.loading);

        mediaDataSourceFactory = buildDataSourceFactory2(false);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        renderersFactory = new DefaultRenderersFactory(mContext);

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);


        loadControl = new DefaultLoadControl();
        if (!isViewShown) {
            fragmentVisibleMethod();
        }


    /*    Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String videoUri = "video url";
                DataSpec dataSpec = new DataSpec(Uri.parse(mUrl), 0, 10 * 1024 * 1024, null);

                LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(90 * 1024 * 1024);
                SimpleCache simpleCache = new SimpleCache(getCahceDir(), evictor);
                DataSource dataSource = buildMyDataSourceFactory().createDataSource();
                CacheUtil.CachingCounters counters = new CacheUtil.CachingCounters();
                try {
                    CacheUtil.cache(dataSpec, simpleCache, buildDataSourceFactory().createDataSource(), counters,null);
                    System.out.println("Done caching");
                } catch (Exception e) {
                    System.out.println("Exception");
                    e.printStackTrace();
                }
            }
        });
        thread.start();*/
        // CacheUtil.cache();


    }

    private void fragmentVisibleMethod(){

        player = ExoPlayerFactory.newSimpleInstance(mContext, renderersFactory, trackSelector, loadControl);

        playerView.setPlayer(player);
        playerView.setUseController(true);
        player.seekTo(currentWindow, playbackPosition);

        // playerView.requestFocus();

        Uri uri = Uri.parse(mUrl);

        MediaSource mediaSource = buildMediaSource(uri, null);

        player.prepare(mediaSource);
        player.setPlayWhenReady(false);

        player.setShuffleModeEnabled(false);
    }

    private File getCahceDir() {
        return new File(mContext.getCacheDir(), "http-cacheddd");
    }

    private File getCahceDir2() {
        return new File(mContext.getCacheDir(), "http-cacheddd-ddd");
    }

    private DefaultDataSource.Factory buildMyDataSourceFactory() {
        return new DefaultDataSourceFactory(mContext, "MyApp", new DefaultBandwidthMeter());
    }

  /*  public DataSource.Factory buildDataSourceFactory() {

        return new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(90 * 1024 * 1024);

                SimpleCache simpleCache = new SimpleCache(getCahceDir2(), evictor);

                DataSource dataSource = buildMyDataSourceFactory().createDataSource();
                return new CacheDataSource(simpleCache, dataSource, CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
            }
        };
    }*/



  /*  private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(mContext,
                new DefaultRenderersFactory(mContext),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView.setPlayer(player);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

        Uri uri = Uri.parse(mUrl);
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, false, false);

    }*/


    private void initPlayer() {
        if (player == null) {
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);

            player = ExoPlayerFactory.newSimpleInstance(mContext,
                    new DefaultRenderersFactory(mContext),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory),
                    new DefaultLoadControl());

            playerView.setPlayer(player);

            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);

            Uri uri = Uri.parse(mUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            player.prepare(mediaSource, true, false);
        }

    }

   /* private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);

    }*/

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory manifestDataSourceFactory =
                new DefaultHttpDataSourceFactory("ua");
        DashChunkSource.Factory dashChunkSourceFactory =
                new DefaultDashChunkSource.Factory(
                        new DefaultHttpDataSourceFactory("ua", BANDWIDTH_METER));
        return new DashMediaSource.Factory(dashChunkSourceFactory,
                manifestDataSourceFactory).createMediaSource(uri);
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource.Factory(new DefaultSsChunkSource.Factory(mediaDataSourceFactory), buildDataSourceFactory(false)).createMediaSource(uri);
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(new DefaultDashChunkSource.Factory(mediaDataSourceFactory), buildDataSourceFactory(false)).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(mContext, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(mContext, "ExoPlayerDemo"), bandwidthMeter);
    }

    DataSource.Factory buildDataSourceFactory2(boolean cache) {

        if (!cache) {
            return new DefaultDataSourceFactory(mContext, BANDWIDTH_METER,
                    buildHttpDataSourceFactory(BANDWIDTH_METER));
        } else {

            return new DataSource.Factory() {
                @Override
                public DataSource createDataSource() {
                    count++;

                    SimpleCache simpleCache = ((MainActivity) getActivity()).getSimpleCahce();
                    return new CacheDataSource(simpleCache, buildCachedHttpDataSourceFactory(BANDWIDTH_METER).createDataSource(),
                            new FileDataSource(), new CacheDataSink(simpleCache, 10 * 1024 * 1024),
                            CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);
                }

            };
        }
    }

    int count = 0;

    private DefaultDataSource.Factory buildCachedHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(getActivity(), bandwidthMeter, buildHttpDataSourceFactory(bandwidthMeter));
    }


    @Override
    public void onTransferInitializing(DataSource source, DataSpec dataSpec, boolean isNetwork) {

    }

    @Override
    public void onTransferStart(DataSource source, DataSpec dataSpec, boolean isNetwork) {

    }

    @Override
    public void onBytesTransferred(DataSource source, DataSpec dataSpec, boolean isNetwork, int bytesTransferred) {

    }

    @Override
    public void onTransferEnd(DataSource source, DataSpec dataSpec, boolean isNetwork) {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            // initializePlayer();
            // initPlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            //   initializePlayer();
            //  initPlayer();
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            // releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            //  releasePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }




    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }
    public void testVideoStop() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;
            player.setPlayWhenReady(false);

            // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data

        } else {
            isViewShown = false;
        }

    }

}