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
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

/**
 * Interface for the user to search for groups.
 */
public class FindGroupsActivity extends AllplayersSherlockActivity {
    private Button mSearchButton;
    private EditText mSearchEditText;
    private EditText mZipcodeEditText;
    private EditText mDistanceEditText;
    private TextView mDistanceLabel;

    /**
     * Called when the activity is starting.
     * 
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in
     * onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findgroups);

        // Check if the user is logged in or not. If not, get rid of the side nav menu button. We
        // dont want some random dude perusing through the app without an account. 
        if (RestApiV1.getCurrentUserUUID().equals("")) {
            mActionBar.setHomeButtonEnabled(false);
        }

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
            
            /**
             * This method is called to notify you that, within s, the count characters beginning
             * at start have just replaced old text that had length before.
             * 
             * @param s The sequence that is being checked for changes.
             * @param start Where the text replacement started.
             * @param before Where the text replacement ended.
             * @param count The number of characters whos text was replaced.
             */
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
            
            /**
             * This method is called to notify you that, somewhere within s, the text has been
             * changed. 
             * 
             * @param s The Editable that has been edited.
             */
            @Override
            public void afterTextChanged(Editable s) {
                // UNUSED
            }
            
            /**
             * This method is called to notify you that, within s, the count characters beginning at
             * start are about to be replaced by new text with length after.
             * 
             * @param s The sequence that is being checked for changes.
             * @param start Where the text replacement started.
             * @param before Where the text replacement ended.
             * @param count The number of characters whos text was replaced.
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // UNUSED
            }
        });

        mSearchButton = (Button) findViewById(R.id.searchGroupsButton);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            
            /**
             * Called when a view has been clicked.
             * 
             * @param v The view that was clicked.
             */
            @Override
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