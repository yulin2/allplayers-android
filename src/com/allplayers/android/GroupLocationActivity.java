package com.allplayers.android;

import java.io.IOException;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.allplayers.android.activities.AllplayersSherlockMapActivity;
import com.allplayers.objects.GroupData;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * TODO If maps are missing on device image, this activity will crash.
 */
public class GroupLocationActivity extends AllplayersSherlockMapActivity {

    private GoogleMap mMap;
    /**
     * Called when the activity is first created, this sets up variables,
     * creates the Action Bar, and sets up the Side Navigation Menu.
     * @param savedInstanceState: Saved data from the last instance of the
     * activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_location);

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        GroupData group = (new Router(this)).getIntentGroup();

        String lat = group.getLat();
        String lon = group.getLon();

        if (lat.equals("") || lon.equals("") || lat.equals("0.000000") || lon.equals("0.000000")) {
            Geocoder geo = new Geocoder(this);
            try {
                List<Address> addr = geo.getFromLocationName(group.getZip(), 1);
                lat = addr.get(0).getLatitude() + "";
                lon = addr.get(0).getLongitude() + "";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LatLng location = new LatLng((Float.parseFloat(lat)), (Float.parseFloat(lon)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 7));
        mMap.addMarker(new MarkerOptions()
                       .position(location)
                       .title(group.getTitle())
                       .snippet(group.getZip())
                      ).showInfoWindow();

        mActionBar.setTitle(group.getTitle());

        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);
    }
}