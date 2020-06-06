package com.example.kristian.fooduse;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LebensmittelJobService extends JobService {

    private static final String TAG = "JobService";
    private boolean jobCancelled = false;
    public static ArrayList<LebensmittelEintrag> lebensmittel = new ArrayList<>();
    public NotificationManager notification;
    PendingIntent pendingIntent;
    private static final int uniqueID = 45612;
    private static final String CHANNEL = "abc";
    private NotificationManagerCompat notificationManager;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"Job started");
        Intent intent = new Intent(getApplicationContext(), Hauptbildschirm.class);
        pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        doBackgroundWork(params);

       // Cursor cursor = mDatabase.rawQuery("select * from " + LebensmittelContract.LebensmittelDbEintrag.TABLE_NAME, null);

        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                lebensmittel = alleLebensmiitelEinträgeAuslesen();

                Date today = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(today);
                cal.add(Calendar.DATE, 2);
                String einTagVorher = cal.getTime().toString().substring(0,10);
                for(LebensmittelEintrag item: lebensmittel){
                    String zuPrüfendesAblaufdatum = item.getAblaufdatum().toString().substring(0,10);
                    Log.v("Prüfer:", "Item Name: " + item.getName() + "läuft am:!" + zuPrüfendesAblaufdatum + "ab." + "Heute minus 2 ist der: "
                            + einTagVorher);

                    if(zuPrüfendesAblaufdatum.equals(einTagVorher)){

                        Notification notification = new NotificationCompat.Builder(getApplication(),BaseApplication.CHANNEL_1_ID)
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.frische_lebensmittel_icon)
                                .setTicker("Achtung!")
                                .setContentText("Aufgepasst! Dein Eintrag: " + item.getName() + " läuft morgen ab! Vielleicht kann es ja eine andere Person gebrauchen?")
                                .setContentTitle("FoodUse")
                                .setContentIntent(pendingIntent)
                                .build();
                        Log.v("Prüfer:", "Apfel wurde gefunden!!! Er läuft am: " + item.getAblaufdatum() + "ab!" + "Heute minus 1 ist der: "
                        + einTagVorher);
                        notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        notificationManager.notify(1,notification);
                    }
                }
                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        jobCancelled = true;
        return false;
    }

    public Cursor allesLesen(){
        LebensmittelDBHelper dbHelper = new LebensmittelDBHelper(this);
        SQLiteDatabase mDatabase = dbHelper.getReadableDatabase();
         return mDatabase.rawQuery("select * from " + LebensmittelContract.LebensmittelDbEintrag.TABLE_NAME, null);
    }

    public ArrayList<LebensmittelEintrag> alleLebensmiitelEinträgeAuslesen(){
        Cursor cursor = allesLesen();
        ArrayList<LebensmittelEintrag> alleEinträge = new ArrayList<>();

        String name;
        String kaufdatum;
        int menge;
        Date ablaufdatum;
        Boolean kühlung;
        String herkunftsort;
        String TempAblaufdatum;
        int ID;
        if (cursor.moveToFirst()){
            name = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_NAME));
            menge = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_MENGE));
            kaufdatum = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KAUFDATUM));
            TempAblaufdatum = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_ABLAUFDATUM));
            ID = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag._ID));
            Log.v("Debug","Ablaufdatum aus DB ausgelesen:" + TempAblaufdatum);
            try {
                ablaufdatum = new SimpleDateFormat(
                        "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(TempAblaufdatum);
            } catch (ParseException e) {
                e.printStackTrace();
                ablaufdatum = new Date();
            }

            int num = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KÜHLUNG));
            if(num==1){
                kühlung = true;
            } else {
                kühlung = false;
            }
            herkunftsort = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_HERKUNFTSORT));
            Log.v("Debug","Ablaufdatum aus DB ausgelesen NACH DEM PARSEN:" + ablaufdatum);
            alleEinträge.add(new LebensmittelEintrag(name, kaufdatum, ablaufdatum,  herkunftsort,  kühlung,  menge, ID));

            while(cursor.moveToNext()){
                name = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_NAME));
                menge = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_MENGE));
                kaufdatum = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KAUFDATUM));
                TempAblaufdatum = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_ABLAUFDATUM));
                ID = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag._ID));
                Log.v("Debug","Ablaufdatum aus DB ausgelesen:" + TempAblaufdatum);
                try {
                    ablaufdatum =new SimpleDateFormat(
                            "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(TempAblaufdatum);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                num = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KÜHLUNG));
                if(num==1){
                    kühlung = true;
                } else {
                    kühlung = false;
                }
                Log.v("Debug","Ablaufdatum aus DB ausgelesen NACH DEM PARSEN:" + ablaufdatum);
                herkunftsort = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_HERKUNFTSORT));
                alleEinträge.add(new LebensmittelEintrag(name, kaufdatum, ablaufdatum,  herkunftsort,  kühlung,  menge, ID));
            }
        } else {

        }

        return alleEinträge;
    }
}
