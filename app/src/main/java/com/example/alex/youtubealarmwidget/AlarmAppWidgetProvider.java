package com.example.alex.youtubealarmwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RemoteViews;


/**
 * Created by Alex on 7/10/2017.
 */

public class AlarmAppWidgetProvider extends AppWidgetProvider {

    // log tag
    private static final String TAG = "ExampleAppWidgetProvide";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        // For each widget that needs an update, get the text that we should display:
        //   - Create a RemoteViews object for it
        //   - Set the text in the RemoteViews object
        //   - Tell the AppWidgetManager to show that views object for the widget.
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {


            int appWidgetId = appWidgetIds[i];
            Log.d(TAG, "updatating app widget " + appWidgetId);
            String titlePrefix = AlarmAppWidgetConfigure.loadTitlePref(context, appWidgetId);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            views.setImageViewBitmap(R.id.appwidget_image1, convertToImg("2312", context));
            views.setImageViewBitmap(R.id.appwidget_image2, convertToImg("52", context));
            views.setImageViewBitmap(R.id.appwidget_image3, convertToImg("6", context));
            views.setImageViewBitmap(R.id.appwidget_image4, convertToImg("34", context));
            appWidgetManager.updateAppWidget(appWidgetId, views);
            //      updateAppWidget(context, appWidgetManager, appWidgetId, titlePrefix);




        }



        // the layout from our package).


    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, "onDeleted");
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            AlarmAppWidgetConfigure.deleteTitlePref(context, appWidgetIds[i]);
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

      //  cnvText.drawCircle(125, 125, 125, paint);


        cnvText.drawText(text, 250, 210, paint);
        return btmText;


    }

}