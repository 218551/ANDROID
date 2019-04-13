package com.example.student.laba5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
    private static final String SMS_REC_ACTION="android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getAction().equals(SmsReceiver.SMS_REC_ACTION)){
            StringBuilder sb = new StringBuilder();
            String sender=null;
            Bundle bundle = intent.getExtras();
            if(bundle!=null){
                Object[] pdus = (Object[])
                    bundle.get("pdus");
                for(Object pdu:pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    sb.append( smsMessage.getDisplayMessageBody());
                    sender = smsMessage.getOriginatingAddress();
                }
            }
            Toast.makeText(context, "SMS_Received - " + sb.toString() + "From: " +sender ,Toast.LENGTH_LONG).show();
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(sender ,null,sb.toString() + ". Potwierdzam",null, null);
            Toast.makeText(context, "SMS_Reply - " + sb.toString() + "To: " + sender ,Toast.LENGTH_LONG).show();
        }
    }

}