package com.example.bryant.ghostwatch;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, ConnectionCallbacks, OnConnectionFailedListener {

    private final int CAM = 0;
    private final int LOCATION = 1;

    private GoogleMap mMap;
    private Marker csub_mark;
    private Marker ghost_mark;

    public final String USRNAME = "com.example.bryant.ghostwatch.MAINACTIVITY";
    private Socket sk;
    private boolean connected = false;
    private boolean disconnect = false;
    private HashMap<String, Pair>playerLoc;
    private double lat = 0, lng = 0;
    private String playerKey = null;
    private Intent intent;
    protected ObjectOutputStream output = null;
    protected ObjectInputStream input = null;
    protected connectToServer c;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionCheck();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        intent = getIntent();
        String usrName = intent.getStringExtra(USRNAME);
        playerLoc = new HashMap<>();
        c = new connectToServer();
        buildGoogleApiClient();

        if (!connected) {
            c.execute(usrName);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

        //LatLng csub = new LatLng(35.349392, -119.104499);
        //LatLng ghost = new LatLng(35.349292, -119.104559);

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionCheck();
        }
        mMap.setMyLocationEnabled(true);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
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
        boolean cam = (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED);
        if (marker.equals(ghost_mark)) {
            if (cam) {
                camPermission();
            } else {
                Intent intent = new Intent(MapsActivity.this, GhostWatch.class);
                MapsActivity.this.startActivity(intent);
            }
        }
        return false;
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        permissionCheck();
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connected) {
            disconnect = true;
            send(".disconnect");
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    //@Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        boolean icon = false;

        intent = getIntent();
        Bitmap playerIcon = intent.getParcelableExtra("PlayerIcon");
        String playerName = intent.getStringExtra("PlayerName");

        if(playerIcon != null) {
            icon = true;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionCheck();
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();

            LatLng loc = new LatLng(lat, lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            mMap.setMinZoomPreference(20.0f);
            mMap.setMaxZoomPreference(20.0f);
            mMap.setBuildingsEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setAllGesturesEnabled(false);

            if(icon) {
                csub_mark = mMap.addMarker(new MarkerOptions()
                        .position(loc).title(playerName)
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(playerIcon)));
            } else {
                csub_mark = mMap.addMarker(new MarkerOptions()
                        .position(loc).title("Bob Macob")
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.marker)));
            }
            ghost_mark = mMap.addMarker(new MarkerOptions()
                    .position(loc).title("Ghost")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ghost)));
            mMap.setOnMarkerClickListener(this);
            Toast.makeText(this, "Latitude: " + mLastLocation.getLatitude() + ", Longitude: " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: Could not determine location", Toast.LENGTH_LONG).show();
        }
    }

        //@Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        //Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    //@Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        //Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    public void send(String msg) {
        try {
            output.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class updateInfo implements Runnable {
        String []split;
        public void run() {
            Object msg;
            try {
                while (!disconnect) {
                    msg = input.readObject();
                    split = msg.toString().split(":");
                    if (split[0].equals("key")) {
                        playerKey = split[1];
                        Log.d("MESSAGE", "player's key " + playerKey);
                    } else if (split[0].equals("remove")) {
                        if (playerLoc.containsKey(split[1])) {
                            playerLoc.remove(split[1]);
                            Log.d("MESSAGE", "player " + split[1] + " removed");
                        }
                    } else {
                        playerLoc.put(split[0], new Pair(Float.valueOf(split[1]), Float.valueOf(split[2])));
                        Log.d("MESSAGE", "Player " + split[0] + " locations: lat:" + playerLoc.get(split[0]).first +
                                " long:" + playerLoc.get(split[0]).second);
                    }
                }
            } catch (IOException e) {
            } catch (ClassNotFoundException e) {
            } finally {
                try {
                    input.close();
                    output.close();
                    sk.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private class connectToServer extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Log.d("doInBackgroud", params[0] + " connecting to server");
            try {
                String IP = "136.168.201.100";
                int PORT = 9067;
                InetAddress serverAddr = InetAddress.getByName(IP);
                sk = new Socket(serverAddr, PORT);
                try {
                    output = new ObjectOutputStream(sk.getOutputStream());
                    input = new ObjectInputStream(sk.getInputStream());
                    output.writeObject(params[0]);
                    output.flush();
                } catch (IOException e) {
                    return false;
                }
            } catch (IOException ei) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Timer timer  = new Timer();
                connected = true;

                Toast.makeText(getApplicationContext(), "Connected to server.", Toast.LENGTH_SHORT).show();

                new Thread(new updateInfo()).start();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (playerKey != null) {
                            send(playerKey + ":" + mLastLocation.getLatitude() + ":" + mLastLocation.getLongitude());
                        }
                    }
                }, new Date(), 10000);
            } else {
                Toast.makeText(getApplicationContext(), "Failed to connect to server.", Toast.LENGTH_SHORT).show();
                sk = null;
                input = null;
                output = null;
                connected = false;
            }
        }
    }

    private void permissionCheck() {
        // from "https://developer.android.com/training/permissions/requesting.html#perm-request"
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Location required to determine where you are, and how close you are to ghosts!").setTitle("GHOSTWATCH ERROR")
                        .setPositiveButton(R.string.AlertDialog_OK, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // begin request for camera permission on OK
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION);
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {// No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        this.LOCATION);
            }
        }
    }

    private void camPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Camera needs to be enabled for AR experience.").setTitle("App Unable to Start")
                    .setPositiveButton(R.string.AlertDialog_OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // begin request for camera permission on OK
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    CAM);
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else { // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    this.CAM);
        }
    }

    // from "https://developer.android.com/training/permissions/requesting.html#perm-request"
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED)) {
                    // if we are still denied permissions, return to login screen
                    // TODO: return to login screen and exit
                    Toast.makeText(this, "Location required for determining ghost locations!", Toast.LENGTH_SHORT).show();
                    //finish();
                } else {
                    // restart the activity on successful permission grant
                    this.recreate();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
