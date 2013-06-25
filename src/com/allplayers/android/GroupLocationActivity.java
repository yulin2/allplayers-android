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
 * Display a map with the current group's location.
 */
public class GroupLocationActivity extends AllplayersSherlockMapActivity {
    private GoogleMap mMap;
    
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
        setContentView(R.layout.group_location);

        // Make a new map
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        // Grab the group's data from the intent.
        GroupData group = (new Router(this)).getIntentGroup();

        // Set up the latitude and longitude. If that data isn't already in the GroupData object,
        // we will calculate it ourselves. 
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
        
        // Set up the camera location and place a marker at the group location.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 11));
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