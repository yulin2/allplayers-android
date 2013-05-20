package com.allplayers.android;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.allplayers.android.activities.AllplayersSherlockActivity;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

public class FindGroupsActivity extends AllplayersSherlockActivity {
    private EditText mSearchEditText;
    private EditText mZipcodeEditText;
    private EditText mDistanceEditText;
    private TextView mDistanceLabel;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.findgroups);

        // Set up the ActionBar.
        mActionBar.setTitle("Search");

        // Set up the Side Navigation Menu.
        mSideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Get a handle on our text inputs and the label for adding a distance to the search.
        mSearchEditText = (EditText)findViewById(R.id.searchGroupsField);
        mZipcodeEditText = (EditText)findViewById(R.id.searchGroupsZipcodeField);
        mDistanceEditText = (EditText)findViewById(R.id.searchGroupsDistanceField);
        mDistanceLabel = (TextView)findViewById(R.id.distanceLabel);

        // Add a watcher for the zipcode input to allow distance search if a valid zip is given.
        mZipcodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // We only want to show the distance field if the zipcode has a valid length of 5.
                if (s.length() == 5) {
                    mDistanceEditText.setVisibility(View.VISIBLE);
                    mDistanceLabel.setVisibility(View.VISIBLE);
                    // If it changes back below 5 or above 5, then we make it disappear.
                } else {
                    mDistanceEditText.setVisibility(View.GONE);
                    mDistanceLabel.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        final Button searchButton = (Button) findViewById(R.id.searchGroupsButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get the text input.
                String query = mSearchEditText.getText().toString().trim();
                String zipcodeString = mZipcodeEditText.getText().toString().trim();
                String distanceString = mDistanceEditText.getText().toString().trim();

                // Set a default zipcode and distance.
                int zipcode = 0;
                int distance = 10;

                // Validate the zipcode input.
                if (zipcodeString.length() == 5) {
                    for (int i = 0; i < 5; i++) {
                        if (!Character.isDigit(zipcodeString.charAt(i))) {
                            break;
                        } else if (i == 4) {
                            zipcode = Integer.parseInt(zipcodeString);
                        }
                    }
                }

                // Validate the distance input.
                if (distanceString.length() >= 1) {
                    for (int i = 0; i < distanceString.length(); i++) {
                        if (!Character.isDigit(distanceString.charAt(i))) {
                            break;
                        } else if (i == distanceString.length() - 1) {
                            distance = Integer.parseInt(distanceString);
                        }
                    }
                }

                // Start the activity to display the search results.
                Intent intent = (new Router(FindGroupsActivity.this)).getSearchGroupsListActivityIntent(query, zipcode, distance);
                startActivity(intent);
            }
        });
    }
}