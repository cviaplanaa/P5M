package com.p5m.puzzledroid;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;

//public class PuzzlePiece extends android.support.v7.widget.AppCompatImageView {
public class PuzzlePiece extends AppCompatImageView {
    public int xCoord;
    public int yCoord;
    public int pieceWidth;
    public int pieceHeight;
    public boolean canMove = true;

    public PuzzlePiece(Context context) {
        super(context);

    }
}