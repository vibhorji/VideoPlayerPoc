package com.wof.videoplayerpoc;

public interface ExoPlayerListener {
    void initializePlayer();
    void releasePlayer();
    void play();
    void pause();
}
