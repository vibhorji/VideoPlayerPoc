package com.wof.videoplayerpoc.recycledemo;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
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
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.wof.videoplayerpoc.MainActivity;
import com.wof.videoplayerpoc.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 2000;
    private PlayerView playerView;
    private ProgressBar loading;
    private Context mContext;
    private ExoPlayer player;
    private String mUrl = "https://mobiletak-pdelivery.akamaized.net/mobiletv/video/2019_04/vod_26_apr_19_chacha_2704_mt_1024/vod_26_apr_19_chacha_2704_mt_1024.m3u8";
    private DataSource.Factory mediaDataSourceFactory;
    private Handler mainHandler;

    private static final DefaultBandwidthMeter BANDWIDTH_METER =
            new DefaultBandwidthMeter();
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.freagment_main_pager, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.build();
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        PlayerView playerView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.video_view);
        }

        private void build(){
            mediaDataSourceFactory = buildDataSourceFactory2(false);
            mainHandler = new Handler();
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            RenderersFactory renderersFactory = new DefaultRenderersFactory(mContext);

            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            LoadControl loadControl = new DefaultLoadControl();

            //    LoadControl loadControl = new DefaultLoadControl.Builder().setTargetBufferBytes()


            player = ExoPlayerFactory.newSimpleInstance(mContext,renderersFactory, trackSelector, loadControl);

            playerView.setPlayer(player);
            playerView.setUseController(true);
            player.seekTo(currentWindow, playbackPosition);

            // playerView.requestFocus();

            Uri uri = Uri.parse(mUrl);

            MediaSource mediaSource = buildMediaSource(uri, null);

            player.prepare(mediaSource);
            player.setPlayWhenReady(false);
        }
    }

    DataSource.Factory buildDataSourceFactory2(boolean cache) {

      /*  if (!cache) {
            return new DefaultDataSourceFactory(mContext, BANDWIDTH_METER,
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
        }*/

        return new DefaultDataSourceFactory(mContext, BANDWIDTH_METER,
                buildHttpDataSourceFactory(BANDWIDTH_METER));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(mContext, "ExoPlayerDemo"), bandwidthMeter);
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




}
