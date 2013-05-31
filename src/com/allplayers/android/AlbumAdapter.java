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

public class AlbumAdapter extends ArrayAdapter<AlbumData> {
    private LruCache<String, Bitmap> mImageCache;
    private TextView mAlbumTitle;
    private TextView mAlbumExtraInfo;
    private List<AlbumData> mAlbums = new ArrayList<AlbumData>();

    public AlbumAdapter(Context context, int textViewResourceId, List<AlbumData> objects) {
        super(context, textViewResourceId, objects);
        mAlbums = objects;
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

    public int getCount() {
        return mAlbums.size();
    }

    public AlbumData getItem(int index) {
        return mAlbums.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.albumlistitem, parent, false);

        //Get item
        AlbumData album = getItem(position);

        //Get reference to ImageView
        ImageView image = (ImageView)row.findViewById(R.id.albumCoverPhoto);
        image.setImageResource(R.drawable.backgroundstate);

        //Get reference to TextView - albumTitle
        mAlbumTitle = (TextView)row.findViewById(R.id.albumTitle);

        //Get reference to TextView - albumExtraInfo
        mAlbumExtraInfo = (TextView)row.findViewById(R.id.albumExtraInfo);

        //Set album title
        mAlbumTitle.setText(album.getTitle());

        if (mImageCache.get(position + "") != null) {
            image.setImageBitmap(mImageCache.get(position + ""));
        } else {
            //Set cover photo icon
            String imageURL = album.getCoverPhoto();

            if (!imageURL.trim().equals("")) {
                new GetRemoteImageTask(image, position).execute(imageURL);
            }
        }

        //@TODO: Fix API bug causing the incorrect number of photos in an album to be displayed.
        //Set extra info
        //albumExtraInfo.setText(album.getPhotoCount() + " photos in this album");
        return row;
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
                imageView.setImageBitmap(bm);
            }
        }
    }
}