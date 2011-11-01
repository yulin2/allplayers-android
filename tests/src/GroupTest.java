import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.*;

public class GroupTest
{
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
		
		//Return all groups
		try
		{
			URL url = new URL("https://www.allplayers.com/?q=api/v1/rest/groups.json");
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
			
			System.out.println("\n\nReturn all groups");
			System.out.println(returned);
		}
		catch (Exception ex)
		{
			System.out.println("2: " + ex);
		}
		
		//Return group by uuid
		try
		{
			URL url = new URL("https://www.allplayers.com/?q=api/v1/rest/groups/5987574c-f611-11e0-a44b-12313d04fc0f.json");
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
			
			System.out.println("\n\nReturn group by uuid");
			System.out.println(returned);
		}
		catch (Exception ex)
		{
			System.out.println(ex);
		}
	}
}
