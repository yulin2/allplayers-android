/* Copyright information */

package allplayers.android;

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
				tv.setText("Hello, Android");
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
}