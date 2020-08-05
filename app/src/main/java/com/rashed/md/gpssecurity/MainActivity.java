package com.rashed.md.gpssecurity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    Button setPhoneNumberButton,autoLockOnButton,autoLockOffButton,voiceCommandButton,vehicleInfoButton,liveTrackButton;
    ImageButton sendFindSmsButton,
            sendStatusSmsButton,
//            sendAlertSmsButton,
//            sendEasySmsButton,
            sendOnSmsButton,
            sendOffSmsButton,
            sendVibrationSensorOffSmsButton,
            sendVibrationSensorOnSmsButton,
            settingsButton;
//            sendStartSmsButton,
//            sendCarOffSmsButton,
//            sendCarOnSmsButton;
//            lockCallButton,
//            unlockCallButton;
    Intent intent;
    public static String phoneNumber = null;
    String findTextMessage = "Find";
    String statusTextMessage = "Status";
//    String alertTextMessage = "Accon";
//    String easyTextMessage = "Accoff";
    String onTextMessage = "On";
    String offTextMessage = "Off";
    String vibrationSensorOffTextMessage = "Easy";
    String vibrationSensorOnTextMessage = "Alert";
    String autoLockOnTextMessage = "Klock,123456,";
    String autoLockOffTextMessage = "Kulock";
//    String motionAlarmOffTextMessage="126#";
//    String startTextMessage = "Start";
//    String carOffTextMessage = "9400000";
//    String carOnTextMessage = "9410000";
    SharedPreferences sharedPreferences, phoneNumberValueSharedPreference;
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver sentBroadcastReceiver, deliveredBroadcastReceiver;
    TextView showPhoneNumberTextView,targetMinuteStatusTextView;
    Button contactCall, contactFacebook;
    String lastButtonId,targetMinute;
    TextToSpeech textToSpeech;
    boolean voiceSendSms=false;
    private LinearLayout rootLayout;
    private AlertDialog targetMinuteAlertDialog;
    private EditText targetMinuteEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        LayoutInflater inflater=getLayoutInflater();
        View view= (View) inflater.inflate(R.layout.appbar_custom_view,null);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setCustomView(view);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        initAll();

        checkAllPermission();

        retrieveDevicePhoneNumberFromStorage();

        initTextToSpeech();

        updateLastButtonStatus();

//        startBackgroundAnimation();


//-----------------------onCreate method end-------------------------------
    }


    private void initAll() {
        setPhoneNumberButton = findViewById(R.id.deviceNumberButtonId);
        sendFindSmsButton = findViewById(R.id.findSmsButtonId);
        sendStatusSmsButton = findViewById(R.id.statusSmsButtonId);
        sendOnSmsButton = findViewById(R.id.onSmsButtonId);
        sendOffSmsButton = findViewById(R.id.offSmsButtonId);
        sendVibrationSensorOffSmsButton = findViewById(R.id.vibSendorOffSmsButtonId);
        sendVibrationSensorOnSmsButton = findViewById(R.id.vibSensorOnSmsButtonId);
        settingsButton=findViewById(R.id.settingsButtonId);
        showPhoneNumberTextView = findViewById(R.id.showPhoneNumberTextViewId);
        contactCall = findViewById(R.id.contactUsButtonId);
        contactFacebook = findViewById(R.id.facebookButtonId);
        voiceCommandButton=findViewById(R.id.voiceCommandButtonId);
        vehicleInfoButton=findViewById(R.id.vehicleInfoButtonId);
        liveTrackButton=findViewById(R.id.liveTrackButtonId);
        autoLockOnButton=findViewById(R.id.autoLockOnButtonId);
        autoLockOffButton=findViewById(R.id.autoLockOffButtonId);
        rootLayout=findViewById(R.id.mainActivityRootLayoutId);


        setPhoneNumberButton.setOnClickListener(this);
        sendFindSmsButton.setOnClickListener(this);
        sendStatusSmsButton.setOnClickListener(this);
        sendOnSmsButton.setOnClickListener(this);
        sendOffSmsButton.setOnClickListener(this);
        sendVibrationSensorOffSmsButton.setOnClickListener(this);
        sendVibrationSensorOnSmsButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
        voiceCommandButton.setOnClickListener(this);
        contactCall.setOnClickListener(this);
        contactFacebook.setOnClickListener(this);
        vehicleInfoButton.setOnClickListener(this);
        liveTrackButton.setOnClickListener(this);
        autoLockOnButton.setOnClickListener(this);
        autoLockOffButton.setOnClickListener(this);
    }

    private void startBackgroundAnimation(){
        AnimationDrawable animationDrawable = (AnimationDrawable) rootLayout.getBackground();
        animationDrawable.setEnterFadeDuration(500);
        animationDrawable.setExitFadeDuration(1500);
        animationDrawable.start();
    }

    private void initTextToSpeech(){
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
                    } else {
                        textToSpeech.setSpeechRate(0.7f);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Initialization Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void retrieveDevicePhoneNumberFromStorage(){
        sharedPreferences = getSharedPreferences("phone", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", null);
        if (phone != null) {
            phoneNumber = phone;
            String subNumb=phoneNumber.substring(phoneNumber.length()-2,phoneNumber.length());
            showPhoneNumberTextView.setText("Last Two Digit Of Number:- "+subNumb);
        }
    }

    private void checkAllPermission(){
        int PERMISSION_ALL = 1;
        String[] permissions = {Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.INTERNET,
                Manifest.permission.VIBRATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        if (!hasPermission(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        }
    }

    private void showTargetMinuteAlertDialog() {
        View view=getLayoutInflater().inflate(R.layout.custom_layout_for_target_time,null,false);
        Button submitButton=view.findViewById(R.id.alertDialogTargetMinuteButtonId);
        targetMinuteEditText=view.findViewById(R.id.alertDialogTargetMinuteEditTextId);
        targetMinuteStatusTextView=view.findViewById(R.id.alertDialogTargetMinuteTextViewId);
        submitButton.setOnClickListener(this);

        targetMinuteStatusTextView.setText("Current Target Minute For Auto Lock:- "+Utils.getStringFromStorage(MainActivity.this,"TargetMinuteKey"));

        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setView(view);
        targetMinuteAlertDialog=builder.create();
        if (targetMinuteAlertDialog.getWindow()!=null){
            targetMinuteAlertDialog.getWindow().getAttributes().windowAnimations=R.style.DialogTheme;
        }
        if (!isFinishing()){
            targetMinuteAlertDialog.show();
        }
    }

    private void startTargetMinuteSmsOperation() {
        targetMinute=targetMinuteEditText.getText().toString();
        if (!TextUtils.isEmpty(targetMinute)){
            Utils.setStringToStorage(MainActivity.this,"TargetMinuteKey",targetMinute);
            sendSms(autoLockOnTextMessage+targetMinute,null,"bike's auto lock turn on");
            targetMinuteAlertDialog.dismiss();
        }else {
            Toast.makeText(this, "Please input your target minute.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openTrackingApp() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo info=pm.getPackageInfo("com.desn.ffb.jdtracker", PackageManager.GET_ACTIVITIES);
            Intent intent=pm.getLaunchIntentForPackage("com.desn.ffb.jdtracker");
            if (intent!=null){
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else {
                Toast.makeText(this, "intent is null", Toast.LENGTH_SHORT).show();
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(MainActivity.this, "Tracking app not installed in your phone", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.desn.ffb.jdtracker"));
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.deviceNumberButtonId:
                vibrateCreation();
                if (Build.VERSION.SDK_INT > 22) {
                    String[] permissions = {
                            Manifest.permission.SEND_SMS
                    };
                    if (!hasPermission(this, permissions)) {
                        Toast.makeText(this, "You need to provide some permission to use the app. To grant the permission please close the app and reopen to see the permissions dialog.Thank you", Toast.LENGTH_SHORT).show();
                    }else {
                        intent = new Intent(MainActivity.this, InputPhoneNumberActivity.class);
                        startActivityForResult(intent, 100);
                    }
                } else {
                    intent = new Intent(MainActivity.this, InputPhoneNumberActivity.class);
                    startActivityForResult(intent, 100);
                }
                break;

            case R.id.findSmsButtonId:
                sendSms(findTextMessage,"findSmsButtonId","bike location");
                break;

            case R.id.statusSmsButtonId:
                sendSms(statusTextMessage,"statusSmsButtonId","bike status");
                break;

            case R.id.onSmsButtonId:
                sendSms(onTextMessage,"onSmsButtonId","bike unlock");
                break;

            case R.id.offSmsButtonId:
                sendSms(offTextMessage,"offSmsButtonId","bike lock");
                break;

//            case R.id.startSmsButtonId:
//                sendSms(startTextMessage,"startSmsButtonId","bike start");
//                break;

            case R.id.vibSendorOffSmsButtonId:
                sendSms(vibrationSensorOffTextMessage,"vibSendorOffSmsButtonId","bike's vibration sensor off");
                break;

            case R.id.vibSensorOnSmsButtonId:
                sendSms(vibrationSensorOnTextMessage,"vibSensorOnSmsButtonId","bike's vibration sensor on");
//                sendSms(motionAlarmOffTextMessage,"vibSensorOnSmsButtonId",null);
                break;

            case R.id.settingsButtonId:
                vibrateCreation();
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                break;

            case R.id.autoLockOnButtonId:
                vibrateCreation();
                showTargetMinuteAlertDialog();
                break;

            case R.id.alertDialogTargetMinuteButtonId:
                startTargetMinuteSmsOperation();
                break;

            case R.id.autoLockOffButtonId:
                vibrateCreation();
                sendSms(autoLockOffTextMessage,null,"bike's auto lock turn off");
                break;

//            case R.id.lockCallButtonId:
//                makeCall(phoneNumber,",*,*,*,*");
//                break;
//
//            case R.id.unLockCallButtonId:
//                makeCall(phoneNumber,",%23,%23,%23,%23");
//                break;

            case R.id.contactUsButtonId:
                makeCall("01718171529",null,"contact us");
                break;

            case R.id.facebookButtonId:
                openUrl("https://www.facebook.com/rashedul.bari.7");
                break;

            case R.id.liveTrackButtonId:
                openTrackingApp();
                break;

            case R.id.vehicleInfoButtonId:
                openUrl("http://yq.18gps.net/?locale=en&back=true");
                break;

            case R.id.voiceCommandButtonId:
                vibrateCreation();
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    voiceSendSms = true;
                    startActivityForResult(intent, 10);
                } else {
                    Toast.makeText(MainActivity.this, "Your device don't support voice command", Toast.LENGTH_SHORT).show();
                }
                break;
        }
//----------------------onClick method end---------------------------------
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK && requestCode == 100) {
            String phoneNumberData = data.getStringExtra("phoneNumber");
            sharedPreferences = getSharedPreferences("phone", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("phone", phoneNumberData);
            editor.apply();
            phoneNumber = phoneNumberData;
            String subNumb=phoneNumberData.substring(phoneNumberData.length()-2,phoneNumberData.length());
            showPhoneNumberTextView.setText("Last Two Digit Of Number:- "+subNumb);
            Toast.makeText(this, phoneNumberData + " Successfully set as your device phone number", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == 100 && resultCode != RESULT_OK) {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }




        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> result = null;
                if (data != null) {
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                } else {
                    return;
                }
                for (int i = 0; i < result.size(); i++) {
                    String value = result.get(i).toLowerCase();
                    if (value.contains("location")) {
                        voiceCommand(findTextMessage,"ok, trying to retrieve your bike location","findSmsButtonId");
                    }

                    else if (value.contains("status")) {
                        voiceCommand(statusTextMessage,"ok, trying to retrieve your bike status","statusSmsButtonId");
                    }

//                    else if (value.contains("call of") || value.contains("call off")) {
//                        voiceCommand(easyTextMessage,"ok, trying to turn off call mode","easySmsButtonId");
//                    }
//
//                    else if (value.contains("call on") || value.contains("call 1")) {
//                        voiceCommand(alertTextMessage,"ok, trying to turn on call mode","alertSmsButtonId");
//                    }

                    else if (value.contains("bike on") || value.contains("mike on") || value.contains("mic on") || value.contains("bike unlock") || value.contains("mike unlock") || value.contains("mic unlock")) {
                        voiceCommand(onTextMessage,"ok, trying to unlock your bike","onSmsButtonId");
                    }

                    else if (value.contains("bike off") || value.contains("mike off") || value.contains("mic off") || value.contains("bike lock") || value.contains("mike lock") || value.contains("mic lock")) {
                        voiceCommand(offTextMessage,"ok, trying to lock your bike","offSmsButtonId");
                    }

//                    else if (value.contains("bike start")) {
//                        voiceCommand(startTextMessage,"ok, trying to start your bike","startSmsButtonId");
//                    }


                    else if (value.contains("vibration on")) {
                        voiceCommand(vibrationSensorOnTextMessage,"ok, trying to turn on vibration sensor","vibSensorOnSmsButtonId");
//                        voiceCommand(motionAlarmOffTextMessage,null,"vibSensorOnSmsButtonId");
                    }


                    else if (value.contains("vibration off")) {
                        voiceCommand(vibrationSensorOffTextMessage,"ok, trying to turn off vibration sensor","vibSendorOffSmsButtonId");
                    }
                    else {
                        voiceCommand(null,"your command is incorrect, please try again",null);
                    }
                }
            } else {
                Toast.makeText(this, "Not Detected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void voiceCommand(String smsCommand, String voiceCommand,String buttonId){
        if (smsCommand!=null && !smsCommand.isEmpty()){
            if (voiceSendSms) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(voiceCommand, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    textToSpeech.speak(voiceCommand, TextToSpeech.QUEUE_FLUSH, null);
                }
                try {
                    if (!TextUtils.isEmpty(phoneNumber)){
                        double num = Double.parseDouble(phoneNumber);
                    }else {
                        Toast.makeText(this, "Please set your device phone number", Toast.LENGTH_SHORT).show();
                    }
                    if (buttonId!=null && !buttonId.isEmpty()){
                        sendSmsFromVoiceCommand(smsCommand,buttonId);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Device's phone number not valid", Toast.LENGTH_SHORT).show();
                }
                voiceSendSms = false;
            }
        }else {
            if (voiceSendSms == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(voiceCommand, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    textToSpeech.speak(voiceCommand, TextToSpeech.QUEUE_FLUSH, null);
                }
                voiceSendSms = false;
            }
        }

    }

    public void makeCall(String number, String symbole,String speechText){
        vibrateCreation();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null);
        }
//        lastButtonId = "lockCallButtonId";
        try {
//            storeLastButtonId();
//            updateLastButtonStatus();
            if (number != null && !number.isEmpty()) {
                double num = Double.parseDouble(number);
            } else {
                Toast.makeText(this, "Please set your phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > 22) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You need to provide some permission to use the app. To grant the permission please close the app and reopen to see the permissions dialog.Thank you", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    callHandler(number,symbole);
                }
            } else {
                callHandler(number,symbole);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please set your phone number", Toast.LENGTH_SHORT).show();
        }

    }

    private void callHandler(String number,String symbole){
        if (symbole!=null && !symbole.isEmpty()){
            String callNumber = number + symbole;
            Uri uri = Uri.parse("tel:" + callNumber);
            intent = new Intent(Intent.ACTION_CALL, uri);
            startActivity(intent);
        }else {
            Uri uri = Uri.parse("tel:" + number);
            intent = new Intent(Intent.ACTION_CALL, uri);
            startActivity(intent);
        }
    }

    public void sendSms(String smsCommand,String buttonId,String speechText){
        vibrateCreation();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null);
        }
        lastButtonId = buttonId;
        try {
            storeLastButtonId();
            updateLastButtonStatus();
            if (phoneNumber != null) {
                double num = Double.parseDouble(phoneNumber);
            } else {
                Toast.makeText(this, "Please set your device phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > 22) {
                String[] permissions = {
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.VIBRATE
                };
                if (!hasPermission(this, permissions)) {
                    Toast.makeText(this, "You need to provide some permission to use the app. To grant the permission please close the app and reopen to see the permissions dialog.Thank you", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    sendMessageHandle(smsCommand);
                }
            } else {
                sendMessageHandle(smsCommand);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please set your device phone number", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendSmsFromVoiceCommand(String smsCommand,String buttonId){
        vibrateCreation();
        lastButtonId = buttonId;
        try {
            storeLastButtonId();
            updateLastButtonStatus();
            if (phoneNumber != null) {
                double num = Double.parseDouble(phoneNumber);
            } else {
                Toast.makeText(this, "Please set your device phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > 22) {
                String[] permissions = {
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.VIBRATE
                };
                if (!hasPermission(this, permissions)) {
                    Toast.makeText(this, "You need to provide some permission to use the app. To grant the permission please close the app and reopen to see the permissions dialog.Thank you", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    sendMessageHandle(smsCommand);
                }
            } else {
                sendMessageHandle(smsCommand);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please set your device phone number", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendMessageHandle(String command){
        try {
            messageHandleMethod();
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, command, sentPI, deliveredPI);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }

    private void openUrl(String url){
        vibrateCreation();
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You need to provide some permission to use the app. To grant the permission please close the app and reopen to see the permissions dialog.Thank you", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    openUrlHandler(url);
                }
            } else {
                openUrlHandler(url);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void openUrlHandler(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        Intent chooser=Intent.createChooser(i,"Choose Browser");
        startActivity(chooser);
    }

    public void updateLastButtonStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("LastButton", MODE_PRIVATE);
        String lastButton = sharedPreferences.getString("lastButton", null);
        if (lastButton != null && !lastButton.isEmpty()) {
            int resID = getResources().getIdentifier(lastButton, "id", getPackageName());
            ImageButton button = ((ImageButton) findViewById(resID));
            sendFindSmsButton.setBackgroundResource(R.drawable.green_and_white_border_button);
            sendStatusSmsButton.setBackgroundResource(R.drawable.green_and_white_border_button);
            sendOnSmsButton.setBackgroundResource(R.drawable.green_and_white_border_button);
            sendOffSmsButton.setBackgroundResource(R.drawable.green_and_white_border_button);
//            sendStartSmsButton.setBackgroundResource(R.drawable.green_and_white_border_button);
            sendVibrationSensorOnSmsButton.setBackgroundResource(R.drawable.green_and_white_border_button);
            sendVibrationSensorOffSmsButton.setBackgroundResource(R.drawable.green_and_white_border_button);
//            lockCallButton.setBackgroundResource(R.drawable.green_and_white_border_button);
//            unlockCallButton.setBackgroundResource(R.drawable.green_and_white_border_button);
            button.setBackgroundResource(R.drawable.red_and_black_border_button);

        }
    }

    public void storeLastButtonId(){
        if (lastButtonId != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("LastButton", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lastButton", lastButtonId);
            editor.apply();
        }
    }

    public void messageHandleMethod() {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        sentBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Failed to send",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "Failed to send",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Failed to send",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Failed to send",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        registerReceiver(sentBroadcastReceiver, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        deliveredBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        registerReceiver(deliveredBroadcastReceiver, new IntentFilter(DELIVERED));
    }

    public void vibrateCreation() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

    public static boolean hasPermission(Context context, String... permissions) {

        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Back Button Pressed");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_warning);
        builder.setMessage("Do you want to exit?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (sentBroadcastReceiver != null) {
            unregisterReceiver(sentBroadcastReceiver);
        }
        if (deliveredBroadcastReceiver != null) {
            unregisterReceiver(deliveredBroadcastReceiver);
        }
        sentPI = null;
        deliveredPI = null;
        super.onDestroy();
    }
}

