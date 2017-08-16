package com.example.alex.youtubealarmwidget;

import android.app.AlarmManager;
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
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AlarmAppWidgetProvider extends AppWidgetProvider {


    /*TODO: Handle the alarms when the time zone changes

     */


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


    public static String ADD_MINS_TO_ALARM = "AddMinsToAlarm";
    public static String ADD_HRS_TO_ALARM = "AddHoursToAlarm";

    public static String ROUND_TO_10_ALARM = "RoundTo10Alarm";
    public static String ROUND_TO_30_ALARM = "RoundTo30Alarm";
    public static String ROUND_TO_60_ALARM = "RoundTo60Alarm";

    public static String CANCEL_ALARM = "CancelAlarm";


    private static final String CLOCK_FORMAT = "HH:mm";


    private static String DEFAULT_ALARM_STRING = "--:--";

    private static Calendar _CurrentAlarmTime;

    //TODO: take this initialization out
    private static boolean _IsAlarmSet = false;


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

            views.setImageViewBitmap(R.id.appwidget_image1, convertToImg(DEFAULT_ALARM_STRING, context));
            views.setTextViewText(R.id.appwidget_text_minutes, String.valueOf(_MinsCounter));
            views.setTextViewText(R.id.appwidget_text_hours, String.valueOf(_HrsCounter));


            //TODO Try and add these to the onEnabled method
            PendingIntent intentUpActionMins = getPendingSelfIntent(context, UP_ACTION_MINS);
            views.setOnClickPendingIntent(R.id.appwidget_text1, intentUpActionMins);


            PendingIntent intentDownActionMins = getPendingSelfIntent(context, DOWN_ACTION_MINS);
            views.setOnClickPendingIntent(R.id.appwidget_text2, intentDownActionMins);


            PendingIntent intentDownActionHrs = getPendingSelfIntent(context, DOWN_ACTION_HRS);
            views.setOnClickPendingIntent(R.id.appwidget_text4, intentDownActionHrs);


            PendingIntent intentUpActionHrs = getPendingSelfIntent(context, UP_ACTION_HRS);
            views.setOnClickPendingIntent(R.id.appwidget_text3, intentUpActionHrs);


            PendingIntent incrementAlarmByMins = getPendingSelfIntent(context, ADD_MINS_TO_ALARM);
            views.setOnClickPendingIntent(R.id.appwidget_text_minutes, incrementAlarmByMins);

            PendingIntent incrementAlarmByHours = getPendingSelfIntent(context, ADD_HRS_TO_ALARM);
            views.setOnClickPendingIntent(R.id.appwidget_text_hours, incrementAlarmByHours);

            PendingIntent intentCancelAlarm = getPendingSelfIntent(context, CANCEL_ALARM);
            views.setOnClickPendingIntent(R.id.appwidget_text_cancel, intentCancelAlarm);

            PendingIntent intentRoundTo10Alarm = getPendingSelfIntent(context, ROUND_TO_10_ALARM);
            views.setOnClickPendingIntent(R.id.button_round_10, intentRoundTo10Alarm);

            PendingIntent intentRoundTo30Alarm = getPendingSelfIntent(context, ROUND_TO_30_ALARM);
            views.setOnClickPendingIntent(R.id.button_round_30, intentRoundTo30Alarm);

            PendingIntent intentRoundTo60Alarm = getPendingSelfIntent(context, ROUND_TO_60_ALARM);
            views.setOnClickPendingIntent(R.id.button_round_60, intentRoundTo60Alarm);


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
        views.setTextViewText(R.id.appwidget_text_cancel, "Just Ran Alarm");

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
            views.setTextViewText(R.id.appwidget_text_minutes, String.valueOf(_MinsCounter));
        }
        if (DOWN_ACTION_MINS.equals(intent.getAction())) {
            _MinsCounter--;
            views.setTextViewText(R.id.appwidget_text_minutes, String.valueOf(_MinsCounter));
        }
        if (UP_ACTION_HRS.equals(intent.getAction())) {
            _HrsCounter++;
            views.setTextViewText(R.id.appwidget_text_hours, String.valueOf(_HrsCounter));
        }
        if (DOWN_ACTION_HRS.equals(intent.getAction())) {
            _HrsCounter--;
            views.setTextViewText(R.id.appwidget_text_hours, String.valueOf(_HrsCounter));
        }
        if (ADD_MINS_TO_ALARM.equals(intent.getAction())) {

            if (!_IsAlarmSet) {
                _CurrentAlarmTime = Calendar.getInstance();
                _CurrentAlarmTime.set(Calendar.SECOND, 0);
                _IsAlarmSet = true;
            }

            views.setImageViewBitmap(R.id.appwidget_image1, convertToImg(addMinutesToCurrentTime(_MinsCounter), context));
            setAlarm(context);
        }
        if (ADD_HRS_TO_ALARM.equals(intent.getAction())) {

            if (!_IsAlarmSet) {
                _CurrentAlarmTime = Calendar.getInstance();
                _IsAlarmSet = true;
            }

            views.setImageViewBitmap(R.id.appwidget_image1, convertToImg(addHoursToCurrentTime(_HrsCounter), context));
        }
        if (ROUND_TO_10_ALARM.equals(intent.getAction())) {

            if (!_IsAlarmSet) {
                _CurrentAlarmTime = Calendar.getInstance();
                _IsAlarmSet = true;
            }

            views.setImageViewBitmap(R.id.appwidget_image1, convertToImg(roundCurrentAlarmTimeToNearest(10), context));
        }
        if (ROUND_TO_30_ALARM.equals(intent.getAction())) {

            if (!_IsAlarmSet) {
                _CurrentAlarmTime = Calendar.getInstance();
                _IsAlarmSet = true;
            }

            views.setImageViewBitmap(R.id.appwidget_image1, convertToImg(roundCurrentAlarmTimeToNearest(30), context));
        }
        if (ROUND_TO_60_ALARM.equals(intent.getAction())) {

            if (!_IsAlarmSet) {
                _CurrentAlarmTime = Calendar.getInstance();
                _IsAlarmSet = true;
            }

            views.setImageViewBitmap(R.id.appwidget_image1, convertToImg(roundCurrentAlarmTimeToNearest(60), context));
        }

        if (CANCEL_ALARM.equals(intent.getAction())) {

            if (_IsAlarmSet) {
              cancelAlarm(context);
            }


            views.setImageViewBitmap(R.id.appwidget_image1, convertToImg(DEFAULT_ALARM_STRING, context));

        }
        appWidgetManager.updateAppWidget(new ComponentName(context, AlarmAppWidgetProvider.class), views);
    }


    private void setAlarm(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //TODO: test with SDK v 19
        if (Build.VERSION.SDK_INT >= 21) {
            //TODO set the pending intent here to use the configration class;
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(_CurrentAlarmTime.getTimeInMillis(), PendingIntent.getActivity(context, 1, new Intent(context, MainActivity.class), 0));
            alarmManager.setAlarmClock(alarmClockInfo, getAlarmPendingIntent(context));
            return;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, _CurrentAlarmTime.getTimeInMillis(), getAlarmPendingIntent(context));
            return;
        }

    }

    private void cancelAlarm(Context context){
        _CurrentAlarmTime = null;
        _IsAlarmSet = false;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getAlarmPendingIntent(context));
    }


    private PendingIntent getAlarmPendingIntent(Context context) {
       //TODO: check if application is running on the foreground in case any other app is open if other alarm goes off
        Intent myIntent = new Intent(context, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // You need this if starting
        return PendingIntent.getActivity(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


    private String addMinutesToCurrentTime(int minutes) {

        _CurrentAlarmTime.add(Calendar.MINUTE, minutes);
        SimpleDateFormat sdf = new SimpleDateFormat(CLOCK_FORMAT);
        return sdf.format(_CurrentAlarmTime.getTime());
    }

    private String addHoursToCurrentTime(int hours) {
        _CurrentAlarmTime.add(Calendar.MINUTE, hours * 60);
        SimpleDateFormat sdf = new SimpleDateFormat(CLOCK_FORMAT);
        return sdf.format(_CurrentAlarmTime.getTime());
    }


    private String roundCurrentAlarmTimeToNearest(int x) {

        //TODO comment for users
        /*
            Rounds to the next "x" or to the closest "x"
         */

        int minutesToAdd = x - _CurrentAlarmTime.get(Calendar.MINUTE) % x;


        _CurrentAlarmTime.add(Calendar.MINUTE, minutesToAdd);

        SimpleDateFormat sdf = new SimpleDateFormat(CLOCK_FORMAT);
        return sdf.format(_CurrentAlarmTime.getTime());

    }

}