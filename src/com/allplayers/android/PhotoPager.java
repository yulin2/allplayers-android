package com.allplayers.android;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.allplayers.android.activities.AllplayersSherlockActivity;
import com.allplayers.objects.PhotoData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

/**
 * Displays an albums photos.
 */
public class PhotoPager extends AllplayersSherlockActivity {

    private PhotoData mCurrentPhoto;
    private PhotoPagerAdapter mPhotoAdapter;
    private ViewPager mViewPager;
    
    private int mCurrentPhotoIndex;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in
     * onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        if (savedInstanceState != null) {
            mCurrentPhotoIndex = savedInstanceState.getInt("photoToStart");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_pager);

        // Initialize variables.
        mCurrentPhoto = (new Router(this)).getIntentPhoto();
        mViewPager = new ViewPager(this);
        mPhotoAdapter = new PhotoPagerAdapter(this, mCurrentPhoto);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPhotoAdapter);
        mViewPager.setCurrentItem(mCurrentPhotoIndex);

        // Set up the ActionBar.
        mActionBar.setTitle(getIntent().getStringExtra("album title"));

        // Set up the Side Navigation Menu
        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so that the state
     * can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle) (the Bundle populated
     * by this method will be passed to both).
     * 
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mCurrentPhotoIndex = mViewPager.getCurrentItem();
        outState.putInt("photoToStart", mCurrentPhotoIndex);
    }

    /**
     * Custom PagerAdapter used to display photos.
     */
    public class PhotoPagerAdapter extends PagerAdapter {

        private LruCache<String, Bitmap> mImageCache;
        private Context mContext;
        private List<PhotoData> photos;

        /**
         * Constructor.
         * 
         * @param contex The current context.
         * @param item The PhotoData item to be displayed.
         */
        public PhotoPagerAdapter(Context context, PhotoData item) {
            mContext = context;
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

            photos = new ArrayList<PhotoData>();

            PhotoData temp = item;

            while (temp.previousPhoto() != null) {
                photos.add(0, temp.previousPhoto());
                temp = temp.previousPhoto();
            }

            if (mCurrentPhotoIndex == 0) {
                mCurrentPhotoIndex = photos.size();
            }

            photos.add(item);

            temp = item;

            while (temp.nextPhoto() != null) {
                photos.add(temp.nextPhoto());
                temp = temp.nextPhoto();
            }

        }

        /**
         * Create the page for the given position.
         * 
         * @param collection The containing View in which the page will be shown.
         * @param position The page position to be instantiated.
         * @return Returns an Object representing the new page.
         */
        @Override
        public Object instantiateItem(View collection, int position) {
            ImageView image = new ImageView(mContext);
            image.setImageResource(R.drawable.backgroundstate);

            if (mImageCache.get(position + "") != null) {
                image.setImageBitmap(mImageCache.get(position + ""));
                ((ViewPager) collection).addView(image, 0);
                return image;
            }
            new GetRemoteImageTask(image, position).execute(photos.get(position).getPhotoFull());
            ((ViewPager) collection).addView(image, 0);

            return image;
        }

        /**
         * Remove a page for the given position.
         * 
         * @param container The containing View from which the page will be removed.
         * @param position The page position to be removed.
         * @param object The same object that was returned by instantiateItem(View, int).
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }

        /**
         * Returns the number of views available.
         * 
         * @return The number of views available.
         */
        @Override
        public int getCount() {
            return photos.size();
        }

        /**
         * Determines whether a page View is associated with a specific key object as returned by
         * instantiateItem(ViewGroup, int). 
         * 
         * @param view Page View to check for association with obj.
         * @param obj Object to check for association with view.
         * @return Returns true if view is associated with the key object object.
         */
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        /**
         * Get's a user's image..
         */
        public class GetRemoteImageTask extends AsyncTask<String, Void, Bitmap> {
            private final WeakReference<ImageView> viewReference;
            private int index;

            /**
             * Constructor.
             * 
             * @param im The ImageView that will hold the fetched image.
             * @param ind The index of the ImageView that will hold the fetched image.
             */
            GetRemoteImageTask(ImageView im, int ind) {
                viewReference = new WeakReference<ImageView>(im);
                index = ind;
            }

            /**
             * Runs on the UI thread before doInBackground(Params...).
             */
            @Override
            protected void onPreExecute() {
                ((Activity) mContext).setProgressBarIndeterminateVisibility(true);
            }

            /**
             * Performs a computation on a background thread. Gets the requested image.
             * 
             * @param photoUrl The URL of the photo to fetch.
             * @return A Bitmap of the image that was fetched.
             */
            protected Bitmap doInBackground(String... photoUrl) {
                //TODO unhardcode
                Bitmap b = RestApiV1.getRemoteImage(photoUrl[0], 1080, 1920);
                mImageCache.put(index + "", b);
                return b;
            }

            /**
             * Runs on the UI thread after doInBackground(Params...). The specified result is the
             * value returned by doInBackground(Params...).Adds the fetched image to an array of the
             * album's images.
             * 
             * @param bm The image to be added.
             */
            protected void onPostExecute(Bitmap bm) {
                ((Activity) mContext).setProgressBarIndeterminateVisibility(false);
                ImageView imageView = viewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bm);
                }
            }
        }
    }
}
