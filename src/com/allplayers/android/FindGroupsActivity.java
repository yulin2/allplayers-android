package com.allplayers.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FindGroupsActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findgroups);

        final Button logOnButton = (Button) findViewById(R.id.searchGroupsButton);
        logOnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText searchEditText = (EditText)findViewById(R.id.searchGroupsField);
                String query = searchEditText.getText().toString().trim();
                EditText zipcodeEditText = (EditText)findViewById(R.id.searchGroupsZipcodeField);
                String zipcodeString = zipcodeEditText.getText().toString().trim();
                EditText distanceEditText = (EditText)findViewById(R.id.searchGroupsDistanceField);
                String distanceString = distanceEditText.getText().toString().trim();

                int zipcode = 0;
                int distance = 10;

                if (zipcodeString.length() == 5) {
                    for (int i = 0; i < 5; i++) {
                        if (!Character.isDigit(zipcodeString.charAt(i))) {
                            break;
                        } else if (i == 4) {
                            zipcode = Integer.parseInt(zipcodeString);
                        }
                    }
                }

                if (distanceString.length() >= 1) {
                    for (int i = 0; i < distanceString.length(); i++) {
                        if (!Character.isDigit(distanceString.charAt(i))) {
                            break;
                        } else if (i == distanceString.length() - 1) {
                            distance = Integer.parseInt(distanceString);
                        }
                    }
                }


                Intent intent = (new Router(FindGroupsActivity.this)).getSearchGroupsListActivityIntent(query, zipcode, distance);
                startActivity(intent);
            }
        });
    }
}