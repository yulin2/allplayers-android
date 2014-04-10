
package com.allplayers.android;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allplayers.objects.AlbumData;
import com.allplayers.rest.RestApiV1;

/**
 * Custom ArrayAdapter for photo albums.
 */
public class AlbumAdapter extends ArrayAdapter<AlbumData> {

    private List<AlbumData> mAlbums = new ArrayList<AlbumData>();
    private LruCache<String, Bitmap> mImageCache;
    private TextView mAlbumTitle;
    
    // Will be used once the API issue causing the incorrect number of photos in an album is solved.
    // TODO: Fix API bug causing the incorrect number of photos in an album to be displayed.
    @SuppressWarnings("unused")
    private TextView mAlbumExtraInfo;

    /**
     * Constructor.
     * 
     * @param context The current context.
     * @param textViewResourceId The resource ID for a layout file containing a
     *            TextView to use when instantiating views.
     * @param objects The objects to represent in the ListView.
     */
    public AlbumAdapter(Context context, int textViewResourceId, List<AlbumData> objects) {
        super(context, textViewResourceId, objects);
        
        mAlbums = objects;
        
        // Set up the cache.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
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
     * Returns the size of the array used to populate the adapter.
     * 
     * @return The size of the array used to populate the adapter.
     */
    @Override
    public int getCount() {
        return mAlbums.size();
    }

    /**
     * Returns the item at the specified index.
     * 
     * @param index The index at which to fetch the item.
     * @return The item at the specified index.
     */
    @Override
    public AlbumData getItem(int index) {
        return mAlbums.get(index);
    }

    /**
     * Returns the view at the specified position.
     * 
     * @param position The position at which to fetch the view.
     * @param convertView The view to be converted for the purpose of holding the fetched view.
     * @param parent The parent ViewGroup of the fetched view.
     * @return The view at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.albumlistitem, parent, false);

        // Get item
        AlbumData album = getItem(position);

        // Get reference to ImageView
        ImageView image = (ImageView) row.findViewById(R.id.albumCoverPhoto);
        image.setImageResource(R.drawable.backgroundstate);

        // Get reference to TextView - albumTitle
        mAlbumTitle = (TextView) row.findViewById(R.id.albumTitle);

        // Get reference to TextView - albumExtraInfo
        mAlbumExtraInfo = (TextView) row.findViewById(R.id.albumExtraInfo);

        // Set album title
        mAlbumTitle.setText(album.getTitle());

        if (mImageCache.get(position + "") != null) {
            image.setImageBitmap(mImageCache.get(position + ""));
        } else {
            // Set cover photo icon
            String imageURL = album.getCoverPhoto();

            if (!imageURL.trim().equals("")) {
                new GetRemoteImageTask(image, position).execute(imageURL);
            }
        }

        // TODO: Fix API bug causing the incorrect number of photos in an album to be displayed.
        // This will allow us to show the number of photos in an album under the title. 
        
        // Set extra info
        // albumExtraInfo.setText(album.getPhotoCount() + " photos in this album");
        
        return row;
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
            
            //TODO unhardcode
            Bitmap b = RestApiV1.getRemoteImage(photoUrl[0], 1080, 1920);
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
