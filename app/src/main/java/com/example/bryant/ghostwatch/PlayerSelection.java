package com.example.bryant.ghostwatch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PlayerSelection extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.GhostWatch";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Button selectLeft;
    private Button selectRight;
    public final String USRNAME = "com.example.bryant.ghostwatch.MAINACTIVITY";
    String usrName;
    Bitmap imageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_selection);

        Intent intent = getIntent();
        usrName = intent.getStringExtra(USRNAME);

        final EditText playerName = (EditText) findViewById(R.id.editText3);

        final TextView txtView = (TextView)findViewById(R.id.textView2);

        final Button iconButton = (Button)findViewById(R.id.button2);
        final Button nameButton = (Button)findViewById(R.id.button3);

        iconButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        nameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                usrName = playerName.getText().toString();
                txtView.setText(usrName);
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("PlayerIcon", imageBitmap);
            intent.putExtra("PlayerName", usrName);

            Intent mapIntent = new Intent(PlayerSelection.this, MapsActivity.class);
            startActivity(mapIntent);
        }
    }
}
