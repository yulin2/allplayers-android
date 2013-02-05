package com.allplayers.android;

import java.util.ArrayList;
import java.util.List;

import com.allplayers.android.PhotoDisplayActivity.GetRemoteImageTask;
import com.allplayers.objects.PhotoData;
import com.allplayers.rest.RestApiV1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PhotoPager extends Activity {
    private ViewPager mViewPager;
    private PhotoPagerAdapter photoAdapter;
    private PhotoData currentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentPhoto = (new Router(this)).getIntentPhoto();
        mViewPager = new ViewPager(this);
        setContentView(mViewPager);
        photoAdapter = new PhotoPagerAdapter(this, currentPhoto);
        mViewPager.setAdapter(photoAdapter);
    }


    public class PhotoPagerAdapter extends PagerAdapter {
    	private Context mContext;
    	int currentPhotoIndex;
        private List<ImageView> images;
        private List<PhotoData> photos;
        
        public PhotoPagerAdapter(Context context, PhotoData item) {
            mContext = context;
            photos = new ArrayList<PhotoData>();
            images = new ArrayList<ImageView>();
           
            PhotoData temp = item;
            while(temp.previousPhoto() != null) {
            	photos.add(0, temp.previousPhoto());
            	temp = temp.previousPhoto();
            	System.out.println("Added a photo before.");
            }
            int currentPhotoIndex = photos.size();
            photos.add(item);
            temp = item;
            while(temp.nextPhoto() != null) {
            	photos.add(temp.nextPhoto());
            	temp = temp.nextPhoto();
            	System.out.println("Added a photo after.");
            }
            
            mViewPager.setCurrentItem(currentPhotoIndex);
        }

        /*public PhotoPagerAdapter(PhotoPagerAdapter orig, int newCenter) {
            images = orig.images;
            photos = orig.photos;
            mContext = orig.mContext;
            ImageView image = new ImageView(mContext);
            switch (newCenter) {
            	// Remove the end image and add a new blank image to the beginning.
            	case 0: {
            		images.remove(2);
            		images.add(0, image);
            		photos.remove(2);
            		photos.add(0, photos.get(0).previousPhoto());
            		new GetRemoteImageTask().execute(photos.get(0).getPhotoFull(), 0);
            	}
            	// Remove the first image and add a new blank image to the end.
            	case 2: {
            		images.remove(0);
            		images.add(image);
            		photos.remove(0);
            		photos.add(photos.get(2).nextPhoto());
            		new GetRemoteImageTask().execute(photos.get(2).getPhotoFull(), 2);
            	}
            }
        }*/
        
        @Override
        public Object instantiateItem(View collection, int position) {
            ImageView image = new ImageView(PhotoPager.this);
            images.add(position, image);
            new GetRemoteImageTask().execute(photos.get(position).getPhotoFull(), position);
            ((ViewPager) collection).addView(images.get(position),0);  
            return images.get(position);
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
            	images.get(index).setImageBitmap(image);
            }
        }
    }
}
