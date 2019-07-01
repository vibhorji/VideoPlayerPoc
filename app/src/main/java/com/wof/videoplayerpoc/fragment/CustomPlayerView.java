package com.wof.videoplayerpoc.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.exoplayer2.ui.PlayerView;

public class CustomPlayerView extends PlayerView {
    public CustomPlayerView(Context context) {
        super(context);
    }

    public CustomPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        performClick();
        return false;
    }
}
