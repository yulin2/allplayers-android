package com.allplayers.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allplayers.objects.AlbumData;
import com.allplayers.rest.RestApiV1;

public class AlbumAdapter extends ArrayAdapter<AlbumData> {
    private List<ImageView> mCoverPhotos = new ArrayList<ImageView>();
    private TextView mAlbumTitle;
    private TextView mAlbumExtraInfo;
    private List<AlbumData> mAlbums = new ArrayList<AlbumData>();

    public AlbumAdapter(Context context, int textViewResourceId, List<AlbumData> objects) {
        super(context, textViewResourceId, objects);
        this.mAlbums = objects;
    }

    public int getCount() {
        return mAlbums.size();
    }

    public AlbumData getItem(int index) {
        return mAlbums.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            //Row Inflation
            LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.albumlistitem, parent, false);
        }

        //Get item
        AlbumData album = getItem(position);

        //Get reference to ImageView
        mCoverPhotos.add(position, (ImageView)row.findViewById(R.id.albumCoverPhoto));

        //Get reference to TextView - albumTitle
        mAlbumTitle = (TextView)row.findViewById(R.id.albumTitle);

        //Get reference to TextView - albumExtraInfo
        mAlbumExtraInfo = (TextView)row.findViewById(R.id.albumExtraInfo);

        //Set album title
        mAlbumTitle.setText(album.getTitle());

        //Set cover photo icon
        String imageURL = album.getCoverPhoto();

        if (!imageURL.trim().equals("")) {
            new GetRemoteImageTask(position).execute(album);
        }

        //@TODO: Fix API bug causing the incorrect number of photos in an album to be displayed.
        //Set extra info
        //albumExtraInfo.setText(album.getPhotoCount() + " photos in this album");
        return row;
    }

    /*
     * Gets an albums cover photo with a rest call.
     */
    public class GetRemoteImageTask extends AsyncTask<AlbumData, Void, Bitmap> {
        int row;
        
        public GetRemoteImageTask(int r) {
        	this.row = r;
        }
        
        protected Bitmap doInBackground(AlbumData... albums) {
            AlbumData album = (AlbumData)albums[0];
            return RestApiV1.getRemoteImage(album.getCoverPhoto());
        }

        protected void onPostExecute(Bitmap bitmap) {
            mCoverPhotos.get(row).setImageBitmap(bitmap);
        }
    }
}