package com.allplayers.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class PhotoDisplayActivity extends Activity implements OnTouchListener
{
	private float downYValue;
	private Button previousPhotoButton;
	private Button nextPhotoButton;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photodisplay);
		
		previousPhotoButton = (Button)findViewById(R.id.previousPhotoButton);
		nextPhotoButton = (Button)findViewById(R.id.nextPhotoButton);
		
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
		
		imView.setOnTouchListener((OnTouchListener) this);
		
		previousPhotoButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				//when clicked change
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
				//when clicked change
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
	
	public boolean onTouch(View arg0, MotionEvent arg1)
	{
		switch(arg1.getAction())
		{
			case MotionEvent.ACTION_DOWN:
			{
				downYValue = arg1.getY();
				break;
			}

			case MotionEvent.ACTION_UP:
			{
				float currentY = arg1.getY();
				
				if(downYValue > currentY)
				{
					ViewFlipper slider = (ViewFlipper) findViewById(R.id.Slider);
					slider.setAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
					slider.setAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
					
					//change image
					if(Globals.currentPhoto.previousPhoto() != null)
					{
						Globals.currentPhoto = Globals.currentPhoto.previousPhoto();
					}
					
					PhotoData photo = Globals.currentPhoto;
					String photoUrl = photo.getPhotoFull();
					Bitmap image = Globals.getRemoteImage(photoUrl);
					
					ImageView imView = (ImageView)findViewById(R.id.fullPhotoDisplay);
					imView.setImageBitmap(image);
					
					//Check for nulls after slide trigger
					if(Globals.currentPhoto.previousPhoto() == null)
					{
						previousPhotoButton.setVisibility(View.INVISIBLE);
					}
					if(Globals.currentPhoto.nextPhoto() != null)
					{
						nextPhotoButton.setVisibility(View.VISIBLE);
					}
				}

				if(downYValue < currentY)
				{
					ViewFlipper slider = (ViewFlipper) findViewById(R.id.Slider);
					slider.setAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
					slider.setAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
					
					//change image
					if(Globals.currentPhoto.nextPhoto() != null)
					{
						Globals.currentPhoto = Globals.currentPhoto.nextPhoto();
					}
					
					PhotoData photo = Globals.currentPhoto;
					String photoUrl = photo.getPhotoFull();
					Bitmap image = Globals.getRemoteImage(photoUrl);
					
					ImageView imView = (ImageView)findViewById(R.id.fullPhotoDisplay);
					imView.setImageBitmap(image);
					
					//Check for nulls after slide trigger
					if(Globals.currentPhoto.nextPhoto() == null)
					{
						nextPhotoButton.setVisibility(View.INVISIBLE);
					}
					if(Globals.currentPhoto.previousPhoto() != null)
					{
						previousPhotoButton.setVisibility(View.VISIBLE);
					}
				}
				
				break;
			}
		}

		return true;
	}
}