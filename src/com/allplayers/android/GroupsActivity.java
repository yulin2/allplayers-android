
package com.allplayers.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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

/**
 * Base activity to hold GroupsFragment.
 */
public class GroupsActivity extends AllplayersSherlockFragmentActivity {
    
    // Variables for sort type spinner.
    private SpinnerAdapter mSpinnerAdapter;
    private String mSortType = "radioactive";

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
                            R.layout.spinner_item);
        Spinner spinner = (Spinner) sorter.findViewById(R.id.sort_chooser);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            /**
             * Callback method to be invoked when an item in this view has been selected. This
             * callback is invoked only when the newly selected position is different from the
             * previously selected position or if there was no selected item.
             * 
             * @param parent The AdapterView where the selection happened.
             * @param view The view within the AdapterView that was clicked.
             * @param position The position of the view in the adapter.
             * @param id The row id of the item that is selected.
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
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

            /**
             * Callback method to be invoked when the selection disappears from this view. The
             * selection can disappear for instance when touch is activated or when the adapter
             * becomes empty.
             * 
             * @param parent The AdapterView that now contains no selected item.
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not being used, but it has to be here.
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

    /**
     * Called when the activity has detected the user's press of the back key. The default
     * implementation simply finishes the current activity, but you can override this to do whatever
     * you want.
     */
    @Override
    public void onBackPressed() {
        
        new AlertDialog.Builder(this)
        .setTitle("Quit?")
        .setMessage("Are you sure you want to quit?")
        .setNegativeButton(android.R.string.no, null)
        .setPositiveButton(android.R.string.yes, new OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                moveTaskToBack(true);
            }
        }).create().show();
    }
}
