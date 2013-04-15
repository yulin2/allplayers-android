package com.allplayers.android;

import java.io.IOException;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.allplayers.android.activities.AllplayersSherlockMapActivity;
import com.allplayers.objects.GroupData;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * TODO If maps are missing on device image, this activity will crash.
 */
public class GroupLocationActivity extends AllplayersSherlockMapActivity {

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

        MapView map = (MapView)findViewById(R.id.groupLocation);
        map.setBuiltInZoomControls(true);

        GroupData group = (new Router(this)).getIntentGroup();

        MapController mapController = map.getController();
        GeoPoint point;
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
        point = new GeoPoint((int)(Float.parseFloat(lat) * 1000000), (int)(Float.parseFloat(lon) * 1000000));
        mapController.setCenter(point);
        mapController.setZoom(15);

        List<Overlay> mapOverlays = map.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.pindrop_50x50);
        mItemizedOverlay itemizedoverlay = new mItemizedOverlay(drawable, this);
        OverlayItem center = new OverlayItem(point, group.getTitle(), group.getZip());
        itemizedoverlay.addOverlay(center);
        mapOverlays.add(itemizedoverlay);

        actionbar = getSupportActionBar();
        actionbar.setIcon(R.drawable.menu_icon);
        actionbar.setTitle(group.getTitle());

        sideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);
    }
}