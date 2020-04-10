package com.rashed.md.gpssecurity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class InputPhoneNumberActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    String phoneNumber;
    PendingIntent sentPI, deliveredPI;
    Toolbar toolbar;
    BroadcastReceiver deliveredBroadcastReceiver,sentBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_phone_number);
        toolbar=findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        LayoutInflater inflater=getLayoutInflater();
        View view= (View) inflater.inflate(R.layout.appbar_custom_view,null);
        getSupportActionBar().setCustomView(view);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        editText = findViewById(R.id.editTextPhoneNumber);
        button = findViewById(R.id.okButtonPhoneNumber);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                phoneNumber = editText.getText().toString().trim();
                intent.putExtra("phoneNumber", phoneNumber);
                setResult(Activity.RESULT_OK, intent);
                MainActivity mainActivity = new MainActivity();
                try {
                    double some=Double.parseDouble(phoneNumber);
                    sendSaveNumber();
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Not Valid Number", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

    }

    public void sendSaveNumber() {
        try {

            messageHandleMethod();
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "Saveme", sentPI, deliveredPI);
        } catch (Exception e) {
            Toast.makeText(getApplication(), "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }

    public void messageHandleMethod() {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        sentPI = PendingIntent.getBroadcast(getApplication(), 0,
                new Intent(SENT), 0);

        deliveredPI = PendingIntent.getBroadcast(getApplication(), 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        sentBroadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Failed to send",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "Failed to send",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Failed to send",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Failed to send",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        registerReceiver(sentBroadcastReceiver, new IntentFilter(SENT));

        //---when the SMS has been delivered---


        deliveredBroadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        registerReceiver(deliveredBroadcastReceiver, new IntentFilter(DELIVERED));
    }


    @Override
    protected void onDestroy() {
        if (sentBroadcastReceiver!=null){
            unregisterReceiver(sentBroadcastReceiver);
        }
        if (deliveredBroadcastReceiver!=null){
            unregisterReceiver(deliveredBroadcastReceiver);
        }
        sentPI=null;
        deliveredPI=null;
        super.onDestroy();
    }
}
