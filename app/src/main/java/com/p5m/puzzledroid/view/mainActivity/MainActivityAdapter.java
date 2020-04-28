package com.p5m.puzzledroid.view.mainActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.p5m.puzzledroid.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import timber.log.Timber;

/**
 * Manage the adapter associated with the MainActivity GridView.
 */
public class MainActivityAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> imagePaths;

    public MainActivityAdapter(Context ctxt, ArrayList<String> images) {
        Timber.i("MainActivityAdapter");
        context = ctxt;
        imagePaths = images;
    }

    public int getCount() {
        return imagePaths.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    /**
     * Display each item of the GridView.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.grid_element, parent, false);
        }
        // Get current image to be displayed
        String currentImage = imagePaths.get(position);
        // Add it to the ImageView
        ImageView imageView = convertView.findViewById(R.id.gridImageView);
        Timber.i("ImageAdapter currentImage: " + currentImage);
        File imgFile = new File(currentImage);
        if(imgFile.exists()) {
            Timber.i("Exists.");
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        } else {
            Timber.i("Does not exist.");
        }
        return convertView;
    }
}