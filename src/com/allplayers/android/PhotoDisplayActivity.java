package com.allplayers.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PhotoDisplayActivity extends Activity
{
	Button previousPhotoButton;
	Button nextPhotoButton;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photodisplay);
		
		previousPhotoButton = (Button)findViewById(R.id.previousPhotosButton);
		nextPhotoButton = (Button)findViewById(R.id.nextPhotosButton);
		
		PhotoData photo = Globals.currentPhoto;
		String photoUrl = photo.getPhotoFull();

		//Check for nulls during picture load up
		if(Globals.currentPhoto.previousPhoto() == null)
		{
			previousPhotoButton.setVisibility(View.INVISIBLE);            	
		}
		if(Globals.currentPhoto.nextPhoto() == null)
		{
			nextPhotoButton.setVisibility(View.INVISIBLE);            	
		}


		Bitmap image = Globals.getRemoteImage(photoUrl);

		ImageView imView = (ImageView)findViewById(R.id.fullPhotoDisplay);
		imView.setImageBitmap(image);

		previousPhotoButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				if(Globals.currentPhoto.previousPhoto() != null)
				{
					Globals.currentPhoto = Globals.currentPhoto.previousPhoto();            	
				}

				PhotoData photo = Globals.currentPhoto;
				String photoUrl = photo.getPhotoFull();
				Bitmap image = Globals.getRemoteImage(photoUrl);

				ImageView imView = (ImageView)findViewById(R.id.fullPhotoDisplay);
				imView.setImageBitmap(image);  

				//Check for nulls after button trigger
				if(Globals.currentPhoto.previousPhoto() == null)
				{
					previousPhotoButton.setVisibility(View.INVISIBLE);            	
				}
				if(Globals.currentPhoto.nextPhoto() != null)
				{
					nextPhotoButton.setVisibility(View.VISIBLE);            	
				}
			}
		});

		nextPhotoButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				if(Globals.currentPhoto.nextPhoto() != null)
				{
					Globals.currentPhoto = Globals.currentPhoto.nextPhoto();            	
				}

				PhotoData photo = Globals.currentPhoto;
				String photoUrl = photo.getPhotoFull();
				Bitmap image = Globals.getRemoteImage(photoUrl);

				ImageView imView = (ImageView)findViewById(R.id.fullPhotoDisplay);
				imView.setImageBitmap(image);           		

				//Check for nulls after button trigger
				if(Globals.currentPhoto.nextPhoto() == null)
				{
					nextPhotoButton.setVisibility(View.INVISIBLE);            	
				}
				if(Globals.currentPhoto.previousPhoto() != null)
				{
					previousPhotoButton.setVisibility(View.VISIBLE);            	
				}
			}
		});
	}
}