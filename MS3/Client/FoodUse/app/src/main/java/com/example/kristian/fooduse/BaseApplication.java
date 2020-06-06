package com.example.kristian.fooduse;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import de.hdodenhof.circleimageview.CircleImageView;

/*
Die BaseApplikation stellt im Grunde genommen unsere App dar. Sobald die App gelauncht wird, wird diese Anwendung ausgeführt, wodurch zum einem der
JobScheduler für die Notification erstellt wird und zum anderen wird der Channel für die Notifications erstellt. Die Applikation wurde mit in das Manifest
aufgenommen und wird immer aufgerufen, um Code verdoppelungen zu vermeiden.
 */
public class BaseApplication extends Application {

    public static final String CHANNEL_1_ID = "CHANNEL1";
    @Override
    public void onCreate() {
        super.onCreate(

        );
        createNotificationChannels();
    }

    public void lebensmittelInformationen_einfügen() {
        LebensmittelDBHelper dbHelper = new LebensmittelDBHelper(this);
        SQLiteDatabase readdb = dbHelper.getReadableDatabase();

        String suche = "Kirsche";
        Cursor cursor = readdb.rawQuery("select * from " + LebensmittelContract.LebensmittelInfo.TABLE_NAME + " WHERE name = " + "'" + suche + "'", null);
        if (cursor.moveToFirst()) {
            int name = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelInfo.COLUMN_GRUNDHALTBARKEIT));
            Log.d("Kalligrafie", "Die Orange hat eine Grundhaltbarkeit von: " + name);
        } else {
            Log.d("Kalligrafie", "Es konnte leider nichts gefunden werden :(");
        }
    }

    private void createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID, "channel1", importance);
            channel.setDescription("Dieser Channel ist für ablaufende Lebensmittel");
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (!prefs.getBoolean("firstTime", false)) {
                scheduleJob();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstTime", true);
                editor.commit();

            }

        }
    }
    public void scheduleJob(){
        ComponentName componentName = new ComponentName(this, LebensmittelJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setPersisted(true)
                .setPeriodic(24* 60 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);

    }
}
