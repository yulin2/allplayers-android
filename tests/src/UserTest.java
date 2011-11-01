import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import javax.net.ssl.*;

import java.io.DataOutputStream;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;

public class UserTest
{
	public static final String USERNAME = ""; //Allplayers.com username
	public static final String PASSWORD = ""; //And password
	
	public static void main(String[] args)
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
			URL url = new URL("https://www.allplayers.com/?q=api/v1/rest/users/login.json");
			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
			DataOutputStream printout;
			BufferedReader input;
			
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			//Send POST output.
			printout = new DataOutputStream(urlConn.getOutputStream());
			String content = "username=" + URLEncoder.encode(USERNAME, "UTF-8") + "&password=" + URLEncoder.encode(PASSWORD, "UTF-8");
			printout.writeBytes(content);
			printout.flush();
			printout.close();
			
			//Get response data.
			input = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String str;
			
			while((str = input.readLine()) != null)
			{
				System.out.println(str);
			}
			
			input.close();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
}
