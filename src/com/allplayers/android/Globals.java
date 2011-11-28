package com.allplayers.android;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/* This class is used for storing global variables across activities.
 * Also, update timestamps should be kept to avoid making too many unnecessary calls, such as the 
 * getUserGroups() call for several activities.
 * 
 * Replace global data with database.
 */
public class Globals
{
	//public static ArrayList<GroupData> groupList = new ArrayList<GroupData>();
	public static GroupData currentGroup = new GroupData();
	public static AlbumData currentAlbum = new AlbumData();
	public static PhotoData currentPhoto = new PhotoData();
	public static EventData currentEvent = new EventData();
	public static MessageData currentMessage = new MessageData();
	
	public static ArrayList<GroupData> groupList = new ArrayList<GroupData>();
	
	public static String search = "";
	public static int zipcode = 00000;
	public static int distance = 10;
	
	public static Bitmap getRemoteImage(final String urlString)
	{
		try
		{
			URL url = new URL(urlString);
			final URLConnection conn = url.openConnection();
			conn.connect();
			final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
			final Bitmap bm = BitmapFactory.decodeStream(bis);
			bis.close();
			return bm;
		}
		catch(IOException ex)
		{
			System.err.println("Globals/getRemoteImage/" + ex);
		}
		
		return null;
	}
}