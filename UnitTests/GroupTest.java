import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;

import java.io.ByteArrayInputStream;


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
			URL url = new URL("https://www.allplayers.com/?q=api/v1/rest/groups.xml");
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
			URL url = new URL("https://www.allplayers.com/?q=api/v1/rest/groups/5987574c-f611-11e0-a44b-12313d04fc0f.xml");
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
		
/*<result is_array="true">

<item>
<uuid>5987574c-f611-11e0-a44b-12313d04fc0f</uuid>
<title>2011-2012 RCJRS Tryouts</title>
<description>Rutherford County JRS</description>

<location>
<street></street>
<city>Murfreesboro</city>
<state>TN</state>
<zip>37128</zip>
<country>us</country>
<latitude>35.845621</latitude>
<longitude>-86.390270</longitude>
</location>

<activity_level>453.169025861532</activity_level>
<list_in_directory>0</list_in_directory>
<active>Active</active>
<registration_fees_enabled>No Fee</registration_fees_enabled>
<payee_uuid>595eb436-f611-11e0-a44b-12313d04fc0f</payee_uuid>
<group_mates_enabled>Group Mates</group_mates_enabled>
<primary_color>FF0000</primary_color>
<secondary_color>D3D3D3</secondary_color>
<node_status>1</node_status>
<logo>https://d34kk2hki1q2pt.cloudfront.net/sites/default/files/imagecache/profile_small/group_conten

t_logo/rcjrs_image_0.jpg</logo>
<uri>https://www.allplayers.com/api/v1/rest/groups/5987574c-f611-11e0-a44b-12313d04fc0f</uri>
<url>https://www.allplayers.com/g/2011_2012_rcjrs_tryouts</url>

<groups_above_uuid is_array="true">
<item>595eb436-f611-11e0-a44b-12313d04fc0f</item>
</groups_above_uuid>
</item>

["uuid","title","description","location","activity_level",
"list_in_directory","active","registration_fees_enabled",
"approved_for_payment","accept_amex","payee_uuid","approved_for_idverify",
"group_mates_enabled","comments","parent_approval_enabled","sort_order",
"override_default_group_permissions","primary_color","secondary_color",
"node_status","logo","uri","url","groups_above_uuid"]*/

/*			try
			{
				//InputSource is = new InputSource(getResources().openRawResource(R.raw.xmlDisplay));
				InputStream readStream = new ByteArrayInputStream(returned.getBytes());
				InputSource is = new InputSource(readStream);
				//create the factory
				SAXParserFactory factory = SAXParserFactory.newInstance();
				//create a parser
				SAXParser parser = factory.newSAXParser();
				//create the reader(scanner)
				XMLReader xmlreader = parser.getXMLReader();
				//instantiate our handler
				XMLHandler handler = new XMLHandler();

				//assign our handler
				xmlreader.setContentHandler(handler);
				//perform the synchronous parse
				xmlreader.parse(is);
				
				System.out.println(handler.getResults());
			}
			catch(Exception ex)
			{
				System.out.println("1: " + ex);
			}*/
	}
}
