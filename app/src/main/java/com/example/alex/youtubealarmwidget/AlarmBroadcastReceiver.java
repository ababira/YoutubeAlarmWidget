package com.example.alex.youtubealarmwidget;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

    NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ExmampleBroadcastReceiv", "intent=" + intent);
        Log.e("ExmampleBroadcastReceiv", "intent=" + intent);





     /*   String state = intent.getExtras().getString("extra");
        Log.e("MyActivity", "In the receiver with " + state);

        Intent serviceIntent = new Intent(context,RingtonePlayingService.class);
        serviceIntent.putExtra("extra", state);

        context.startService(serviceIntent);*/


//        MediaPlayer mp = MediaPlayer.create(context, R.raw.ting);
//
//        mp.start();


        // For our example, we'll also update all of the widgets when the timezone
        // changes, or the user or network sets the time.
        //  String action = intent.getAction();
        //    if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)
        //         || action.equals(Intent.ACTION_TIME_CHANGED)) {
      /*      AppWidgetManager gm = AppWidgetManager.getInstance(context);
            ArrayList<Integer> appWidgetIds = new ArrayList<Integer>();
            ArrayList<String> texts = new ArrayList<String>();

           // AlarmAppWidgetConfigure.loadAllTitlePrefs(context, appWidgetIds, texts);
            final int N = appWidgetIds.size();
            for (int i=0; i<N; i++) {
                //TODO uncomment this to examine the functionallity
                AlarmAppWidgetProvider.updateAppWidget(context, gm, appWidgetIds.get(i), texts.get(i));
            }*/
        //  }


    }

}
