package com.example.bryant.ghostwatch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wikitude.architect.ArchitectView;


public class MainActivity extends AppCompatActivity {

    /* constants for permissions */
    private final int CAM = 0;

    public Context ctx;
    private Button login;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.ctx = getApplicationContext();
        login = (Button) findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                camPermCheck();
            }
        });

        if (ArchitectView.isDeviceSupported(ctx)) {
            Toast.makeText(getApplicationContext(), "Application supported by wikitude", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    // for GhostWatch class
    private void camPermCheck() {
        // from "https://developer.android.com/training/permissions/requesting.html#perm-request"
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Camera needs to be enabled for AR experience.").setTitle("App Unable to Start");

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else { // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        this.CAM);
            }
        }
        else {
            // we have camera permission, start GhostWatch
            startCam();
        }
    }

    private void startCam() {
        Intent intent = new Intent(this, GhostWatch.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    // from "https://developer.android.com/training/permissions/requesting.html#perm-request"
    // TODO: will add more permissions if/when needed
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String [] permissions, @NonNull int [] grantResults) {
        switch (requestCode) {
            case CAM: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCam();
                } else {
                    finish();   // cleanest way to "exit"
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
