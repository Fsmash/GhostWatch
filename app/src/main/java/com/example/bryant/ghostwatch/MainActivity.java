package com.example.bryant.ghostwatch;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wikitude.architect.ArchitectView;

import java.io.File;

/**
 * Created by bryant 9/28/16
 */

public class MainActivity extends AppCompatActivity {
    /* constants for permissions */
    private final int CAM = 0;
    private final int LOCATION = 1;

    private EditText username;
    public final String LOGIN = "com.example.bryant.ghostwatch.MAINACTIVITY";
    private MediaPlayer loginTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        deviceSupport();

        // Clearing ArchitectView cache
        clearCache(ArchitectView.getCacheDirectoryAbsoluteFilePath(this));

        Button login = (Button) findViewById(R.id.login_button);
        username = (EditText) findViewById(R.id.editText2);
        loginTheme = MediaPlayer.create(this, R.raw.spooky);
        loginTheme.setLooping(true);

        //locationPermission();
        //camPermission();

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                permissionCheck();
            }
        });

        loginTheme.start();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!loginTheme.isPlaying())
            loginTheme.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (loginTheme.isPlaying())
            loginTheme.stop();
        loginTheme.release();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loginTheme = MediaPlayer.create(this, R.raw.spooky);
        loginTheme.setLooping(true);
    }

    @Override
    protected void onResume() {
        // TODO: make camera wait until onRequestPermissionsResult completes w/o error (if denied on resume)
        super.onResume();
        if (!loginTheme.isPlaying())
            loginTheme.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loginTheme.isPlaying())
            loginTheme.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // To clear architectView cache files every time app is run.
    private void clearCache(final String path) {
        try {
            final File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                final String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // for GhostWatch class
    private void permissionCheck() {
        // from "https://developer.android.com/training/permissions/requesting.html#perm-request"
        boolean loc = (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED);
        if (loc) {
            locationPermission();
        } else {
            startPlayerSelection();
        }
    }

    private void locationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Location required to determine where you are, and how close you are to ghosts!").setTitle("App Unable to Start")
                    .setPositiveButton(R.string.AlertDialog_OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // begin request for camera permission on OK
                            ActivityCompat.requestPermissions(MainActivity.this,
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

    private void deviceSupport() {
        if (ArchitectView.isDeviceSupported(this)) {
            Toast.makeText(getApplicationContext(), "Application supported by Wikitude.", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void startPlayerSelection() {
        Intent intent = new Intent(MainActivity.this, PlayerSelection.class);
        String usr = username.getText().toString();
        intent.putExtra(LOGIN, usr);
        startActivity(intent);
    }

    private void startMap() {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        String usr = username.getText().toString();
        intent.putExtra(LOGIN, usr);
        startActivity(intent);
    }

    // from "https://developer.android.com/training/permissions/requesting.html#perm-request"
    // TODO: will add more permissions if/when needed
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAM: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPlayerSelection();
                    startMap();
                } else {
                    finish();   // cleanest way to "exit"
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
