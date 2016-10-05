package com.example.bryant.ghostwatch;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Marker csub_mark;
    private Marker ghost_mark;

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
        LatLng csub = new LatLng(35.349092, -119.104229);
        LatLng ghost = new LatLng(35.349292, -119.104529);

        mMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(csub));
        googleMap.setMinZoomPreference(19.0f);
        googleMap.setMaxZoomPreference(19.0f);
        googleMap.setBuildingsEnabled(true);

        csub_mark = googleMap.addMarker(new MarkerOptions()
                .position(csub).title("Player Location")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.marker)));
        ghost_mark = googleMap.addMarker(new MarkerOptions()
                .position(ghost).title("Ghost")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ghost)));
        googleMap.setOnMarkerClickListener(this);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }
    }
    public boolean onMarkerClick(final Marker marker) {

        if (marker.equals(ghost_mark))
        {
            Intent intent = new Intent(MapsActivity.this, GhostWatch.class);
            MapsActivity.this.startActivity(intent);
        }
        return false;
    }
}
