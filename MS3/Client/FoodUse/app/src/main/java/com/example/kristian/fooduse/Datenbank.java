package com.example.kristian.fooduse;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Datenbank {
    Context context;


    public Datenbank(Context context) {
        this.context = context;
    }

    Lebensmittelinformationen notFound  = new Lebensmittelinformationen("NotFound", 0, 0, 0);
    Standort not_found = new Standort(0,0);

    Lebensmittelinformationen getLebensmittelInfo(String name) {

        LebensmittelDBHelper dbHelper = new LebensmittelDBHelper(context);
        SQLiteDatabase readdb = dbHelper.getReadableDatabase();

        Cursor cursor = readdb.rawQuery("select * from " + LebensmittelContract.LebensmittelInfo.TABLE_NAME + " WHERE name = " + "'" + name + "'", null);
        if (cursor.moveToFirst()) {
            int ghd = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelInfo.COLUMN_GRUNDHALTBARKEIT));
            int kühlung = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelInfo.COMLUMN_KÜHLUNGSZUSCHLAG));
            Double eth = cursor.getDouble(cursor.getColumnIndex(LebensmittelContract.LebensmittelInfo.COLUMN_ETHYLEN));
            return new Lebensmittelinformationen(name, ghd, kühlung,eth);
        } else return notFound;
    }

    Standort getStandortInfo(String name) {
        LebensmittelDBHelper dbHelper = new LebensmittelDBHelper(context);
        SQLiteDatabase readdb = dbHelper.getReadableDatabase();
        Cursor cursor = readdb.rawQuery("select * from " + LebensmittelContract.HerkunftslandInfo.TABLE_NAME + " WHERE name = " + "'" + name + "'", null);
        if (cursor.moveToFirst()) {
            Double lat = cursor.getDouble(cursor.getColumnIndex(LebensmittelContract.HerkunftslandInfo.COLUMN_LAT));
            Double lon = cursor.getDouble(cursor.getColumnIndex(LebensmittelContract.HerkunftslandInfo.COMLUMN_LON));
            return new Standort(lat,lon);
        } else {
            return not_found;
        }
    }
}
