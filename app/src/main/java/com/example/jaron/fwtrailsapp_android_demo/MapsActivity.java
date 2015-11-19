package com.example.jaron.fwtrailsapp_android_demo;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.kml.KmlLayer;
import com.google.android.gms.location.LocationListener;

import java.io.InputStream;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String MY_TAG = "LogMessage";
    private GoogleMap mMap;
    private LocationListener locationListener;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    protected LocationRequest mLocationRequest;
    private boolean recording = false;
    private ArrayList<LatLng> coordinates = new ArrayList<>();
    private Polyline line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        buildGoogleApiClient();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.i(MY_TAG, "onCreate");
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
        Log.i(MY_TAG, "buildGoogleApiClient");
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.i(MY_TAG, "createLocationRequest");
    }

    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
        }
        startLocationUpdates();
        Log.i(MY_TAG, "onConnected");
    }

    protected void startLocationUpdates() {
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if(recording) {
                    updateLocation(location);
                }
            }
        };

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
        Log.i(MY_TAG, "startLocationUpdates");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        InputStream is = getResources().openRawResource(R.raw.doc);
        try {
            KmlLayer layer = new KmlLayer(mMap, is, getApplicationContext());
            layer.addLayerToMap();
        }
        catch(org.xmlpull.v1.XmlPullParserException e){
            Log.i(null, "catch1: XML Parser Cannot Parse KML File.\nActual:\t" + e.toString());
        }
        catch(java.io.IOException e){
            Log.i(null, "catch2:\t" + e.toString());
        }

        line = mMap.addPolyline(new PolylineOptions()
                .width(5)
                .color(Color.RED));

        mMap.setMyLocationEnabled(true);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                updateLocation(location);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        // Add a marker in Walb Student Union Skybridge
        LatLng skyBridge = new LatLng(41.117405, -85.108335);
        mMap.addMarker(new MarkerOptions().position(skyBridge).title("IPFW Skybridge"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(skyBridge));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

        Log.i(MY_TAG, "onMapReady");
    }

    private void updateLocation(Location location)
    {
        LatLng updatedLocation = new LatLng(location.getLatitude(),location.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(updatedLocation).title("New Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(updatedLocation));
        coordinates.add(updatedLocation);
        line.setPoints(coordinates);
        Log.i(MY_TAG, "updateLocation   Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
    }

    public void startRecording(View view){
        recording = true;
        Log.i(MY_TAG, "startRecording");
    }

    public void stopRecording(View view){
        recording  = false;
        line.setPoints(coordinates);
        Log.i(MY_TAG, "stopRecording");
        for(int i = 0; i < coordinates.size(); i++){
            Log.i(MY_TAG, "Lat: " + coordinates.get(i).longitude + " Lat: " + coordinates.get(i).longitude);
        }
    }

    public void onConnectionSuspended(int n){

        Log.i(MY_TAG, "onConnectionSuspended");
    }

    public void onConnectionFailed(ConnectionResult cr){

        Log.i(MY_TAG, "onConnectionFailed");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Log.i(MY_TAG, "onStart");
    }
}
