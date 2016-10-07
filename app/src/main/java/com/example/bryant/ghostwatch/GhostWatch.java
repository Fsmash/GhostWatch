package com.example.bryant.ghostwatch;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.StartupConfiguration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by bryant on 9/28/16.
 */

public class GhostWatch extends AppCompatActivity {
    /* constants for permissions */

    private final String key = "ewIuC8RbftJalqtOiIOmv4zpqE3eb2mqg/DygFqnV1SFCg+dmo+d5pdvcz98jOIVq5h2" +
            "KX4Gsl4j9XG9cLb8OsT5d8fz1pR2bQgJiIQoOW3a/s58wzPGVc+/WwagYbjM04M3mqcO8QRc1ZVXhjK583nLeDc" +
            "XtIb8Nnot6rd/FvFTYWx0ZWRfX6+D/0iAlLpTD0c4HggGNq0jE24Cjj5kI6KCRo2wTFgoD9bjbe9JWGE+Mi9gxd" +
            "rIUHUTmrhUvO46f+tcjAzVXnYdNZNfpUTIoKLRJqWpLN/5CI6e9jxbO22eRhMbfFuPc2Blzw8P16FHgNMyKfZ0X" +
            "+hEOt1ME2SqC0Vm6awKkJzXtnEacFEZkz6uNdmL1F5iSA4a9Mk+uoGbIEk5TZp5naxALY9eaTO3R57pRUurLzqd" +
            "swp5UOmVq15oyVZHwmu2nsJJho+E7r5xnalo4PzElqohpxNX+5gmL04ktBMMRAT1pkUArwkef+Gkr0Bp7hj2yiy" +
            "NzRZ70s8OEbjhAUw3mG3Fqn57tiqdvLsc6nXLBAmRo4WRPjJSsNOtoTUWGK2TFpezSyXHwPZLeFO90ZwVMkQM9K" +
            "oBkQjVJYJhLWNg/hPmG7S/1XgJ16JoyqFjSlZ8ymjqo1euxjYn0F47UeSdS7Yj8I9x1NnL45FIEYgsWL+OebjdQ" +
            "gM9WhY=";

    public final String LOGIN = "com.example.bryant.ghostwatch.MAINACTIVITY";
    private final int CAM = 0;
    private final String IP = "136.168.201.102";
    private final int PORT = 9067;
    private ArchitectView architectView;
    private StartupConfiguration config;
    public ObjectOutputStream out = null;
    public ObjectInputStream in = null;
    private MediaPlayer boo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.wikitude);
        //this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
        //config = new StartupConfiguration(key, StartupConfiguration.Features.Geo, StartupConfiguration.CameraPosition.BACK);

        Intent intent = getIntent();
        String usrName = intent.getStringExtra(LOGIN);

        setupArchitectView();
        //if (this.architectView == null)
        //    Log.e(this.getClass().getName(), "architectView is NULL");
        /*
        try {
            this.architectView.onCreate(config);
        } catch (RuntimeException rex) {
            this.architectView = null;
            Toast.makeText(getApplicationContext(), "can't create Architect View", Toast.LENGTH_SHORT).show();
            Log.e(this.getClass().getName(), "Exception in ArchitectView.onCreate()", rex);
        }*/

        boo = MediaPlayer.create(this, R.raw.boo);
        boo.setLooping(true);
        boo.start();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ConnectToServer c = new ConnectToServer();
        c.execute(usrName);
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (this.architectView != null) {
            // call mandatory live-cycle method of architectView
            this.architectView.onPostCreate();

            try {
                this.architectView.load("GhostWatchWorld/index.html");
                Log.e(this.getClass().getName(), "Loaded Architect world");
            } catch (IOException e) {
                Log.e(this.getClass().getName(), "Unable to load Architect world", e);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        boo.start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //setupArchitectView();
        boo = MediaPlayer.create(this, R.raw.boo);
        boo.setLooping(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //this.architectView.onDestroy();
        if (boo.isPlaying()) {
            boo.stop();
            boo.release();
        }
    }

    @Override
    protected void onResume() {
        permissionCheck();  // always recheck permissions
        // TODO: make camera wait until onRequestPermissionsResult completes w/o error (if denied on resume)
        super.onResume();
        this.architectView.onResume();
        boo.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.architectView.onPause();
        boo.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.architectView.onDestroy();
        if (boo.isPlaying()) {
            boo.stop();
            boo.release();
        }
    }

    private void setupArchitectView() {
        this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
        config = new StartupConfiguration(key, StartupConfiguration.Features.Geo, StartupConfiguration.CameraPosition.BACK);

        try {
            this.architectView.onCreate(config);
        } catch (RuntimeException rex) {
            this.architectView = null;
            Toast.makeText(getApplicationContext(), "can't create Architect View", Toast.LENGTH_SHORT).show();
            Log.e(this.getClass().getName(), "Exception in ArchitectView.onCreate()", rex);
        }
    }

    private class ConnectToServer extends AsyncTask<String, Void, Boolean> {
        //String msg = "";
        //TextView msgBoard;

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                InetAddress serverAddr = InetAddress.getByName(IP);
                Socket sk = new Socket(serverAddr, PORT);
                try {
                    out = new ObjectOutputStream(sk.getOutputStream());
                    in = new ObjectInputStream(sk.getInputStream());
                    out.writeObject(params[0]);
                    out.flush();
                } catch (IOException e) {
                    return false;
                }
            } catch (IOException ei) {
                return false;
            }
            Log.d("doInBackgroud", "connected to server");
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(getApplicationContext(), "Connected to server.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to connect to server.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void permissionCheck() {
        // from "https://developer.android.com/training/permissions/requesting.html#perm-request"
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Camera needs to be enabled for AR experience.").setTitle("App Unable to Start")
                        .setPositiveButton(R.string.AlertDialog_OK, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // begin request for camera permission on OK
                                ActivityCompat.requestPermissions(GhostWatch.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        CAM);
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {// No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        this.CAM);
            }
        }
    }

    // from "https://developer.android.com/training/permissions/requesting.html#perm-request"
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAM: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // if we are still denied permissions, return to login screen
                    // TODO: return to login screen and exit
                    Toast.makeText(this, "Camera permission required for AR!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
