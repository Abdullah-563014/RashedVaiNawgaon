package com.rashed.md.gpssecurity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SmsShowActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView textView;
    Bundle bundle;
    String message=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_show);
        toolbar=findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Incoming sms");

        textView=findViewById(R.id.smsShowTextViewId);

        bundle=getIntent().getExtras();
        if (bundle!=null){
            message=bundle.getString("message");
        }

        textView.setText(message);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
