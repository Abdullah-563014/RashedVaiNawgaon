package com.rashed.md.gpssecurity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rashed.md.gpssecurity.interfaces.SmsDeliveredConfirmedInterfaces;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener,SmsDeliveredConfirmedInterfaces {

    private EditText alertDialogSpeedEditText,
            alertDialogApnNameEditText,
            alertDialogMoveAlarmEditText,
            alertDialogExtraAdminEditText,
            alertDialogRemoveExtraAdminEditText;
    private Button alertDialogSetSpeedButton,
            setSpeedAlarmButton,
            apnSettingButton,
            setMoveAlarmButton,
            powerCutAlarmOnButton,
            powerCutAlarmOffButton,
            setExtraAdminButton,
            removeExtraAdminButton,
            formatDeviceButton,
            alertDialogSetApnNameButton,
            alertDialogMoveAlarmButton,
            alertDialogExtraAdminButton,
            alertDialogRemoveExtraAdminButton;
    private TextView alertDialogCurrentSpeedTextView,
            powerCutStatusTextView,
            alertDialogApnStatusTextView,
            alertDialogMoveAlarmTextView,
            alertDialogExtraAdminTextView,
            alertDialogRemoveExtraAdminStatusTextView;
    private String alertDialogInputSpeed,
            dialogType,
            alertDialogInputApnName,
            alertDialogMoveAlarmInputSpeed,
            alertDialogExtraAdminInputValue,
            alertDialogRemoveExtraAdminInputValue;
    private Toolbar toolbar;
    private AlertDialog alertDialog;
    private LinearLayout rootLayout;
    private PendingIntent sentPI, deliveredPI;
    private BroadcastReceiver sentBroadcastReceiver, deliveredBroadcastReceiver;
    private TextToSpeech textToSpeech;

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

        startBackgroundAnimation();

        initTextToSpeech();

        getPowerCutStatusFromStorage();

    }


    private void initAll() {
        setSpeedAlarmButton=findViewById(R.id.settingActivitySetSpeedAlarmButtonId);
        apnSettingButton=findViewById(R.id.settingActivityApnSettingButtonId);
        setMoveAlarmButton=findViewById(R.id.settingActivityMoveAlarmButtonId);
        powerCutAlarmOnButton=findViewById(R.id.settingActivityPowerCutAlarmOnButtonId);
        powerCutAlarmOffButton=findViewById(R.id.settingActivityPowerCutAlarmOffButtonId);
        setExtraAdminButton=findViewById(R.id.settingActivitySaveExtraAdminButtonId);
        formatDeviceButton=findViewById(R.id.settingActivityDeviceFormatButtonId);
        powerCutStatusTextView=findViewById(R.id.settingsActivityPowerCutAlarmStatusTextViewId);
        removeExtraAdminButton=findViewById(R.id.settingActivityRemoveExtraAdminButtonId);
        rootLayout=findViewById(R.id.settingsActivityRootLayoutId);

        setSpeedAlarmButton.setOnClickListener(this);
        apnSettingButton.setOnClickListener(this);
        setMoveAlarmButton.setOnClickListener(this);
        powerCutAlarmOnButton.setOnClickListener(this);
        powerCutAlarmOffButton.setOnClickListener(this);
        setExtraAdminButton.setOnClickListener(this);
        formatDeviceButton.setOnClickListener(this);
        removeExtraAdminButton.setOnClickListener(this);
    }

    private void getPowerCutStatusFromStorage(){
        String powerCutStatus=Utils.getStringFromStorage(getApplicationContext(),"PowerCutAlarmOn");
        if (powerCutStatus==null){
            powerCutStatus="off";
        }
        powerCutStatusTextView.setText("Current power cut status is:- "+powerCutStatus);
    }

    private void startBackgroundAnimation(){
        AnimationDrawable animationDrawable = (AnimationDrawable) rootLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1500);
        animationDrawable.setExitFadeDuration(3500);
        animationDrawable.start();
    }

    private void sendSms(String smsCommand){
        try {
            if (Utils.devicePhoneNumber != null) {
                double num = Double.parseDouble(Utils.devicePhoneNumber);
            } else {
                Toast.makeText(this, "Please set your device phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessageHandle(smsCommand);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please set your device phone number", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessageHandle(String command){
        try {
            messageHandleMethod();
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Utils.devicePhoneNumber, null, command, sentPI, deliveredPI);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }

    private void messageHandleMethod() {
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
                        smsDelivered();
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

    private void showAlertDialog(){
        View view=null;
        switch (dialogType){
            case "SpeedAlarm":
                view=getLayoutInflater().inflate(R.layout.custom_layout_for_alarm_speed,null,false);
                alertDialogSetSpeedButton=view.findViewById(R.id.alertDialogSetSpeedAlarmButtonId);
                alertDialogSpeedEditText=view.findViewById(R.id.alertDialogSetSpeedAlarmEditTextId);
                alertDialogCurrentSpeedTextView=view.findViewById(R.id.alertDialogCurrentSpeedTextViewId);
                String currentSpeed=Utils.getStringFromStorage(getApplicationContext(),"CurrentAlarmedSpeed");
                alertDialogCurrentSpeedTextView.setText("Current speed alarm:- "+currentSpeed+" Km/h");
                alertDialogSetSpeedButton.setOnClickListener(this);
                break;

            case "ApnName":
                view=getLayoutInflater().inflate(R.layout.custom_layout_for_apn_setting,null,false);
                alertDialogSetApnNameButton=view.findViewById(R.id.alertDialogSetApnNameButtonId);
                alertDialogApnNameEditText=view.findViewById(R.id.alertDialogSetApnNameEditTextId);
                alertDialogApnStatusTextView=view.findViewById(R.id.alertDialogCurrentApnNameTextViewId);
                String oldApnName=Utils.getStringFromStorage(getApplicationContext(),"CurrentApnName");
                alertDialogApnStatusTextView.setText("Current APN name:- "+oldApnName);
                alertDialogSetApnNameButton.setOnClickListener(this);
                break;

            case "MoveAlarm":
                view=getLayoutInflater().inflate(R.layout.custom_layout_for_move_alarm,null,false);
                alertDialogMoveAlarmButton=view.findViewById(R.id.alertDialogSetMoveAlarmButtonId);
                alertDialogMoveAlarmEditText=view.findViewById(R.id.alertDialogSetMoveAlarmEditTextId);
                alertDialogMoveAlarmTextView=view.findViewById(R.id.alertDialogMoveAlarmTextViewId);
                String oldMoveAlarm=Utils.getStringFromStorage(getApplicationContext(),"CurrentMoveAlarm");
                alertDialogMoveAlarmTextView.setText("Current Move Speed Alarm:- "+oldMoveAlarm);
                alertDialogMoveAlarmButton.setOnClickListener(this);
                break;

            case "SetExtraAdmin":
                view=getLayoutInflater().inflate(R.layout.custom_layout_for_extra_admin,null,false);
                alertDialogExtraAdminButton=view.findViewById(R.id.alertDialogSetExtraAdminButtonId);
                alertDialogExtraAdminEditText=view.findViewById(R.id.alertDialogSetExtraAdminEditTextId);
                alertDialogExtraAdminTextView=view.findViewById(R.id.alertDialogExtraAdminTextViewId);
                String oldExtraAdmin=Utils.getStringFromStorage(getApplicationContext(),"CurrentSetExtraAdmin");
                alertDialogExtraAdminTextView.setText("Current Extra Admin:- "+oldExtraAdmin);
                alertDialogExtraAdminButton.setOnClickListener(this);
                break;

            case "RemoveExtraAdmin":
                view=getLayoutInflater().inflate(R.layout.custom_layout_for_remove_extra_admin,null,false);
                alertDialogRemoveExtraAdminButton=view.findViewById(R.id.alertDialogRemoveExtraAdminButtonId);
                alertDialogRemoveExtraAdminEditText=view.findViewById(R.id.alertDialogRemoveExtraAdminEditTextId);
                alertDialogRemoveExtraAdminStatusTextView=view.findViewById(R.id.alertDialogRemoveExtraAdminTextViewId);
                String existedAdmin=Utils.getStringFromStorage(getApplicationContext(),"CurrentSetExtraAdmin");
                alertDialogRemoveExtraAdminStatusTextView.setText("Current Extra Admin:- "+existedAdmin);
                alertDialogRemoveExtraAdminButton.setOnClickListener(this);
                break;
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(SettingsActivity.this);
        builder.setCancelable(true);
        builder.setView(view);
        alertDialog=builder.create();
        if (alertDialog.getWindow()!=null){
            alertDialog.getWindow().getAttributes().windowAnimations=R.style.DialogTheme;
        }
        if (!isFinishing()){
            alertDialog.show();
        }
    }

    private void vibrateCreation() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

    private void voiceCommand(String command){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(command, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(command, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void initTextToSpeech(){
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(SettingsActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
                    } else {
                        textToSpeech.setSpeechRate(0.7f);
                    }
                } else {
                    Toast.makeText(SettingsActivity.this, "Initialization Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.settingActivitySetSpeedAlarmButtonId:
                vibrateCreation();
                dialogType="SpeedAlarm";
                showAlertDialog();
                break;

            case R.id.alertDialogSetSpeedAlarmButtonId:
                vibrateCreation();
                alertDialogInputSpeed=alertDialogSpeedEditText.getText().toString();
                if (!TextUtils.isEmpty(alertDialogInputSpeed)){
                    sendSms("speed123456 "+alertDialogInputSpeed);
                    alertDialog.dismiss();
                }else {
                    alertDialogSpeedEditText.setError("Please input valid speed.");
                    alertDialogSpeedEditText.setFocusable(true);
                }
                break;

            case R.id.settingActivityApnSettingButtonId:
                vibrateCreation();
                dialogType="ApnName";
                showAlertDialog();
                break;

            case R.id.alertDialogSetApnNameButtonId:
                vibrateCreation();
                alertDialogInputApnName=alertDialogApnNameEditText.getText().toString();
                if (!TextUtils.isEmpty(alertDialogInputApnName)){
                    sendSms("apn123456 "+alertDialogInputApnName);
                    alertDialog.dismiss();
                }else {
                    alertDialogApnNameEditText.setError("Please input valid apn name.");
                    alertDialogApnNameEditText.setFocusable(true);
                }
                break;

            case R.id.settingActivityMoveAlarmButtonId:
                vibrateCreation();
                dialogType="MoveAlarm";
                showAlertDialog();
                break;

            case R.id.alertDialogSetMoveAlarmButtonId:
                vibrateCreation();
                alertDialogMoveAlarmInputSpeed=alertDialogMoveAlarmEditText.getText().toString();
                if (!TextUtils.isEmpty(alertDialogMoveAlarmInputSpeed)){
                    sendSms("move123456 "+alertDialogMoveAlarmInputSpeed);
                    alertDialog.dismiss();
                }else {
                    alertDialogMoveAlarmEditText.setError("Please input valid move speed.");
                    alertDialogMoveAlarmEditText.setFocusable(true);
                }
                break;

            case R.id.settingActivityPowerCutAlarmOnButtonId:
                vibrateCreation();
                dialogType="PowerCutAlarmOn";
                sendSms("pwrcall123456,1");
                break;

            case R.id.settingActivityPowerCutAlarmOffButtonId:
                vibrateCreation();
                dialogType="PowerCutAlarmOff";
                sendSms("pwrcall123456,0");
                break;

            case R.id.settingActivitySaveExtraAdminButtonId:
                vibrateCreation();
                dialogType="SetExtraAdmin";
                showAlertDialog();
                break;

            case R.id.alertDialogSetExtraAdminButtonId:
                vibrateCreation();
                alertDialogExtraAdminInputValue=alertDialogExtraAdminEditText.getText().toString();
                if (!TextUtils.isEmpty(alertDialogExtraAdminInputValue)){
                    sendSms("admin123456 "+alertDialogExtraAdminInputValue);
                    alertDialog.dismiss();
                }else {
                    alertDialogExtraAdminEditText.setError("Please input admin's valid phone number.");
                    alertDialogExtraAdminEditText.setFocusable(true);
                }
                break;

            case R.id.settingActivityRemoveExtraAdminButtonId:
                vibrateCreation();
                dialogType="RemoveExtraAdmin";
                showAlertDialog();
                break;

            case R.id.alertDialogRemoveExtraAdminButtonId:
                vibrateCreation();
                alertDialogRemoveExtraAdminInputValue=alertDialogRemoveExtraAdminEditText.getText().toString();
                if (!TextUtils.isEmpty(alertDialogRemoveExtraAdminInputValue)){
                    sendSms("noadmin123456 "+alertDialogRemoveExtraAdminInputValue);
                    alertDialog.dismiss();
                }else {
                    alertDialogRemoveExtraAdminEditText.setError("Please input admin's valid phone number.");
                    alertDialogRemoveExtraAdminEditText.setFocusable(true);
                }
                break;

            case R.id.settingActivityDeviceFormatButtonId:
                vibrateCreation();
                dialogType="FormatDevice";
                sendSms("FORMAT");
                break;
        }
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

    @Override
    public void smsDelivered() {
        switch (dialogType){
            case "SpeedAlarm":
                voiceCommand("Setting activated successfully");
                Utils.setStringToStorage(getApplicationContext(),"CurrentAlarmedSpeed",alertDialogInputSpeed);
                break;

            case "ApnName":
                voiceCommand("Setting activated successfully");
                Utils.setStringToStorage(getApplicationContext(),"CurrentApnName",alertDialogInputApnName);
                break;

            case "MoveAlarm":
                voiceCommand("Setting activated successfully");
                Utils.setStringToStorage(getApplicationContext(),"CurrentMoveAlarm",alertDialogMoveAlarmInputSpeed);
                break;

            case "PowerCutAlarmOn":
                voiceCommand("Setting activated successfully");
                Utils.setStringToStorage(getApplicationContext(),"CurrentPowerCutStatus","on");
                powerCutStatusTextView.setText("Current power cut status is:- "+Utils.getStringFromStorage(getApplicationContext(),"CurrentPowerCutStatus"));
                break;

            case "PowerCutAlarmOff":
                voiceCommand("Setting activated successfully");
                Utils.setStringToStorage(getApplicationContext(),"CurrentPowerCutStatus","off");
                powerCutStatusTextView.setText("Current power cut status is:- "+Utils.getStringFromStorage(getApplicationContext(),"CurrentPowerCutStatus"));
                break;

            case "SetExtraAdmin":
                voiceCommand("Setting activated successfully");
                Utils.setStringToStorage(getApplicationContext(),"CurrentSetExtraAdmin",alertDialogExtraAdminInputValue);
                break;

            case "RemoveExtraAdmin":
                voiceCommand("Extra admin removed successfully");
                Utils.setStringToStorage(getApplicationContext(),"CurrentSetExtraAdmin"," ");
                break;

            case "FormatDevice":
                voiceCommand("Setting formatted successfully");
                break;
        }
    }
}
