package com.rashed.md.gpssecurity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiverClass extends BroadcastReceiver {


    String smsServerAdress,smsBodyFromServer;
    String phoneNumberWithCountryCode;
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(SMS_RECEIVED)){
            Bundle pudsBundle=intent.getExtras();
            Object[] pdus= (Object[]) pudsBundle.get("pdus");
            SmsMessage smsMessage=SmsMessage.createFromPdu((byte[]) pdus[0]);
            smsServerAdress=smsMessage.getOriginatingAddress();
            smsBodyFromServer=smsMessage.getMessageBody();



            String phoneNumber=MainActivity.phoneNumber;
            phoneNumberWithCountryCode="+88"+phoneNumber;
            if (phoneNumberWithCountryCode.contains(smsServerAdress)){
                if (smsBodyFromServer.contains("http://www.google.com/maps")){
                    Intent intentForGoogleMap=new Intent(context,GoogleMapViewClass.class);
                    intentForGoogleMap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentForGoogleMap.putExtra("message",smsBodyFromServer);
                    context.startActivity(intentForGoogleMap);
                }
                else {
                    Intent smsActivityIntent=new Intent(context,SmsShowActivity.class);
                    smsActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    smsActivityIntent.putExtra("message",smsBodyFromServer);
                    context.startActivity(smsActivityIntent);
                }
            }
        }



    }

}
