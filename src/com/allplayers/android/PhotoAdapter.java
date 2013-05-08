package com.allplayers.android;

import com.allplayers.objects.PhotoData;
import com.allplayers.rest.RestApiV1;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PhotoAdapter extends BaseAdapter {
    private ImageView[] mPhotosImages;
    private List<PhotoData> mPhotos = new ArrayList<PhotoData>();
    private Context mContext;
    private int mPhotoCount = 0;

    public PhotoAdapter(Context context, List<PhotoData> objects) {
        mContext = context;
        this.mPhotos = objects;
    }

    public PhotoAdapter(Context context) {
        mContext = context;
    }

    public void add(PhotoData photo) {
        mPhotos.add(photo);
        mPhotoCount++;
    }

    public void addAll(List<PhotoData> photoObjects) {
        mPhotos.addAll(photoObjects);
        mPhotoCount += photoObjects.size();
        mPhotosImages = new ImageView[mPhotoCount];
    }

    public int getCount() {
        return mPhotos.size();
    }

    public PhotoData getItem(int index) {
        return mPhotos.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image;
        image = new ImageView(mContext);
        image.setLayoutParams(new GridView.LayoutParams(140, 140));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setPadding(5, 5, 5, 5);
        if (mPhotosImages[position] != null) {
            return mPhotosImages[position];
        }
        //Get item
        PhotoData photo = getItem(position);

        //Get reference to ImageView
        mPhotosImages[position] = image;

        //Set cover photo icon
        String imageURL = photo.getPhotoThumb();
        if (!imageURL.trim().equals("")) {
            new GetRemoteImageTask(position).execute(photo);
        }
        return mPhotosImages[position];
    }

    /*
     * Gets a user's image using a rest call.
     */
    public class GetRemoteImageTask extends AsyncTask<PhotoData, Void, Bitmap> {
        int row;

        public GetRemoteImageTask(int r) {
            this.row = r;
        }

        protected Bitmap doInBackground(PhotoData... photos) {
            PhotoData photo = (PhotoData)photos[0];
            return RestApiV1.getRemoteImage(photo.getPhotoThumb());
        }

        protected void onPostExecute(Bitmap bitmap) {
            mPhotosImages[row].setImageBitmap(bitmap);
        }
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }
}