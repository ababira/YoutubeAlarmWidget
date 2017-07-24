package com.example.alex.youtubealarmwidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Alex on 7/10/2017.
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ExmampleBroadcastReceiv", "intent=" + intent);

        // For our example, we'll also update all of the widgets when the timezone
        // changes, or the user or network sets the time.
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                || action.equals(Intent.ACTION_TIME_CHANGED)) {
            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            ArrayList<Integer> appWidgetIds = new ArrayList<Integer>();
            ArrayList<String> texts = new ArrayList<String>();

            AlarmAppWidgetConfigure.loadAllTitlePrefs(context, appWidgetIds, texts);

            final int N = appWidgetIds.size();
            for (int i=0; i<N; i++) {
                //TODO uncomment this to examine the functionallity
               // AlarmAppWidgetProvider.updateAppWidget(context, gm, appWidgetIds.get(i), texts.get(i));
            }
        }
    }

}
