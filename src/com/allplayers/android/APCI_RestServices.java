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

import org.json.JSONObject;

public class APCI_RestServices
{
	public static String user_id = "";
	public static String session_cookie = ""; //first session cookie
	public static String chocolatechip_cookie = ""; //second cookie
	
	public APCI_RestServices()
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
	}
	
	public static String isLoggedIn()
	{
		if(user_id.equals(""))
		{
			//return false;
			return "empty";
		}
		
		//Check an authorized call
		String result = "";
		HttpURLConnection connection;
		int responsecode = 0;
		try
		{
			URL url = new URL("https://www.allplayers.com/?q=api/v1/rest/users/" + user_id + ".json");
			connection = (HttpURLConnection)url.openConnection();
			connection.setDoInput(true);
			connection.setRequestProperty("Cookie", chocolatechip_cookie);
			connection.addRequestProperty("Cookie", session_cookie);
			responsecode = connection.getResponseCode();
			InputStream inStream = connection.getInputStream();
			BufferedReader input = new BufferedReader(new InputStreamReader(inStream));
			
			String line = "";
			
			if((line = input.readLine()) == null)
			{
				result += "null";
			}
			while((line = input.readLine()) != null)
			{
				result += line;
			}
			
			JSONObject jsonResult = new JSONObject(result);
			String retrievedUUID = jsonResult.getString("uuid");
			
			if(retrievedUUID.equals(user_id))
			{
				//return true;
				return user_id;
			}
			else //this case should not occur
			{
				//return false;
				return retrievedUUID;
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
			//return false;
			return "ERRORRORROROR: \nresult=" + result + "\nresponsecode=" + responsecode + "\nerror=" + ex.toString();
		}
	}
	
	public static String validateLogin(String username, String password)
	{
		//Log in
		try
		{
			HttpURLConnection connection;
			DataOutputStream printout;
			BufferedReader input;
			
			URL url = new URL("https://www.allplayers.com/?q=api/v1/rest/users/login.json");
			connection = (HttpURLConnection)url.openConnection();
			
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			printout = new DataOutputStream(connection.getOutputStream());
			
			//Send POST output.
			String content = "username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");
			printout.writeBytes(content);
			printout.flush();
			printout.close();
			
			//Get response data.
			input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String str;
			
			String result = "";
			while((str = input.readLine()) != null)
			{
				result += str;
			}
			
			input.close();
			
			// Get all cookies from the server.
		    // Note: The first call to getHeaderFieldKey() will implicit send
		    // the HTTP request to the server.
		    for (int i=0; ; i++) {
		        String headerName = connection.getHeaderFieldKey(i);
		        String headerValue = connection.getHeaderField(i);

		        if (headerName == null && headerValue == null) {
		            // No more headers
		            break;
		        }
		        if ("Set-Cookie".equalsIgnoreCase(headerName)) {
		            // Parse cookie
		            String[] fields = headerValue.split(";\\s*");

		            String cookieValue = fields[0];
		            String expires = null;
		            String path = null;
		            String domain = null;
		            boolean secure = false;

		            // Parse each field
		            for (int j=1; j<fields.length; j++) {
		                if ("secure".equalsIgnoreCase(fields[j])) {
		                    secure = true;
		                } else if (fields[j].indexOf('=') > 0) {
		                    String[] f = fields[j].split("=");
		                    if ("expires".equalsIgnoreCase(f[0])) {
		                        expires = f[1];
		                    } else if ("domain".equalsIgnoreCase(f[0])) {
		                        domain = f[1];
		                    } else if ("path".equalsIgnoreCase(f[0])) {
		                        path = f[1];
		                    }
		                }
		            }
		            
		            if(cookieValue.startsWith("SESS"))
		            {
		            	session_cookie = cookieValue;
		            }
		            else if(cookieValue.startsWith("CHOCOLATECHIP"))
		            {
		            	chocolatechip_cookie = cookieValue;
		            }
		        }
		    }
			
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
			URL url = new URL("https://www.allplayers.com/?q=api/v1/rest/groups.json&search=\"" + search + "\"");
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			InputStream inStream = connection.getInputStream();
			BufferedReader input = new BufferedReader(new InputStreamReader(inStream));
			
			String line = "";
			String result = "";
			while((line = input.readLine()) != null)
			{
				result += line;
			}
			
			return result;
		}
		catch(Exception ex)
		{
			System.out.println(ex);
			return ex.toString();
		}
	}
	
	public static String getUserGroups()
	{
		/*if(!isLoggedIn())
		{
			return "not logged in\n\n" + user_id;
		}*/
		
		String test = isLoggedIn();
		
		//Return all groups that meet search requirement
		try
		{
			URL url = new URL("https://www.allplayers.com/?q=api/v1/rest/user/" + user_id + "/groups.json");
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setDoInput(true);
			//connection.setRequestProperty("Cookie", session_name + "=" + session_id);
			InputStream inStream = connection.getInputStream();
			BufferedReader input = new BufferedReader(new InputStreamReader(inStream));
			
			String line = "";
			String result = "";
			while((line = input.readLine()) != null)
			{
				result += line;
			}
			
			return test + "\n\n" + result;
		}
		catch(Exception ex)
		{
			System.out.println(ex);
			return test + "\n\nhmmmmm\n" + ex.toString();
		}
	}
}