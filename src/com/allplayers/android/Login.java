package com.allplayers.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends Activity
{
	/** called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		final Button findGroupsButton = (Button)findViewById(R.id.findGroupsButton);
        findGroupsButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	startActivity(new Intent(Login.this, FindGroupsActivity.class));
            }
        });
		
		final Button button = (Button)findViewById(R.id.loginButton);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	EditText usernameEditText = (EditText)findViewById(R.id.usernameField);     
            	EditText passwordEditText = (EditText)findViewById(R.id.passwordField);
            	
            	String username = usernameEditText.getText().toString();
            	String password = passwordEditText.getText().toString();;
            	
                String result = APCI_RestServices.validateLogin(username, password);
                
				try
				{
					JSONObject jsonResult = new JSONObject(result);
					APCI_RestServices.user_id = jsonResult.getJSONObject("user").getString("uuid");
					
					Intent intent = new Intent(Login.this, MainScreen.class);
					startActivity(intent);
				}
				catch(JSONException ex)
				{
					System.out.println(ex);
					
					Toast invalidLogin = Toast.makeText(getApplicationContext(), "Invalid Login", Toast.LENGTH_LONG);
					invalidLogin.show();
				}
            }
        });
	}
}