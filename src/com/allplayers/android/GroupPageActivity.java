
package com.allplayers.android;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.allplayers.android.activities.AllplayersSherlockActivity;
import com.allplayers.objects.GroupData;
import com.allplayers.objects.GroupMemberData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.google.gson.Gson;

public class GroupPageActivity extends AllplayersSherlockActivity {

    private GroupData mGroup;
    private ArrayList<GroupMemberData> mMembersList;
    private boolean isAdmin = false, isMember = false, isLoggedIn = false;
    private ProgressBar mProgressBar;
    private AllplayersSherlockActivity mParentActivity = this;
    Menu mAdminMenu;

    /**
     * Called when the activity is first created, this creates the Action Bar
     * and sets up the Side Navigation Menu as well as assigning some variables.
     *
     * @param savedInstanceState: Saved data from the last instance of the
     *            activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grouppage);

        // Get the group information from the current intent.
        mGroup = (new Router(this)).getIntentGroup();

        mProgressBar = (ProgressBar) findViewById(R.id.progress_indicator);

        // Hide the default title: needed to style our title.
        mActionBar.setDisplayShowTitleEnabled(false);

        // Create, style, and add our custom title to the ActionBar.
        TextView title = new TextView(this);
        title.setText(mGroup.getTitle());
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.WHITE);
        title.setLines(1);
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setPadding(0, 15, 0, 0);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(Gravity.CENTER);
        mActionBar.setCustomView(title, params);
        mActionBar.setDisplayShowCustomEnabled(true);

        // Add the "Admin Toolbox" menu to the actionbar.


        // Set up the Side Navigation Menu.
        mSideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Load the group members so we check if the user belongs and can set up the UI.
        if (!RestApiV1.getCurrentUserUUID().equals("")) {
            new GetGroupMembersByGroupIdTask().execute(mGroup.getUUID());
            new GetGroupLocationTask().execute(mGroup.getUUID());
        } else {
            new GetRemoteImageTask().execute(mGroup.getLogo());
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     *
     * @param menu: The options menu in which you place your items.
     * @return You must return true for the menu to be displayed; if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mAdminMenu = menu;
        new CheckUserAdminPrivalegesAndCreateOptionsMenuTask().execute();
        return true;
    }



    /**
     * Listener for the Action Bar Options Menu.
     *
     * @param item: The selected menu item.
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

        case android.R.id.home: {
            mSideNavigationView.toggleMenu();
            return true;
        }

        case R.id.menu_broadcast: {
            Gson gson = new Gson();
            String broadcastRecipients = gson.toJson(mMembersList);
            Intent intent = new Intent(getBaseContext(), SelectMessageContacts.class);
            intent.putExtra("broadcastRecipients", broadcastRecipients);
            startActivity(intent);
        }

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Gets the group's location because it has not been previously pulled.
     *
     */
    public class GetGroupLocationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... group_uuid) {
            return RestApiV1.getGroupInformationByGroupId(group_uuid[0]);
        }

        @Override
        protected void onPostExecute(String jsonResult) {
            try {
                JSONObject groupInfo = new JSONObject(jsonResult);
                mGroup.setZip(groupInfo.getJSONObject("location").getString("zip"));
                mGroup.setLatLon(groupInfo.getJSONObject("location").getString("latitude"), groupInfo.getJSONObject("location").getString("longitude"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Gets a remote image using a REST call.
     */
    public class GetRemoteImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... logoURL) {
            return RestApiV1.getRemoteImage(logoURL[0]);
        }

        @Override
        protected void onPostExecute(Bitmap logo) {
            // Hide the loading indicator.
            mProgressBar.setVisibility(View.GONE);

            // Set the image for the group's logo.
            ImageView imView = (ImageView) findViewById(R.id.groupLogo);
            if (logo == null) {
                imView.setImageResource(R.drawable.group_default_logo);
            } else {
                imView.setImageBitmap(logo);
            }

            // If this person is a member of the current group and is logged in, show the button
            // that launches the Group Member viewing page.
            final ImageButton groupMembersButton = (ImageButton) findViewById(R.id.groupMembersButton);
            if (isMember && isLoggedIn) groupMembersButton.setVisibility(View.VISIBLE);
            groupMembersButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = (new Router(GroupPageActivity.this))
                                    .getGroupMembersActivityIntent(mGroup);
                    startActivity(intent);
                }
            });

            // If this person is a member of the current group and is logged in, show the button
            // that launches the Group Event viewing page.
            final ImageButton groupEventsButton = (ImageButton) findViewById(R.id.groupEventsButton);
            if (isMember && isLoggedIn) groupEventsButton.setVisibility(View.VISIBLE);
            groupEventsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = (new Router(GroupPageActivity.this))
                                    .getGroupEventsActivityIntent(mGroup);
                    startActivity(intent);
                }
            });

            // If this person is a member of the current group and is logged in, show the button
            // that launches the Group Photos viewing page.
            final ImageButton groupPhotosButton = (ImageButton) findViewById(R.id.groupPhotosButton);
            if (isMember && isLoggedIn) groupPhotosButton.setVisibility(View.VISIBLE);
            groupPhotosButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = (new Router(GroupPageActivity.this))
                                    .getGroupAlbumsActivityIntent(mGroup);
                    startActivity(intent);
                }
            });

            // If this person is a member of the current group and is logged in, show the button
            // that launches the Group Location viewing page.
            final ImageButton groupLocationButton = (ImageButton) findViewById(R.id.groupLocationButton);
            if (isMember && isLoggedIn) groupLocationButton.setVisibility(View.VISIBLE);
            groupLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(Build.VERSION.SDK_INT < 11)) {
                        Intent intent = (new Router(GroupPageActivity.this))
                                        .getGroupLocationActivityIntent(mGroup);
                        startActivity(intent);
                    } else {
                        Toast.makeText(mParentActivity, "This feature is not supported on your device.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    /**
     * Checks if the user is a group member. If the user is a group member the
     * group page interface is set up.
     */
    public class GetGroupMembersByGroupIdTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... group_uuid) {
            return RestApiV1.getGroupMembersByGroupId(group_uuid[0], 0, 0);
        }

        @Override
        protected void onPostExecute(String jsonResult) {
            isLoggedIn = RestApiV1.isLoggedIn();
            GroupMembersMap groupMembers = new GroupMembersMap(jsonResult);
            mMembersList = groupMembers.getGroupMemberData();
            String currentUUID = RestApiV1.getCurrentUserUUID();
            // Compare the current UUID to all the group members' to check if the logged
            // in user is a member of the group.
            for (int i = 0; i < mMembersList.size(); i++) {
                if (mMembersList.get(i).getUUID().equals(currentUUID)) {
                    isMember = true;
                    break;
                }
            }
            // Load the logo and then set up the UI.
            new GetRemoteImageTask().execute(mGroup.getLogo());
        }
    }

    public class CheckUserAdminPrivalegesAndCreateOptionsMenuTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return RestApiV1.getUserRolesInGroup(mGroup.getUUID(), RestApiV1.getCurrentUserUUID());
        }

        @Override
        protected void onPostExecute(String jsonResult) {

            // The API call we are using will return "error" always if the current user is not a
            // group admin. We can check this condition to see if we want to set up the admin menu.
            if (!jsonResult.equals("error")) {
                createOptionsMenu();
            }
        }

    }

    /**
     * Helper for CheckUserAdminPrivalegesAndCreateOptionsMenuTask. Creats the admin options menu.
     */
    private void createOptionsMenu() {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.admin_tools_menu, mAdminMenu);
    }
}