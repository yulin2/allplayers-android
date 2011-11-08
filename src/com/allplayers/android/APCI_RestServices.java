package com.allplayers.android;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class APCI_RestServices
{
	public static String validateLogin(String username, String password)
	{
		//Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager()
		{
			public java.security.cert.X509Certificate[] getAcceptedIssuers()
			{
			return null;
			}
			
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
			{
			}
			
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
			{
			}
		}};
		
		//Install the all-trusting trust manager
		try
		{
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		}
		catch (Exception ex)
		{
		}
		//Now you can access an https URL without having the certificate in the truststore
		
		//Log in
		try
		{
			HttpURLConnection urlConn;
			DataOutputStream printout;
			BufferedReader input;
			
			URL url = new URL("https://www.allplayers.com/?q=api/v1/rest/users/login.json");
			urlConn = (HttpURLConnection)url.openConnection();
			
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			printout = new DataOutputStream(urlConn.getOutputStream());
			
			//Send POST output.
			String content = "username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");
			printout.writeBytes(content);
			printout.flush();
			printout.close();
			
			//Get response data.
			input = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String str;
			
			String result = "";
			while((str = input.readLine()) != null)
			{
				result += str;
			}
			
			input.close();
			return result;
		}
		catch(Exception ex)
		{
			System.out.println(ex);
			return ex.toString();
		}
	}
	
	public static String searchGroups(String search)
	{
		//Return all groups that meet search requirement
		try
		{
			URL url = new URL("https://www.allplayers.com/?q=api/v1/rest/groups.json?search=\"" + search + "\"");
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			InputStream inStream = connection.getInputStream();
			BufferedReader input = new BufferedReader(new InputStreamReader(inStream));
			
			String line = "";
			String returned = "";
			while((line = input.readLine()) != null)
			{
				returned += line;
			}
			
			return returned;
		}
		catch(Exception ex)
		{
			System.out.println(ex);
			return ex.toString();
		}
	}
}