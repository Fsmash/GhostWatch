package com.example.bryant.ghostwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

public class PlayerSelection extends AppCompatActivity {

    private Button selectLeft;
    private Button selectRight;
    public final String USRNAME = "com.example.bryant.ghostwatch.MAINACTIVITY";
    String usrName;
    private EditText playerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_selection);

        Intent intent = getIntent();
        usrName = intent.getStringExtra(USRNAME);
    }

    public void namePlayer() {
            playerName = (EditText) findViewById(R.id.editText3);

    }

}
