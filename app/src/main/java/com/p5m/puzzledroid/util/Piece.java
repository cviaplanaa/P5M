package com.p5m.puzzledroid.util;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;

import timber.log.Timber;

public class Piece extends AppCompatImageView {
    public int x;
    public int y;
    public int width;
    public int height;
    public boolean movable = true;

    public Piece(Context context) {
        super(context);
        Timber.i("PierceController");
    }
}