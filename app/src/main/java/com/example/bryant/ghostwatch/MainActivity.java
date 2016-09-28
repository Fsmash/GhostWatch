package com.example.bryant.ghostwatch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.wikitude.architect.ArchitectView;

public class MainActivity extends AppCompatActivity {

    public static Context ctx;
    final private String key = "ewIuC8RbftJalqtOiIOmv4zpqE3eb2mqg/DygFqnV1SFCg+dmo+d5pdvcz98jOIVq5h2" +
            "KX4Gsl4j9XG9cLb8OsT5d8fz1pR2bQgJiIQoOW3a/s58wzPGVc+/WwagYbjM04M3mqcO8QRc1ZVXhjK583nLeDc" +
            "XtIb8Nnot6rd/FvFTYWx0ZWRfX6+D/0iAlLpTD0c4HggGNq0jE24Cjj5kI6KCRo2wTFgoD9bjbe9JWGE+Mi9gxd" +
            "rIUHUTmrhUvO46f+tcjAzVXnYdNZNfpUTIoKLRJqWpLN/5CI6e9jxbO22eRhMbfFuPc2Blzw8P16FHgNMyKfZ0X" +
            "+hEOt1ME2SqC0Vm6awKkJzXtnEacFEZkz6uNdmL1F5iSA4a9Mk+uoGbIEk5TZp5naxALY9eaTO3R57pRUurLzqd" +
            "swp5UOmVq15oyVZHwmu2nsJJho+E7r5xnalo4PzElqohpxNX+5gmL04ktBMMRAT1pkUArwkef+Gkr0Bp7hj2yiy" +
            "NzRZ70s8OEbjhAUw3mG3Fqn57tiqdvLsc6nXLBAmRo4WRPjJSsNOtoTUWGK2TFpezSyXHwPZLeFO90ZwVMkQM9K" +
            "oBkQjVJYJhLWNg/hPmG7S/1XgJ16JoyqFjSlZ8ymjqo1euxjYn0F47UeSdS7Yj8I9x1NnL45FIEYgsWL+OebjdQ" +
            "gM9WhY=";

    protected ArchitectView architectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.ctx = getApplicationContext();
        //this.architectView = (ArchitectView)this.findViewById( this.getArchitectViewId()  );

        if (ArchitectView.isDeviceSupported(ctx)) {
            Toast.makeText(getApplicationContext(), "Application supported by wikitude", Toast.LENGTH_SHORT).show();
            //this.architectView.onCreate(key);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


}
