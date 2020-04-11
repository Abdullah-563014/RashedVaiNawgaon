package com.rashed.md.gpssecurity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText speedEditText;
    private Button setSpeedButton;
    private TextView currentSpeedTextView;
    private String inputSpeed;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar=findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        View view= (View) getLayoutInflater().inflate(R.layout.appbar_custom_view,null);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setCustomView(view);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        initAll();

        showCurrentSpeedToTextView();
    }

    private void showCurrentSpeedToTextView() {
        String currentSpeed=Utils.getStringFromStorage(getApplicationContext(),"CurrentAlarmedSpeed");
        if (currentSpeed==null){
            currentSpeed="80";
        }
        currentSpeedTextView.setText("Current alarm speed:- "+currentSpeed+" Km/h");
    }

    private void initAll() {
        speedEditText=findViewById(R.id.settingsActivitySetSpeedAlarmEditTextId);
        setSpeedButton=findViewById(R.id.settingsActivitySetSpeedAlarmButtonId);
        currentSpeedTextView=findViewById(R.id.settingsActivityCurrentSpeedTextViewId);

        setSpeedButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.settingsActivitySetSpeedAlarmButtonId:
                inputSpeed=speedEditText.getText().toString();
                if (!TextUtils.isEmpty(inputSpeed)){
                    setSpeed();
                }else {
                    speedEditText.setError("Please input valid speed.");
                    speedEditText.setFocusable(true);
                }
                break;
        }
    }

    private void setSpeed(){

    }
}
