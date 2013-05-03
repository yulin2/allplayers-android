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


        mEvent = (new Router(this)).getIntentEvent();

        actionbar = getSupportActionBar();
        actionbar.setIcon(R.drawable.menu_icon);
        actionbar.setTitle(mEvent.getTitle());

        sideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);

        mLatitude = mEvent.getLatitude();
        mLongitude = mEvent.getLongitude();

        createGoogleMap();
    }

    private void createGoogleMap() {
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        LatLng location = new LatLng((Float.parseFloat(mLatitude)), (Float.parseFloat(mLongitude)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 7));
        MarkerOptions marker = new MarkerOptions()
        .position(location)
        .title(mEvent.getTitle())
        .snippet("Start: " + mEvent.getStartDateString() + "\nEnd: " + mEvent.getEndDateString());

        map.setInfoWindowAdapter(new CustomInfoAdapter(getLayoutInflater()));
        map.addMarker(marker).showInfoWindow();

        map.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = (new Router(EventDisplayActivity.this)).getEventDetailActivityIntent(mEvent);
                startActivity(intent);
                return false;
            }
        });
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = (new Router(EventDisplayActivity.this)).getEventDetailActivityIntent(mEvent);
                startActivity(intent);
            }
        });
    }
}

class CustomInfoAdapter implements InfoWindowAdapter {
    LayoutInflater inflater;

    CustomInfoAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View infoWindow = inflater.inflate(R.layout.event_marker_info_window, null);

        TextView textView = (TextView)infoWindow.findViewById(R.id.title);
        textView.setText(marker.getTitle());
        textView = (TextView)infoWindow.findViewById(R.id.snippet);
        textView.setText(marker.getSnippet());
        return infoWindow;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub
        return null;
    }

}