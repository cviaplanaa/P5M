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
import android.widget.TextView;

import com.p5m.puzzledroid.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import timber.log.Timber;

/**
 * Adapter for the MainActivity gridView
 */
public class MainActivityAdapter extends BaseAdapter {
    private Context context;
    private List<String> images;

    public MainActivityAdapter(Context c, List<String> imagesList) {
        Timber.i("ImageController");
        context = c;
        images = imagesList;
    }

    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.grid_element, parent, false);
        }
        // Get current image to be displayed
        String currentImage = images.get(position);
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

    // Create a new ImageView for each item referenced by the Adapter
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        Timber.i("getView");
//        if (convertView == null) {
//            final LayoutInflater layoutInflater = LayoutInflater.from(context);
//            convertView = layoutInflater.inflate(R.layout.grid_element, null);
//        }
//
//        final ImageView imageView = convertView.findViewById(R.id.gridImageview);
//        imageView.setImageBitmap(null);
//        // run image related code after the view was laid out
//        imageView.post(new Runnable() {
//            @Override
//            public void run() {
//                new AsyncTask<Void, Void, Void>() {
//                    private Bitmap bitmap;
//                    @Override
//                    protected Void doInBackground(Void... voids) {
////                        bitmap = getImageFromAssets(imageView, images.get(position));
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//                        super.onPostExecute(aVoid);
////                        imageView.setImageBitmap(bitmap);
//                        File imgFile = new  File(images.get(position));
//                        if(imgFile.exists()) {
//                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                            imageView.setImageBitmap(myBitmap);
//                        }
//                    }
//                }.execute();
//            }
//        });
//        return convertView;
//    }

//    private Bitmap getImageFromAssets(ImageView imageView, String assetName) {
//        Timber.i("getImageFromAssets");
//        // Get the dimensions of the View
//        int targetW = imageView.getWidth();
//        int targetH = imageView.getHeight();
//
//        if(targetW == 0 || targetH == 0) {
//            // view has no dimensions set
//            return null;
//        }
//
//        try {
//            InputStream is = myAssetManager.open("img/" + assetName);
//            // Get the dimensions of the bitmap
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            bmOptions.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(is, new Rect(-1, -1, -1, -1), bmOptions);
//            int photoW = bmOptions.outWidth;
//            int photoH = bmOptions.outHeight;
//
//            // Determine how much to scale down the image
//            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//            is.reset();
//
//            // Decode the image file into a Bitmap sized to fill the View
//            bmOptions.inJustDecodeBounds = false;
//            bmOptions.inSampleSize = scaleFactor;
//            bmOptions.inPurgeable = true;
//
//            return BitmapFactory.decodeStream(is, new Rect(-1, -1, -1, -1), bmOptions);
//        } catch (IOException e) {
//            e.printStackTrace();
//
//            return null;
//        }
//    }
}