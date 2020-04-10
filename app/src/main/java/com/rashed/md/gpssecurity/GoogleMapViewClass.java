package com.rashed.md.gpssecurity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GoogleMapViewClass extends AppCompatActivity {


    Bundle bundle;
    String url;
    String latAndLon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_view_class);


        bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                url = bundle.getString("message");
//                latAndLon = url.substring(34).trim();
                String[] strings=url.split("Date");
                latAndLon=strings[0].trim();
//                String[] latAndLongValue=latAndLon.split(",");
//                latituate=latAndLongValue[0];
//                longituate=latAndLongValue[1];
            } catch (Exception e) {
                Toast.makeText(this, "Url not found", Toast.LENGTH_SHORT).show();
            }


        }


        try {
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW);
//            mapIntent.setData(Uri.parse("geo:0,0?q=" + latAndLon));
            mapIntent.setData(Uri.parse(latAndLon));
            startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Url not valid", Toast.LENGTH_SHORT).show();
        }
    }
}
