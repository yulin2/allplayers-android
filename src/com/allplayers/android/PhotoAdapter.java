package com.allplayers.android;

import com.allplayers.objects.PhotoData;
import com.allplayers.rest.RestApiV1;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoAdapter extends ArrayAdapter<PhotoData> {
    private ImageView photoImage;
    private TextView photoTitle;
    private TextView photoExtraInfo;
    private List<PhotoData> photos = new ArrayList<PhotoData>();

    public PhotoAdapter(Context context, int textViewResourceId, List<PhotoData> objects) {
        super(context, textViewResourceId, objects);
        this.photos = objects;
    }

    public int getCount() {
        return photos.size();
    }

    public PhotoData getItem(int index) {
        return photos.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            //Row Inflation
            LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.photolistitem, parent, false);
        }

        //Get item
        PhotoData photo = getItem(position);

        //Get reference to ImageView
        photoImage = (ImageView)row.findViewById(R.id.photoImage);

        //Get reference to TextView - albumTitle
        photoTitle = (TextView)row.findViewById(R.id.photoTitle);

        //Get reference to TextView - albumExtraInfo
        photoExtraInfo = (TextView)row.findViewById(R.id.photoExtraInfo);

        //Set album title
        photoTitle.setText(photo.getTitle());

        //Set cover photo icon
        String imageURL = photo.getPhotoThumb();

        if (!imageURL.trim().equals("")) {
            Bitmap bitmap = RestApiV1.getRemoteImage(photo.getPhotoThumb());
            photoImage.setImageBitmap(bitmap);
        }

        //Set extra info
        photoExtraInfo.setText("");
        return row;
    }
}