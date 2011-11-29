package com.allplayers.android;

import android.content.Context;

import org.jasypt.util.text.BasicTextEncryptor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

//Local Storage for JSON Strings
//
//Example Usage:
//LocalStorage.writeInbox(this.getBaseContext(), jsonResult);
//LocalStorage.readInbox(this.getBaseContext()); <---Returns a String
//
//
//writeInbox(Context, String)
//readInbox(Context)
//
//writeUserGroups(Context, String)
//readUserGroups(Context)
//
//writeUserGroupMembers(Context, String)
//readUserGroupMembers(Context)
//
//Will add more as we implement this functionality into main code
//

public class LocalStorage
{
	protected static Context mContext;
	private static String writeString;

	public static void writeUserName(Context c, String write)
	{
		writeFile(c, write, "UserName");
	}
	public static String readUserName(Context c)
	{
		String returnValue = readFile(c, "UserName");
		return returnValue;
	}
	
	public static void writePassword(Context c, String write)
	{
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword("C47F3F767ED97B9CDC73A3A35C0DFCF75FC4F9AF323EC40C8F9986FF9F40A61C");
		String encryptedPassword = textEncryptor.encrypt(write);
		
		writeFile(c, encryptedPassword, "Password");
	}
	public static String readPassword(Context c)
	{
		String returnValue = readFile(c, "Password");
		return returnValue;
	}
	
	public static void writeInbox(Context c, String write)
	{
		writeFile(c, write, "Inbox");
	}
	public static String readInbox(Context c)
	{
		String returnValue = readFile(c, "Inbox");
		return returnValue;
	}

	public static void writeUserGroups(Context c, String write)
	{
		writeFile(c, write, "UserGroups");
	}
	public static String readUserGroups(Context c)
	{
		String returnValue = readFile(c, "UserGroups");
		return returnValue;
	}

	public static void writeUserGroupMembers(Context c, String write)
	{
		writeFile(c, write, "UserGroupMembers");
	}
	public static String readUserGroupMembers(Context c)
	{
		String returnValue = readFile(c, "UserGroupMembers");
		return returnValue;
	}

	public static void writeFile(Context c, String write, String fileName)
	{
		mContext = c;
		writeString = write;

		try 
		{
			writeString = write;

			FileOutputStream fOut = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(fOut); 

			osw.write(writeString);

			osw.flush();
			osw.close();
		} 
		catch(IOException ioe) 
		{
			ioe.printStackTrace();
		}	  
	}

	public static String readFile(Context c, String fileName)
	{
		mContext = c;
		String data = "";
		FileInputStream fis;

		try
		{
			fis = mContext.openFileInput(fileName);

			InputStreamReader isr = new InputStreamReader(fis);

			BufferedReader br = new BufferedReader(isr);

			data = br.readLine();

			isr.close();
			br.close();

			return data;
		}
		catch(FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
			return "";
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}

		return data;		  
	}
}