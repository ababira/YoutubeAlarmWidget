package com.example.alex.youtubealarmwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by Alex on 7/10/2017.
 */



/*
* TODO
* The answers above are good -
* but don't consider the user's potential to restart the device (which clears PendingIntent's scheduled by AlarmManager).
You need to create a WakefulBroadcastReceiver, which will contain an
AlarmManager to schedule deliver a PendingIntent.
When the WakefulBroadcastReceiver handles the intent -
post your notification and signal the WakefulBroadcastReceiver to complete.
* */

public class AlarmBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ExmampleBroadcastReceiv", "intent=" + intent);
        Log.e("ExmampleBroadcastReceiv", "intent=" + intent);


        PowerManager pm = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakeLock.acquire();

        Intent i  = new Intent(context, MainActivity.class);

        context.startActivity(i);

        wakeLock.release();


    }

}
