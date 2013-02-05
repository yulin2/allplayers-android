package com.allplayers.android;

import java.util.ArrayList;
import java.util.List;

import com.allplayers.android.PhotoDisplayActivity.GetRemoteImageTask;
import com.allplayers.objects.PhotoData;
import com.allplayers.rest.RestApiV1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PhotoPager extends Activity {
	private LruCache<String, Bitmap> mMemoryCache;
    private ViewPager mViewPager;
    private PhotoPagerAdapter photoAdapter;
    private PhotoData currentPhoto;
	int currentPhotoIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @SuppressLint("NewApi")
			@Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        
        currentPhoto = (new Router(this)).getIntentPhoto();
        mViewPager = new ViewPager(this);
        setContentView(mViewPager);
        photoAdapter = new PhotoPagerAdapter(this, currentPhoto);
        mViewPager.setAdapter(photoAdapter);
        mViewPager.setCurrentItem(currentPhotoIndex);
    }


    public class PhotoPagerAdapter extends PagerAdapter {
    	private Context mContext;
        private ImageView[] images;
        private List<PhotoData> photos;

        public PhotoPagerAdapter(Context context, PhotoData item) {
            mContext = context;
            photos = new ArrayList<PhotoData>();
           
            PhotoData temp = item;
            while(temp.previousPhoto() != null) {
            	photos.add(0, temp.previousPhoto());
            	temp = temp.previousPhoto();
            	System.out.println("Added a photo before.");
            }
            currentPhotoIndex = photos.size();
            photos.add(item);
            temp = item;
            while(temp.nextPhoto() != null) {
            	photos.add(temp.nextPhoto());
            	temp = temp.nextPhoto();
            	System.out.println("Added a photo after.");
            }
            System.out.println("Current Photo Index = " + currentPhotoIndex);
            images = new ImageView[photos.size()];
        }
        
        @Override
        public Object instantiateItem(View collection, int position) {
            ImageView image = new ImageView(PhotoPager.this);
            images[position] = image;
            Bitmap bm = mMemoryCache.get("photo"+position);
            if(bm != null) {
            	images[position].setImageBitmap(bm);
                ((ViewPager) collection).addView(images[position],0);  
            	return images[position];
            }
            images[position] = image;
            new GetRemoteImageTask().execute(photos.get(position).getPhotoFull(), position);
            ((ViewPager) collection).addView(images[position],0);  
            return images[position];
        }
        
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        	((ViewPager) container).removeView((ImageView) object);
        }
        
        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        /**
         * Get's a user's image using a rest call and displays it.
         */
        public class GetRemoteImageTask extends AsyncTask<Object, Void, Bitmap> {
        	int index;
            protected Bitmap doInBackground(Object... photoUrl) {
            	index = (Integer) photoUrl[1];
                return RestApiV1.getRemoteImage((String) photoUrl[0]);
            }

            protected void onPostExecute(Bitmap image) {
            	mMemoryCache.put("photo"+index, image);
            	images[index].setImageBitmap(image);
            }
        }
    }
}
