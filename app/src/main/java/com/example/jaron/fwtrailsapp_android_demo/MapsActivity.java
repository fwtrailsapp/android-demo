package com.example.jaron.fwtrailsapp_android_demo;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlLayer;
import com.google.android.gms.location.LocationListener;

import java.io.InputStream;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String MY_TAG = "LogMessage";
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Location mCurrentLocation;
    private String mLatitudeText, mLongitudeText;
    protected LocationRequest mLocationRequest;

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
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            //These 2 lines replaced the 2 above from the Google code sample
            mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            mLongitudeText = String.valueOf(mLastLocation.getLongitude());
        }
        startLocationUpdates();
        Log.i(MY_TAG, "onConnected");
    }

    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                mCurrentLocation = location;
                updateLocation(location);
            }
        };

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
        Log.i(MY_TAG, "startLocationUpdates");
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
        InputStream is = getResources().openRawResource(R.raw.doc);
        try {
            KmlLayer layer = new KmlLayer(mMap, is, getApplicationContext());
            layer.addLayerToMap();
//        mMap.addGroundOverlay(R.raw.mkl);
        }
        catch(org.xmlpull.v1.XmlPullParserException e){
            Log.i(null, "catch1");
        }
        catch(java.io.IOException e){
            Log.i(null, "catch2");
        }

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

        Log.i(MY_TAG, "onMapReady");
    }

    private void updateLocation(Location location)
    {
        // Figure out how to move the screen to keep up with the dot in the center.
        LatLng updatedLocation = new LatLng(location.getLatitude(),location.getLongitude());
        LatLngBounds newLocation = new LatLngBounds(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(location.getLatitude(), location.getLongitude()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(newLocation, 5));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(updatedLocation));
        mMap.addMarker(new MarkerOptions().position(updatedLocation).title("New Location"));
        Log.i(MY_TAG, "updateLocation");
        Log.i(MY_TAG, "Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
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
