package com.p5m.puzzledroid;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;

//public class PuzzlePiece extends android.support.v7.widget.AppCompatImageView {
public class PieceController extends AppCompatImageView {
    public int x;
    public int y;
    public int width;
    public int height;
    public boolean movable = true;

    public PieceController(Context context) {
        super(context);

    }
}