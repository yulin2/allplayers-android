package com.allplayers.android;

import com.allplayers.objects.PhotoData;
import com.allplayers.rest.RestApiV1;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoAdapter extends BaseAdapter {
    private ImageView[] photoImage;
    private List<PhotoData> photos = new ArrayList<PhotoData>();
    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    private int numOfPhotosInAlbum = 0;

    public PhotoAdapter(Context context, List<PhotoData> objects) {
        mContext = context;
        this.photos = objects;
        createCache();
    }

    public PhotoAdapter(Context context) {
        mContext = context;
        createCache();
    }

    public void add(PhotoData photo) {
        photos.add(photo);
        numOfPhotosInAlbum++;
    }

    public void addAll(List<PhotoData> photoObjects) {
        photos.addAll(photoObjects);
        numOfPhotosInAlbum += photoObjects.size();
        photoImage = new ImageView[numOfPhotosInAlbum];
    }

    public int getCount() {
        return photos.size();
    }

    public PhotoData getItem(int index) {
        return photos.get(index);
    }

    public void createCache() {
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @SuppressLint("NewApi")
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                //System.out.println("Size is " +bitmap.getByteCount() / 1024);
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image;
        image = new ImageView(mContext);
        image.setLayoutParams(new GridView.LayoutParams(140, 140));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setPadding(5, 5, 5, 5);
        if (photoImage[position] != null) {
            return photoImage[position];
        }
        //Get item
        PhotoData photo = getItem(position);

        //Get reference to ImageView
        photoImage[position] = image;

        //Set cover photo icon
        String imageURL = photo.getPhotoThumb();
        if (!imageURL.trim().equals("")) {
            GetRemoteImageTask helper = new GetRemoteImageTask();
            helper.execute(photo, position);
        }

        return photoImage[position];
    }

    /*
     * Gets a user's image using a rest call.
     */
    public class GetRemoteImageTask extends AsyncTask<Object, Void, Bitmap> {
        int row;
        protected Bitmap doInBackground(Object... photos) {
            this.row = (Integer)photos[1];
            PhotoData photo = (PhotoData)photos[0];
            return RestApiV1.getRemoteImage(photo.getPhotoThumb());
        }

        protected void onPostExecute(Bitmap bitmap) {
            System.out.println("Loaded a new image");
            mMemoryCache.put("albumPhoto" + row, bitmap);
            photoImage[row].setImageBitmap(bitmap);
        }
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }
}