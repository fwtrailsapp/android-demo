package com.example.jaron.fwtrailsapp_android_demo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        locationManager = (LocationManager) this.getSystemService((Context.LOCATION_SERVICE));
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //Gets called when a new location is found by the network location provider.
                updateLocation(location);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        // Add a marker in Sydney and move the camera41.117405, -85.108335
        LatLng skyBridge = new LatLng(41.117405, -85.108335);
        mMap.addMarker(new MarkerOptions().position(skyBridge).title("IPFW Skybridge"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(skyBridge));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

    }

    private void updateLocation(Location location)
    {
        // Figure out how to move the screen to keep up with the dot in the center.
        LatLng updatedLocation = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(updatedLocation));
    }
}
