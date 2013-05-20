
package com.allplayers.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.allplayers.android.activities.AllplayersSherlockFragmentActivity;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

public class GroupsActivity extends AllplayersSherlockFragmentActivity {

    private SpinnerAdapter mSpinnerAdapter;
    private String mSortType = "radioactive";

    /**
     * Called when the activity is first created, this creates the Action Bar
     * and sets up the Side Navigation Menu.
     *
     * @param savedInstanceState: Saved data from the last instance of the
     *            activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.groups);

        // Create the fragment for our groups list and give it the default sort type.
        Fragment groupFragment = new GroupsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("sort", "radioactive");
        groupFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.container, groupFragment).addToBackStack(null).commit();

        // Set up the ActionBar.
        mActionBar.setTitle("Groups");

        // Create a custom dropdown menu for picking our sort type and populate it with our sort options.
        ViewGroup sorter = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.sort_method_layout, null);
        mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sort_method,
                          android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) sorter.findViewById(R.id.sort_chooser);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            // Called when the user clicks an option in our sort menu.
            @Override
            public void onItemSelected(AdapterView<?> collection, View view,
            int position, long id) {
                // Choose the new sort type based on the option picked.
                String newSort = "";
                switch (position) {
                case 0:
                    newSort = "radioactive";
                    break;
                case 1:
                    newSort = "alphabetical_ascending";
                    break;
                case 2:
                    newSort = "alphabetical_descending";
                    break;
                }
                // If the sort has changed, replace the groups list fragment with one with the new sort.
                if (!mSortType.equals(newSort)) {
                    mSortType = newSort;
                    Fragment groupFragment = new GroupsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("sort", newSort);
                    groupFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, groupFragment).addToBackStack(null).commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // Add our custom dropdown menu to our ActionBar.
        mActionBar.setCustomView(sorter, new LayoutParams(Gravity.RIGHT));
        mActionBar.setDisplayShowCustomEnabled(true);

        // Set up the Side Navigation Menu.
        mSideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);
    }

    @Override
    public void onBackPressed() {
        // This is used to fix a problem with the activity stack on API 10.
        moveTaskToBack(true);
    }
}
