package com.allplayers.android;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.allplayers.objects.PhotoData;
import com.allplayers.rest.RestApiV1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class PhotoDisplayActivity extends Activity implements OnTouchListener {
    float  downYValue;
    private ViewFlipper slider;
    private ArrayList<ImageView> photos;
    private int currentPhotoIndex = 1;

    /**
     * The current photo displayed.
     */
    private PhotoData mPhoto;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photodisplay);

        slider = (ViewFlipper)findViewById(R.id.Slider);

        photos = new ArrayList<ImageView>();
        // Create ImageViews and set their on touch listeners.
        photos.add((ImageView)findViewById(R.id.firstPhoto));
        photos.add((ImageView)findViewById(R.id.secondPhoto));
        photos.add((ImageView)findViewById(R.id.thirdPhoto));
        photos.get(0).setOnTouchListener((OnTouchListener) PhotoDisplayActivity.this);
        photos.get(1).setOnTouchListener((OnTouchListener) PhotoDisplayActivity.this);
        photos.get(2).setOnTouchListener((OnTouchListener) PhotoDisplayActivity.this);
        slider.setDisplayedChild(1);

        mPhoto = (new Router(this)).getIntentPhoto();
        String photoUrl = mPhoto.getPhotoFull();

        // Set the initial 3 images with the current one in the center.
        new GetRemoteImageTask().execute(photoUrl, 1);
        if (mPhoto.nextPhoto() != null) {
            new GetRemoteImageTask().execute(mPhoto.nextPhoto().getPhotoFull(), 2);
        }
        if (mPhoto.previousPhoto() != null) {
            new GetRemoteImageTask().execute(mPhoto.previousPhoto().getPhotoFull(), 0);
        }
    }

    public boolean onTouch(View arg0, MotionEvent arg1) {
        switch (arg1.getAction()) {
        case MotionEvent.ACTION_DOWN: {
            downYValue = arg1.getX();
            break;
        }

        case MotionEvent.ACTION_UP: {
            float currentX = arg1.getX();

            if (downYValue < currentX) {

                //change image
                if (mPhoto.previousPhoto() != null) {
                    slider.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left));
                    slider.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_to_right));

                    mPhoto = mPhoto.previousPhoto();
                    PhotoData photoToLoad = mPhoto.previousPhoto();
                    int nextPhotoIndex = (currentPhotoIndex + 1) % 3;

                    if (mPhoto.previousPhoto() != null) {
                        new GetRemoteImageTask().execute(photoToLoad.getPhotoFull(), nextPhotoIndex);
                    }
                    currentPhotoIndex--;
                    if (currentPhotoIndex == -1) currentPhotoIndex = 2;
                    slider.setDisplayedChild(currentPhotoIndex);
                }


            }

            if (downYValue > currentX) {

                //change image
                if (mPhoto.nextPhoto() != null) {
                    slider.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right));
                    slider.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_to_left));

                    mPhoto = mPhoto.nextPhoto();
                    int nextPhotoIndex = currentPhotoIndex - 1;
                    if (nextPhotoIndex == -1) nextPhotoIndex = 2;
                    PhotoData photoToLoad = mPhoto.nextPhoto();
                    GetRemoteImageTask helper = new GetRemoteImageTask();

                    if (mPhoto.nextPhoto() != null)
                        helper.execute(photoToLoad.getPhotoFull(), nextPhotoIndex);

                    currentPhotoIndex = (currentPhotoIndex + 1) % 3;
                    slider.setDisplayedChild(currentPhotoIndex);
                }
            }

            break;
        }
        }

        return true;
    }

    /*
     * Get's a user's image using a rest call and displays it.
     */
    public class GetRemoteImageTask extends AsyncTask<Object, Void, Bitmap> {
        int index;
        protected Bitmap doInBackground(Object... photoUrl) {
            index = ((Integer) photoUrl[1]);
            return RestApiV1.getRemoteImage((String) photoUrl[0]);
        }

        protected void onPostExecute(Bitmap image) {
            photos.get(index).setImageBitmap(image);
        }
    }
}
