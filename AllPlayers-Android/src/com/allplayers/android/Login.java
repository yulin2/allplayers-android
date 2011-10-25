/* Copyright information */

package com.allplayers.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/* Class information */
public class Login extends Activity
{
	private EditText username;
	private EditText password;

	/** called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//create the panel to enclose everything
		View mainPanel = createInputForm();
		
		//show the panel on the screen
		setContentView(mainPanel);
	}
	
	/** create the login form */
	private ViewGroup createInputForm()
	{
		LinearLayout panel = new LinearLayout(this);
		panel.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		panel.setOrientation(LinearLayout.VERTICAL);

		//username label and text field
		TextView usernameLbl = new TextView(this);
		usernameLbl.setText("Username:");
		usernameLbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
		usernameLbl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		username = new EditText(this);
		username.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		//password label and text field
		TextView passwordLbl = new TextView(this);
		passwordLbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
		passwordLbl.setText("Password:");
		passwordLbl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		password = new EditText(this);
		password.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		password.setTransformationMethod(new PasswordTransformationMethod());

		//login button
		final Button loginButton = new Button(this);
		loginButton.setText("Login");
		loginButton.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		loginButton.setGravity(Gravity.CENTER);
		loginButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				TextView tv = new TextView(Login.this);
				
				String name = "";
				String result = checkLogin(username.getText().toString(), password.getText().toString());
				JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON(result);
				
				tv.setText("Hello, " + name);
				setContentView(tv);
			}
		});

		//adding the views to the panel
		//username
		panel.addView(usernameLbl);
		panel.addView(username);
		//password
		panel.addView(passwordLbl);
		panel.addView(password);
		//login button
		panel.addView(loginButton);

		return panel;
	}
	
	public String checkLogin(String username, String password)
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
			return "";
		}
	}
}