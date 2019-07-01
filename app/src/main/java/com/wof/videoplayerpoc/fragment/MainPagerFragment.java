package com.wof.videoplayerpoc.fragment;

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

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.wof.videoplayerpoc.ExoPlayerListener;
import com.wof.videoplayerpoc.MainActivity;
import com.wof.videoplayerpoc.R;

public class MainPagerFragment extends Fragment implements ExoPlayerListener {

    private SimpleExoPlayer player;
    private Context context;
    public PlayerView playerView;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private boolean playWhenReady;
    private static final DefaultBandwidthMeter BANDWIDTH_METER =
            new DefaultBandwidthMeter();
    private DataSource.Factory mediaDataSourceFactory;
      private String mUrl = "https://mobiletak-pdelivery.akamaized.net/mobiletv/video/2019_04/vod_26_apr_19_chacha_2704_mt_1024/vod_26_apr_19_chacha_2704_mt_1024.m3u8";
    //  private String mUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    //  private String mUrl = "https://www.youtube.com/watch?v=zKO0-J7bDcQ";
   // public static String mUrl = "https://www.youtube.com/watch?v=HdccMD1D9dM";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        s("onAttach");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        s("onCreateView");
        return inflater.inflate(R.layout.freagment_main_pager, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        s("onViewCreated");
        mediaDataSourceFactory = buildDataSourceFactory2(true);
        playerView = view.findViewById(R.id.video_view);
       /* view.findViewById(R.id.exo_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });*/
        initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        s("onResume");
        initializePlayer();
    }

    @Override
    public void onStart() {
        super.onStart();
        s("onStart");

    }

    @Override
    public void onPause() {
        super.onPause();
        s("onPause");
        releasePlayer();

    }

    @Override
    public void onStop() {
        super.onStop();
        s("onStop");
        releasePlayer();
    }

    @Override
    public void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(context,
                new DefaultRenderersFactory(context),
                new DefaultTrackSelector(), new DefaultLoadControl());
        playerView.setPlayer(player);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {


            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }
        });

      /*  MediaSource mediaSource = new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                 createMediaSource(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));*/
                //     createMediaSource(Uri.parse("http://asset1-inspection.ninja24.in/inspection/2019/02/20/C633D33/v0/engineTransmission/additionalInfo/engineSound/enginesoundadditionalinfoform1_1550672833.mp4\n"));
                //        createMediaSource(Uri.parse(url));
        Uri uri = Uri.parse(mUrl);
        MediaSource mediaSource = buildMediaSource(uri, null);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
        player.seekTo(currentWindow, playbackPosition);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                player.setPlayWhenReady(false);

            }
        }, 5000);
    }

    DataSource.Factory buildDataSourceFactory2(boolean cache) {

        if (!cache) {
            return new DefaultDataSourceFactory(context, BANDWIDTH_METER,
                    buildHttpDataSourceFactory(BANDWIDTH_METER));
        }else{

            return new DataSource.Factory() {
                @Override
                public DataSource createDataSource() {

                    SimpleCache simpleCache = ((MainActivity)getActivity()).getSimpleCahce();
                    return new CacheDataSource(simpleCache, buildCachedHttpDataSourceFactory(BANDWIDTH_METER).createDataSource(),
                            new FileDataSource(), new CacheDataSink(simpleCache, 10 * 1024 * 1024),
                            CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);
                }

            };
        }
    }


    private DefaultDataSource.Factory buildCachedHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(getActivity(), bandwidthMeter, buildHttpDataSourceFactory(bandwidthMeter));
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
        return new DefaultDataSourceFactory(context, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(context, "ExoPlayerDemo"), bandwidthMeter);
    }


    @Override
    public void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    public void play() {
        if(player != null){
            player.setPlayWhenReady(true);
        }
    }

    @Override
    public void pause() {
        if(player != null){
            player.setPlayWhenReady(false);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        s("onDetach");
    }

    private void s(String message){
        Log.e(TAG,message);
    }

    public static final String TAG = "MainPagerFragment";


}
