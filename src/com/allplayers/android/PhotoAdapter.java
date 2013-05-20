package com.allplayers.android;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.allplayers.objects.PhotoData;
import com.allplayers.rest.RestApiV1;

public class PhotoAdapter extends BaseAdapter {
    private List<PhotoData> mPhotos = new ArrayList<PhotoData>();
    private Context mContext;
    private LruCache<String, Bitmap> mImageCache;
    public PhotoAdapter(Context context, List<PhotoData> objects) {
        mContext = context;
        mPhotos = objects;
        mPhotos.size();

        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mImageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
            }
        };
    }

    public PhotoAdapter(Context context) {
        mContext = context;
    }

    public void add(PhotoData photo) {
        mPhotos.add(photo);
    }

    public void addAll(List<PhotoData> photoObjects) {
        mPhotos.addAll(photoObjects);
        photoObjects.size();
    }

    public int getCount() {
        return mPhotos.size();
    }

    public PhotoData getItem(int index) {
        return mPhotos.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image;
        if (convertView != null) {
            image = (ImageView) convertView;
            if (mImageCache.get(position + "") != null) {
                image.setImageBitmap(mImageCache.get(position + ""));
                return image;
            } else {
                new GetRemoteImageTask(image, position).execute(mPhotos.get(position).getPhotoThumb());
            }
        } else {
            image = new ImageView(mContext);
            new GetRemoteImageTask(image, position).execute(mPhotos.get(position).getPhotoThumb());
        }
        image.setLayoutParams(new GridView.LayoutParams(140, 140));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setPadding(5, 5, 5, 5);
        return image;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    /**
     * Get's a user's image using a rest call and displays it.
     */
    public class GetRemoteImageTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> viewReference;
        private int index;

        GetRemoteImageTask(ImageView im, int ind) {
            viewReference = new WeakReference<ImageView>(im);
            index = ind;
        }

        /**
         * Gets the requested image using a REST call.
         * @param photoUrl: The URL of the photo to fetch.
         */
        protected Bitmap doInBackground(String... photoUrl) {
            Bitmap b = RestApiV1.getRemoteImage(photoUrl[0]);
            mImageCache.put(index + "", b);
            return b;
        }

        /**
         * Adds the fetched image to an array of the album's images.
         * @param image: The image to be added.
         */
        protected void onPostExecute(Bitmap bm) {
            ImageView imageView = viewReference.get();
            if (imageView != null) {
                Log.d("PHOTO", "SETTING BITMAP");
                imageView.setImageBitmap(bm);
            }
        }
    }
}