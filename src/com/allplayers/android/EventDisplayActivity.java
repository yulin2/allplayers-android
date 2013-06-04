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

public class EventDisplayActivity extends AllplayersSherlockMapActivity {
    private EventData mEvent;
    private String mLatitude;
    private String mLongitude;

    /**
     * Called when the activity is first created, this sets up variables,
     * creates the Action Bar, and sets up the Side Navigation Menu.
     * @param savedInstanceState: Saved data from the last instance of the
     * activity.
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
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Go to the more detailed event page.
                Intent intent = (new Router(EventDisplayActivity.this)).getEventDetailActivityIntent(mEvent);
                startActivity(intent);
                return false;
            }
        });
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
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

        @Override
        public View getInfoContents(Marker marker) {
            View infoWindow = inflater.inflate(R.layout.event_marker_info_window, null);

            ((TextView)infoWindow.findViewById(R.id.title)).setText(marker.getTitle());
            ((TextView)infoWindow.findViewById(R.id.snippet)).setText(marker.getSnippet());
            return infoWindow;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

    }
}