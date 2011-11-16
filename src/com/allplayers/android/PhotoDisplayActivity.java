package com.allplayers.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class PhotoDisplayActivity extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photodisplay);
		
		PhotoData photo = Globals.currentPhoto;
		String photoUrl = photo.getPhotoFull();
		
		Bitmap image = Globals.getRemoteImage(photoUrl);
		
		ImageView imView = (ImageView)findViewById(R.id.fullPhotoDisplay);
		imView.setImageBitmap(image);
	}
}