package com.example.alex.youtubealarmwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;



public class AlarmAppWidgetProvider extends AppWidgetProvider {




   /*TODO:
    A note about PendingIntent.getBroadcast(context, 0, intent, 0): This will cause a bug if you have more
    than one instance of widgets. Problem: only last widget gets updated whenever you click any instance of widget.
    Solution: pass widgetId to getPendingSelfIntent and try this: PendingIntent.getBroadcast(context, widgetId, intent, 0
    */


    // log tag
    private static final String TAG = "ExampleAppWidgetProvide";


    public static String UP_ACTION_MINS = "UpActionMins";
    public static String DOWN_ACTION_MINS = "DownActionMins";

    public static String UP_ACTION_HRS = "UpActionHrs";
    public static String DOWN_ACTION_HRS = "DownActionHrs";


    //TODO  REMOVE THESE VARIABLES TO USE INTENTS
    private static int _MinsCounter = 5;
    private static int _HrsCounter = 1;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");


        // For each widget that needs an update, get the text that we should display:
        //   - Create a RemoteViews object for it
        //   - Set the text in the RemoteViews object
        //   - Tell the AppWidgetManager to show that views object for the widget.
        final int N = appWidgetIds.length;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        for (int appWidgetId : appWidgetIds) {


            Log.d(TAG, "updatating app widget " + appWidgetId);
            String titlePrefix = AlarmAppWidgetConfigure.loadTitlePref(context, appWidgetId);

            views.setImageViewBitmap(R.id.appwidget_image1, convertToImg("2312", context));
            views.setImageViewBitmap(R.id.appwidget_image2, convertToImg("52", context));
            views.setImageViewBitmap(R.id.appwidget_image3, convertToImg(String.valueOf(_MinsCounter), context));
            views.setImageViewBitmap(R.id.appwidget_image4, convertToImg("34", context));


            //TODO Try and add these to the onEnabled method
            PendingIntent intentUpActionMins = getPendingSelfIntent(context, UP_ACTION_MINS);
            views.setOnClickPendingIntent(R.id.appwidget_text1, intentUpActionMins);


            PendingIntent intentDownActionMins = getPendingSelfIntent(context, DOWN_ACTION_MINS);
            views.setOnClickPendingIntent(R.id.appwidget_text2, intentDownActionMins);


            PendingIntent intentDownActionHrs = getPendingSelfIntent(context, DOWN_ACTION_HRS);
            views.setOnClickPendingIntent(R.id.appwidget_text4, intentDownActionHrs);


            PendingIntent intentUpActionHrs = getPendingSelfIntent(context, UP_ACTION_HRS);
            views.setOnClickPendingIntent(R.id.appwidget_text3, intentUpActionHrs);


        }
        appWidgetManager.updateAppWidget(new ComponentName(context, AlarmAppWidgetProvider.class), views);


        // the layout from our package).


    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, "onDeleted");
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int appWidgetId : appWidgetIds) {
            AlarmAppWidgetConfigure.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {

        Log.d(TAG, "onEnabled");


        // When the first widget is created, register for the TIMEZONE_CHANGED and TIME_CHANGED
        // broadcasts.  We don't want to be listening for these if nobody has our widget active.
        // This setting is sticky across reboots, but that doesn't matter, because this will
        // be called after boot if there is a widget instance for this provider.
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(context.getApplicationContext(), AlarmBroadcastReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


    }

    @Override
    public void onDisabled(Context context) {
        // When the first widget is created, stop listening for the TIMEZONE_CHANGED and
        // TIME_CHANGED broadcasts.
        Log.d(TAG, "onDisabled");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(context.getApplicationContext(), AlarmBroadcastReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String titlePrefix) {
        Log.d(TAG, "updateAppWidget appWidgetId=" + appWidgetId + " titlePrefix=" + titlePrefix);
        // Getting the string this way allows the string to be localized.  The format
        // string is filled in using java.util.Formatter-style format strings.
        CharSequence text = context.getString(R.string.appwidget_text_format,
                AlarmAppWidgetConfigure.loadTitlePref(context, appWidgetId),
                "0x" + Long.toHexString(SystemClock.elapsedRealtime()));

        // Construct the RemoteViews object.  It takes the package name (in our case, it's our
        // package, but it needs this because on the other side it's the widget host inflating
        // the layout from our package).
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        // TODO uncooment this to add functionality
        //views.setTextViewText(R.id.appwidget_text1, text);

        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private Bitmap convertToImg(String text, Context context) {
        Bitmap btmText = Bitmap.createBitmap(500, 250, Bitmap.Config.ARGB_4444);
        Canvas cnvText = new Canvas(btmText);

        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/digital-7.ttf");

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(tf);
        paint.setARGB(255, 245, 196, 135);
        paint.setTextSize(250);

        paint.setTextAlign(Paint.Align.CENTER);

        cnvText.drawText(text, 250, 210, paint);
        return btmText;


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        if (UP_ACTION_MINS.equals(intent.getAction())) {
            _MinsCounter++;
            views.setImageViewBitmap(R.id.appwidget_image3, convertToImg(String.valueOf(_MinsCounter), context));
        }
        if (DOWN_ACTION_MINS.equals(intent.getAction())) {
            _MinsCounter--;
            views.setImageViewBitmap(R.id.appwidget_image3, convertToImg(String.valueOf(_MinsCounter), context));
        }
        if (UP_ACTION_HRS.equals(intent.getAction())) {
            _HrsCounter++;
            views.setImageViewBitmap(R.id.appwidget_image4, convertToImg(String.valueOf(_HrsCounter), context));
        }
        if (DOWN_ACTION_HRS.equals(intent.getAction())) {
            _HrsCounter--;
            views.setImageViewBitmap(R.id.appwidget_image4, convertToImg(String.valueOf(_HrsCounter), context));
        }
        appWidgetManager.updateAppWidget(new ComponentName(context, AlarmAppWidgetProvider.class), views);
    }

    

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


}