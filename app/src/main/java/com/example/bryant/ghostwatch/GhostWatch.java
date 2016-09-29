package com.example.bryant.ghostwatch;

//import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.StartupConfiguration;

import java.io.IOException;

/**
 * Created by bryant on 9/28/16.
 */

public class GhostWatch extends AppCompatActivity {

    private final String key = "ewIuC8RbftJalqtOiIOmv4zpqE3eb2mqg/DygFqnV1SFCg+dmo+d5pdvcz98jOIVq5h2KX4Gsl4j9XG9cLb8OsT5d8fz1pR2bQgJiIQoOW3a/s58wzPGVc+/WwagYbjM04M3mqcO8QRc1ZVXhjK583nLeDcXtIb8Nnot6rd/FvFTYWx0ZWRfX6+D/0iAlLpTD0c4HggGNq0jE24Cjj5kI6KCRo2wTFgoD9bjbe9JWGE+Mi9gxdrIUHUTmrhUvO46f+tcjAzVXnYdNZNfpUTIoKLRJqWpLN/5CI6e9jxbO22eRhMbfFuPc2Blzw8P16FHgNMyKfZ0X+hEOt1ME2SqC0Vm6awKkJzXtnEacFEZkz6uNdmL1F5iSA4a9Mk+uoGbIEk5TZp5naxALY9eaTO3R57pRUurLzqdswp5UOmVq15oyVZHwmu2nsJJho+E7r5xnalo4PzElqohpxNX+5gmL04ktBMMRAT1pkUArwkef+Gkr0Bp7hj2yiyNzRZ70s8OEbjhAUw3mG3Fqn57tiqdvLsc6nXLBAmRo4WRPjJSsNOtoTUWGK2TFpezSyXHwPZLeFO90ZwVMkQM9KoBkQjVJYJhLWNg/hPmG7S/1XgJ16JoyqFjSlZ8ymjqo1euxjYn0F47UeSdS7Yj8I9x1NnL45FIEYgsWL+OebjdQgM9WhY=";
    private ArchitectView architectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(this.getClass().getName(),"in GhostWatchClass");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.wikitude);
        //Log.e(this.getClass().getName(),"in GhostWatchClass");
        this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
        final StartupConfiguration config = new StartupConfiguration(key);

        if (architectView == null)
            Log.e(this.getClass().getName(), "architectView is NULL");

        Log.e(this.getClass().getName(),"in GhostWatchClass 2");
        try {
            this.architectView.onCreate(config);
        } catch (RuntimeException rex) {
            this.architectView = null;
            Toast.makeText(getApplicationContext(), "can't create Architect View", Toast.LENGTH_SHORT).show();
            Log.e(this.getClass().getName(), "Exception in ArchitectView.onCreate()", rex);
        }

        Log.e(this.getClass().getName(),"in GhostWatchClass3");
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if ( this.architectView != null ) {
            // call mandatory live-cycle method of architectView
            this.architectView.onPostCreate();
        }
    }

}
