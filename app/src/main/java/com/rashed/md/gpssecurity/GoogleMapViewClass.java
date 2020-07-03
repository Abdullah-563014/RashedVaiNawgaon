package com.rashed.md.gpssecurity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
                String temporaryLatAndLang=url.split(" ")[0];
                String latLanWithAddress=temporaryLatAndLang.trim();
                if (latLanWithAddress.contains("http://www.google.com/maps?q=")){
                    String[] splitLatLanFromAddress=latLanWithAddress.split("=");
                    latAndLon=splitLatLanFromAddress[1];
                }else {
                    Toast.makeText(this, "Location is not valid.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Url not found", Toast.LENGTH_SHORT).show();
            }


        }



        try {
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW);
            mapIntent.setData(Uri.parse("geo:0,0?q=" + latAndLon));
//            mapIntent.setData(Uri.parse(latAndLon));
            startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Url not valid", Toast.LENGTH_SHORT).show();
        }
    }
}
