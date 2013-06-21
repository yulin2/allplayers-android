package com.allplayers.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.allplayers.android.activities.AllplayersSherlockMapActivity;
import com.allplayers.objects.EventData;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Display the map for an activity.
 */
public class EventDisplayActivity extends AllplayersSherlockMapActivity {
    private EventData mEvent;
    private String mLatitude;
    private String mLongitude;

    /**
     * Called when the activity is starting.
     * 
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in
     * onSaveInstanceState(Bundle).Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventdetail);

        // Pull the event from the current intent.
        mEvent = (new Router(this)).getIntentEvent();

        // Set up the ActionBar.
        mActionBar.setTitle(mEvent.getTitle());

        // Set up the Side Navigation Menu.
        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Pull the event latitude and longitude.
        mLatitude = mEvent.getLatitude();
        mLongitude = mEvent.getLongitude();

        // Create the map.
        createGoogleMap();
    }

    private void createGoogleMap() {
        
        // Create the map fragment.
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        
        // Create a location object from our lat and long.
        LatLng location = new LatLng((Float.parseFloat(mLatitude)), (Float.parseFloat(mLongitude)));
        
        // Focus in on our event location.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
        
        // Create a marker on our event location.
        MarkerOptions marker = new MarkerOptions()
        .position(location)
        .title(mEvent.getTitle())
        .snippet("Start: " + mEvent.getStartDateString() + "\nEnd: " + mEvent.getEndDateString());

        // Add our marker to the map.
        map.setInfoWindowAdapter(new CustomInfoAdapter(getLayoutInflater()));
        map.addMarker(marker).showInfoWindow();

        map.setOnMarkerClickListener(new OnMarkerClickListener() {
            
            /**
             * Called when a marker has been clicked or tapped.
             * 
             * @param marker The marker that was clicked.
             * @return True if the listener has consumed the event (i.e., the default behavior
             * should not occur), false otherwise (i.e., the default behavior should occur). The
             * default behavior is for the camera to move to the map and an info window to appear.
             */
            @Override
            public boolean onMarkerClick(Marker marker) {
                
                // Go to the more detailed event page.
                Intent intent = (new Router(EventDisplayActivity.this)).getEventDetailActivityIntent(mEvent);
                startActivity(intent);
                return false;
            }
        });
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            
            /**
             * Callback for click/tap events on a marker's info window.
             * 
             * @param marker The marker of the info window that was clicked.
             */
            @Override
            public void onInfoWindowClick(Marker marker) {
                
                // Go to the more detailed event page.
                Intent intent = (new Router(EventDisplayActivity.this)).getEventDetailActivityIntent(mEvent);
                startActivity(intent);
            }
        });
    }

    class CustomInfoAdapter implements InfoWindowAdapter {
        LayoutInflater inflater;

        CustomInfoAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        /**
         * Provides custom contents for the default info window frame of a marker.
         * 
         * @param marker The marker for which an info window is being populated.
         * @return A custom view to display as contents in the info window for marker, or null to
         * use the default content rendering instead.
         */
        @Override
        public View getInfoContents(Marker marker) {
            View infoWindow = inflater.inflate(R.layout.event_marker_info_window, null);

            ((TextView)infoWindow.findViewById(R.id.title)).setText(marker.getTitle());
            ((TextView)infoWindow.findViewById(R.id.snippet)).setText(marker.getSnippet());
            return infoWindow;
        }

        /**
         * Provides a custom info window for a marker.
         * @param marker The marker for which an info window is being populated.
         * @return A custom info window for marker, or null to use the default info window frame
         * with custom contents.
         */
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }
}