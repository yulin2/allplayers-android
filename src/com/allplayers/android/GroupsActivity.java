
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

        Fragment groupFragment = new GroupsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("sort", "radioactive");
        groupFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.container, groupFragment).addToBackStack(null).commit();

        actionbar.setTitle("Groups");

        ViewGroup sorter = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.sort_method_layout, null);
        mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sort_method,
                          android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) sorter.findViewById(R.id.sort_chooser);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> collection, View view,
            int position, long id) {
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
                // TODO Auto-generated method stub
            }
        });
        actionbar.setCustomView(sorter, new LayoutParams(Gravity.RIGHT));
        actionbar.setDisplayShowCustomEnabled(true);

        sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
