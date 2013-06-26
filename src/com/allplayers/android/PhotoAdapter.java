package com.allplayers.android;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.allplayers.objects.PhotoData;
import com.allplayers.rest.RestApiV1;

/**
 * Custom ArrayAdapter for photos.
 */
public class PhotoAdapter extends BaseAdapter {
    private List<PhotoData> mPhotos = new ArrayList<PhotoData>();
    private Context mContext;
    private LruCache<String, Bitmap> mImageCache;
    
    /**
     * Constructor.
     * 
     * @param context The current context.
     * @param objects The objects to represent in the ListView.
     */
    public PhotoAdapter(Context context, List<PhotoData> objects) {
        mContext = context;
        mPhotos = objects;
        mPhotos.size();

        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mImageCache = new LruCache<String, Bitmap>(cacheSize) {
            
            /**
             * Returns the size of the entry for key and value in user-defined units.
             */
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
            }
        };
    }

    /**
     * Constructor.
     * 
     * @param The current context.
     */
    public PhotoAdapter(Context context) {
        mContext = context;
    }

    /**
     * Adds the passed in PhotoData object to the ArrayList.
     * 
     * @param photo The PhotoData object to be added to the ArrayList.
     */
    public void add(PhotoData photo) {
        mPhotos.add(photo);
    }

    /**
     * Adds a list of PhotoData objects to the ArrayList.
     * 
     * @param photoObjects The list of PhotoData objects to be added to the ArrayList.
     */
    public void addAll(List<PhotoData> photoObjects) {
        mPhotos.addAll(photoObjects);
        photoObjects.size();
    }

    /**
     * Returns the size of the list used to populate the adapter.
     * 
     * @return The size of the list used to populate the adapter.
     */
    @Override
    public int getCount() {
        return mPhotos.size();
    }

    /**
     * Returns the item at the specified index.
     * 
     * @param index The index at which to fetch the item.
     * @return The item at the specified index.
     */
    @Override
    public PhotoData getItem(int index) {
        return mPhotos.get(index);
    }

    /**
     * Returns the view at the specified position.
     * 
     * @param position The position at which to fetch the view.
     * @param convertView The view to be converted for the purpose of holding the fetched view.
     * @param parent The parent ViewGroup of the fetched view.
     * @return The view at the specified position.
     */
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

    /**
     * Get the row id associated with the specified position in the list.
     * 
     * @param position The position of the item within the adapter's data set whose row id we want.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Get's a user's image and displays it in the list item.
     */
    public class GetRemoteImageTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> viewReference;
        private int index;

        /**
         * Constructor
         * 
         * @param im The imageview to populate with the fetched image.
         * @param ind The index at which the imageview lies.
         */
        GetRemoteImageTask(ImageView im, int ind) {
            viewReference = new WeakReference<ImageView>(im);
            index = ind;
        }

        /**
         * Gets the requested image.
         * 
         * @param photoUrl The URL of the photo to fetch.
         */
        @Override
        protected Bitmap doInBackground(String... photoUrl) {
            Bitmap b = RestApiV1.getRemoteImage(photoUrl[0]);
            mImageCache.put(index + "", b);
            return b;
        }

        /**
         * Adds the fetched image to an array of the album's images.
         * 
         * @param image: The image to be added.
         */
        @Override
        protected void onPostExecute(Bitmap bm) {
            ImageView imageView = viewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bm);
            }
        }
    }
}