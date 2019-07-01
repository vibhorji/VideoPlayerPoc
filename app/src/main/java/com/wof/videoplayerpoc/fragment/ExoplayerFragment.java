package com.wof.videoplayerpoc.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
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
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.wof.videoplayerpoc.MainActivity;
import com.wof.videoplayerpoc.R;

import java.io.File;

public class ExoplayerFragment extends Fragment implements TransferListener, Player.EventListener {
    private boolean isViewShown = false;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 2000;
    private PlayerView playerView;
    private ProgressBar loading;
    private Context mContext;
    private ExoPlayer player;
    private String mUrl = "https://mobiletak-pdelivery.akamaized.net/mobiletv/video/2019_04/vod_26_apr_19_chacha_2704_mt_1024/vod_26_apr_19_chacha_2704_mt_1024.m3u8";
    //  private String mUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    //  private String mUrl = "https://www.youtube.com/watch?v=zKO0-J7bDcQ";
    //  public static String mUrl = "https://www.youtube.com/watch?v=HdccMD1D9dM";
    private DataSource.Factory mediaDataSourceFactory;
    private Handler mainHandler;
    boolean isPlayWhenReady;

    private String url[] = {"https://mobiletak-pdelivery.akamaized.net/mobiletv/video/2019_06/vod_26_jun_19_modi_2048/vod_26_jun_19_modi_2048.m3u8",
            "https://mobiletak-pdelivery.akamaized.net/mobiletv/video/2019_06/vod_26_jun_19_kd_1pm_2048/vod_26_jun_19_kd_1pm_2048.m3u8",
            "https://mobiletak-pdelivery.akamaized.net/mobiletv/video/2019_06/vod_26_jun_19_flood_2048/vod_26_jun_19_flood_2048.m3u8",
            "https://mobiletak-pdelivery.akamaized.net/mobiletv/video/2019_06/vod_26_jun_19_kd_1pm_2048/vod_26_jun_19_kd_1pm_2048.m3u8",
            "https://mobiletak-pdelivery.akamaized.net/mobiletv/video/2019_06/vod_26_jun_19_akash_2048/vod_26_jun_19_akash_2048.m3u8"
    };

    private String titileArray[] = {"राज्यसभा में फिर से पीएम मोदी के निशाने पर रहीं सोनिया और राहुल गांधी",
            "4 बजे का बुलेटिन, 26 जून",
            "मानसून की पहली बारिश ने ऐसे मचाई तबाही, पेड़ पर चढ़े लोग।",
            "3 बजे का बुलेटिन, 26 जून",
            "जब बीच सड़क पर बैट उठाकर मारपीट करने लगे कैशाल विजयवर्गीय के विधायक बेटे आकाश"};


    private String textArray[] = {"प्रधानमंत्री नरेंद्र मोदी ने राष्ट्रपति के अभिभाषण पर धन्यवाद प्रस्ताव पर चर्चा का जवाब देते हुए कांग्रेस के आरोपों का एक-एक कर जवाब दिया। साथ ही उन्होंने कांग्रेस अध्यक्ष राहुल गांधी पर कटाक्ष भी किए।",
            "अध्यक्ष पद छोड़ने पर फिर अड़े राहुल गांधी -पार्टी की बैठक में दोहराई अपनी बात- सांसदों की अपील बने रहे अध्यक्ष-कांग्रेस में विकल्प नहीं..गृहमंत्री अमित शाह का पहला कश्मीर दौरा..सुरक्षा के हालात पर करेंगे चर्चा- दौरे से पहले त्राल में आतंकियों से मुठभेड़-एक का सफाया..मुस्लिम के गटर वाले बवाल में फंसी कांग्रेस-तत्कालीन मंत्री आरिफ मोहम्मद ने राव के बयान को कबूला- बोले ओवैसी-अखलाक क्यों नहीं याद!",
            "मानसून की पहली बारिश..पानी में फंसी स्कूल वैन..बाढ़ ने जीना दुश्वार किया...खतरे में है ज़िन्दगी..हर तरफ पानी ही पानी",
            "अध्यक्ष पद छोड़ने पर फिर अड़े राहुल गांधी -पार्टी की बैठक में दोहराई अपनी बात- सांसदों की अपील बने रहे अध्यक्ष-कांग्रेस में विकल्प नहीं..गृहमंत्री अमित शाह का पहला कश्मीर दौरा..सुरक्षा के हालात पर करेंगे चर्चा- दौरे से पहले त्राल में आतंकियों से मुठभेड़-एक का सफाया..मुस्लिम के गटर वाले बवाल में फंसी कांग्रेस-तत्कालीन मंत्री आरिफ मोहम्मद ने राव के बयान को कबूला- बोले ओवैसी-अखलाक क्यों नहीं याद!",
            "इंदौर में बीजेपी विधायक बेकाबू हो गए। कैलाश विजयवर्गीय के विधायक बेटे आकाश विजयवर्गीय ने म्यूनिसिपल कॉरपोरेशन के अधिकारियों को हड़काया और इतने बेकाबू हो गए कि मारपीट पर उतर आए। दरअसल जर्जर मकान तोड़ने गई निगम अधिकारी और आकाश विजयवर्गीय के बीच बहस शुरु हुई और फिर निगम अधिकारी के पैर पर आकाश विजयवर्गीय ने क्रिकेट बैट से मारा"};

  /*  private String thumbnailList[] = {"https://akm-img-a-in.tosshub.com/sites/mobiletv/201906/modi-on-congerss_1561541501_640x360.jpg",
    "https://akm-img-a-in.tosshub.com/sites/mobiletv/201906/2506kd4_1561538512_640x360.jpeg",
    "https://akm-img-a-in.tosshub.com/sites/mobiletv/201906/flood_pkg_1561538398_640x360.jpeg",
    "https://akm-img-a-in.tosshub.com/sites/mobiletv/201906/2506kd3_1561537991_640x360.jpeg",
    "https://akm-img-a-in.tosshub.com/sites/mobiletv/201906/akash_1561537814_640x360.jpeg"};*/

    private int image[] = {R.drawable.modi_vertical, R.drawable.rahul, R.drawable.anjana, R.drawable.patra_3, R.drawable.crowd};
    private int imageResource;
    private static final DefaultBandwidthMeter BANDWIDTH_METER =
            new DefaultBandwidthMeter();
    private int positon;
    MediaSource mediaSource;

    public static ExoplayerFragment newInstance(int i) {
        ExoplayerFragment exoplayerFragment = new ExoplayerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", i);
        exoplayerFragment.setArguments(bundle);
        return exoplayerFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            positon = getArguments().getInt("position");
            mUrl = url[positon];
            imageResource = image[positon];
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.freagment_main_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerView = view.findViewById(R.id.video_view);
        loading = view.findViewById(R.id.loading);
        //  ((ImageView)view.findViewById(R.id.thumnail)).setImageResource(imageResource);
        ((TextView) view.findViewById(R.id.tv_title)).setText(titileArray[positon]);
        ((TextView) view.findViewById(R.id.tv_detail)).setText(textArray[positon]);


        Glide.with(getActivity()).load(image[positon]).into(((ImageView) view.findViewById(R.id.thumnail)));
        view.findViewById(R.id.exo_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null) {
                    player.setPlayWhenReady(true);
                    view.findViewById(R.id.fl_overlay).setVisibility(View.GONE);
                }
            }
        });



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

    private void initPlayerFinal() {
        mediaDataSourceFactory = buildDataSourceFactory2(false);
        mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        RenderersFactory renderersFactory = new DefaultRenderersFactory(mContext);

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        LoadControl loadControl = new DefaultLoadControl();

        //    LoadControl loadControl = new DefaultLoadControl.Builder().setTargetBufferBytes()


        player = ExoPlayerFactory.newSimpleInstance(mContext, renderersFactory, trackSelector, loadControl);

        playerView.setPlayer(player);
        playerView.setUseController(true);

        player.addListener(this);
        player.seekTo(currentWindow, playbackPosition);

        // playerView.requestFocus();

        Uri uri = Uri.parse(mUrl);


        mediaSource = buildMediaSource(uri, null);

        player.prepare(mediaSource);
        player.setPlayWhenReady(false);

    }

    private void maybeReportPlayerState() {
        boolean playWhenReady = player.getPlayWhenReady();

    }


    private void releasePlayerFinal() {

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

    public DataSource.Factory buildDataSourceFactory() {

        return new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(90 * 1024 * 1024);

                SimpleCache simpleCache = new SimpleCache(getCahceDir2(), evictor);

                DataSource dataSource = buildMyDataSourceFactory().createDataSource();
                return new CacheDataSource(simpleCache, dataSource, CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
            }
        };
    }



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

            if (!isViewShown) {
                initPlayerFinal();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            //   initializePlayer();
            //  initPlayer();
            if (!isViewShown) {
                initPlayerFinal();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            // releasePlayer();
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            //  releasePlayer();
            releasePlayer();
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


    public void onPauseFragment() {
        if (player != null) {
            if (getView() != null)
                getView().findViewById(R.id.fl_overlay).setVisibility(View.VISIBLE);
            player.setPlayWhenReady(false);
        }


    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED ||
                !playWhenReady) {

            playerView.setKeepScreenOn(false);
        } else { // STATE_IDLE, STATE_ENDED
            // This prevents the screen from getting dim/lock
            playerView.setKeepScreenOn(true);
        }

       // Toast.makeText(getActivity(), ""+playbackState, Toast.LENGTH_SHORT).show();
        switch (playbackState) {

            case Player.STATE_IDLE:

                player.getPlaybackState();
                player.prepare(mediaSource, false, false);
                break;
            case Player.STATE_BUFFERING:

                break;

            case Player.TIMELINE_CHANGE_REASON_PREPARED:


                break;
            case Player.STATE_READY:

                isPlayWhenReady = true;

                break;
            case Player.STATE_ENDED:


                player.prepare(mediaSource, true, true);

                break;

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            // Log.e("test","test");
            if (isPlayWhenReady) {
                isViewShown = true;
                player.setPlayWhenReady(false);
            } else {
                isViewShown = false;
            }

        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

        Toast.makeText(mContext, ""+error, Toast.LENGTH_SHORT).show();
        player.getPlaybackState();
    }
}