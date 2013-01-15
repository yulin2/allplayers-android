package com.allplayers.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FindGroupsActivity extends Activity {
    EditText searchEditText;
    EditText zipcodeEditText;
    EditText distanceEditText;
    TextView distanceLabel;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findgroups);
        searchEditText = (EditText)findViewById(R.id.searchGroupsField);
        zipcodeEditText = (EditText)findViewById(R.id.searchGroupsZipcodeField);
        distanceEditText = (EditText)findViewById(R.id.searchGroupsDistanceField);
        distanceLabel = (TextView)findViewById(R.id.distanceLabel);

        zipcodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // We only want to show the distance field if the zipcode has a valid length of 5.
                if (s.length() == 5) {
                    distanceEditText.setVisibility(View.VISIBLE);
                    distanceLabel.setVisibility(View.VISIBLE);
                // If it changes back below 5 or above 5, then we make it disappear.
                } else {
                    distanceEditText.setVisibility(View.GONE);
                    distanceLabel.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        final Button logOnButton = (Button) findViewById(R.id.searchGroupsButton);
        logOnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String query = searchEditText.getText().toString().trim();
                String zipcodeString = zipcodeEditText.getText().toString().trim();
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