package com.example.kristian.fooduse;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.kristian.fooduse.LebensmittelContract.*;

import java.util.ArrayList;

public class LebensmittelDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "LebensmittelListeDB";
    public static final int DATABASE_VERSION = 19;

    public LebensmittelDBHelper(Context context) {
        super(context, DATABASE_NAME, null  , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_LEBENSMITTELLISTE_TABLE = "CREATE TABLE " +
                LebensmittelDbEintrag.TABLE_NAME + " (" +
                LebensmittelDbEintrag._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LebensmittelDbEintrag.COLUMN_NAME + " TEXT NOT NULL, " +
                LebensmittelDbEintrag.COLUMN_HERKUNFTSORT + " TEXT NOT NULL, " +
                LebensmittelDbEintrag.COLUMN_KAUFDATUM + " TEXT NOT NULL, " +
                LebensmittelDbEintrag.COLUMN_ABLAUFDATUM + " TEXT NOT NULL, " +
                LebensmittelDbEintrag.COLUMN_KÜHLUNG + " INTEGER NOT NULL, " +
                LebensmittelDbEintrag.COLUMN_MENGE + " INTEGER NOT NULL" +
                ");";
        final String SQL_CREATE_LEBENSMITTELINFORMATIONEN_TABLE = "CREATE TABLE " +
                LebensmittelInfo.TABLE_NAME + " (" +
                LebensmittelInfo._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LebensmittelInfo.COLUMN_NAME + " TEXT NOT NULL, " +
                LebensmittelInfo.COLUMN_GRUNDHALTBARKEIT + " INTEGER NOT NULL, " +
                LebensmittelInfo.COMLUMN_KÜHLUNGSZUSCHLAG + " INTEGER NOT NULL, " +
                LebensmittelInfo.COLUMN_ETHYLEN + " REAL NOT NULL" +
                ");";

        final String SQL_CREATE_HERKUNFTSLANDINFORMATIONEN_TABLE = "CREATE TABLE " +
                HerkunftslandInfo.TABLE_NAME + " (" +
                HerkunftslandInfo._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HerkunftslandInfo.COLUMN_NAME + " TEXT NOT NULL, " +
                HerkunftslandInfo.COLUMN_LAT + " REAL NOT NULL, " +
                HerkunftslandInfo.COMLUMN_LON + " REAL NOT NULL" +
                ");";


        db.execSQL(SQL_CREATE_HERKUNFTSLANDINFORMATIONEN_TABLE);
        db.execSQL(SQL_CREATE_LEBENSMITTELLISTE_TABLE);
        db.execSQL(SQL_CREATE_LEBENSMITTELINFORMATIONEN_TABLE);

        alleLebensmittelInformationenEintragen(db);
        alleStandortInformationenEintragen(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + LebensmittelDbEintrag.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + LebensmittelInfo.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + HerkunftslandInfo.TABLE_NAME);
            onCreate(db);
    }

    public void alleLebensmittelInformationenEintragen(SQLiteDatabase db){
        final String SQL_APFEL = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Apple'" + ", 21, 24, 0.7);";
        final String SQL_APRIKOSE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Apricot'" + ", 2, 6, 0.7);";
        final String SQL_AVOCADO = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Avocado'" + ", 3, 5, 0.7);";
        final String SQL_BANANE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Banana'" + ", 4, 1, 0.7);";
        final String SQL_BLAUBEERE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Blueberry'" + ", 2, 5, 0);";
        final String SQL_CANTALOUPE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Cantaloupe'" + ", 6, 5, 1);";
        final String SQL_KIRSCHE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Cherry'" + ", 2, 5, 0);";
        final String SQL_KOKOSNUSS = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Coconut'" + ", 7, 10, 0);";
        final String SQL_FEIGEN = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Fig'" + ", 3, 3, 0);";
        final String SQL_GRAPEFRUIT = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Grapefruit'" + ", 3, 3, 0);";
        final String SQL_TRAUBE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Grape'" + ", 4, 3, 0);";
        final String SQL_HONIGMELONE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Honeydew Melon'" + ", 6, 6, 0);";
        final String SQL_KIWI = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Kiwi'" + ", 13, 1, 0);";
        final String SQL_ZITRONE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Lemon'" + ", 21, 24, 0);";
        final String SQL_LIMETTE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Lime'" + ", 21, 24, 0);";
        final String SQL_MANGO = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Mango'" + ", 6, 4, 0.7);";
        final String SQL_ORANGE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Orange'" + ", 17, 28, 0);";
        final String SQL_ANANAS = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Pineapple'" + ", 2, 2, 0);";
        final String SQL_KÜRBIS = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Pumpkin'" + ", 75, 45, 0);";
        final String SQL_ERDBEERE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Strawberry'" + ", 2, 3, 0);";
        final String SQL_BIRNE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Pear'" + ", 3, 3, 0);";
        final String SQL_GRANATAPFEL = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Pommegranate'" + ", 7, 14, 0);";
        final String SQL_PFIRSICH = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Peach'" + ", 3, 1, 0.7);";
        final String SQL_PASSIONSFRUCHT = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Passionfruit'" + ", 15, 0, 0);";
        final String SQL_PAPAYA = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Papaya'" + ", 5, 3, 0);";
        final String SQL_TOMATE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Tomato'" + ", 7, 7, 0.7);";
        final String SQL_WASSERMELONE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Watermelon'" + ", 8, 9, 0);";
        final String SQL_MANDARINE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Tangerine'" + ", 6, 2, 0);";
        final String SQL_ROSENKOHL = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Brussels Sprouts'" + ", 3, 7, 0);";
        final String SQL_AUBERGINE = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Eggplant'" + ", 4, 13, 0);";
        final String SQL_ZWIEBEL = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Onion'" + ", 35, 10, 0);";
        final String SQL_RETTICH = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Radish'" + ", 4, 26, 0);";
        final String SQL_KARTOFFEL = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Potato'" + ", 24, 81, 0);";
        final String SQL_SUESSKARTOFFEL = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Sweet Potato'" + ", 24, 51, 0);";
        final String SQL_ZUCCHINI = "INSERT INTO Lebensmittelinformationen (name, grundhaltbarkeit, kühlungszuschlag, ethylen) VALUES (" +
                "'Zucchini'" + ", 3, 3, 0);";

        ArrayList<String> alleSqlStatements = new ArrayList<>();
        alleSqlStatements.add(SQL_APFEL);
        alleSqlStatements.add(SQL_APRIKOSE);
        alleSqlStatements.add(SQL_AVOCADO);
        alleSqlStatements.add(SQL_BANANE);
        alleSqlStatements.add(SQL_BLAUBEERE);
        alleSqlStatements.add(SQL_CANTALOUPE);
        alleSqlStatements.add(SQL_KIRSCHE);
        alleSqlStatements.add(SQL_KOKOSNUSS);
        alleSqlStatements.add(SQL_FEIGEN);
        alleSqlStatements.add(SQL_GRAPEFRUIT);
        alleSqlStatements.add(SQL_TRAUBE);
        alleSqlStatements.add(SQL_HONIGMELONE);
        alleSqlStatements.add(SQL_KIWI);
        alleSqlStatements.add(SQL_ZITRONE);
        alleSqlStatements.add(SQL_LIMETTE);
        alleSqlStatements.add(SQL_MANGO);
        alleSqlStatements.add(SQL_ORANGE);
        alleSqlStatements.add(SQL_ANANAS);
        alleSqlStatements.add(SQL_KÜRBIS);
        alleSqlStatements.add(SQL_ERDBEERE);
        alleSqlStatements.add(SQL_BIRNE);
        alleSqlStatements.add(SQL_GRANATAPFEL);
        alleSqlStatements.add(SQL_PFIRSICH);
        alleSqlStatements.add(SQL_PASSIONSFRUCHT);
        alleSqlStatements.add(SQL_PAPAYA);
        alleSqlStatements.add(SQL_TOMATE);
        alleSqlStatements.add(SQL_WASSERMELONE);
        alleSqlStatements.add(SQL_MANDARINE);
        alleSqlStatements.add(SQL_ROSENKOHL);
        alleSqlStatements.add(SQL_AUBERGINE);
        alleSqlStatements.add(SQL_ZWIEBEL);
        alleSqlStatements.add(SQL_RETTICH);
        alleSqlStatements.add(SQL_KARTOFFEL);
        alleSqlStatements.add(SQL_SUESSKARTOFFEL);
        alleSqlStatements.add(SQL_ZUCCHINI);

        for(String item: alleSqlStatements){
            db.execSQL(item);
        }

    }

    public void alleStandortInformationenEintragen(SQLiteDatabase db) {
        final String SQL_SPANIEN = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Spain'" + ", 40.46366700000001, -3.7492200000000366);";
        final String SQL_DEUTSCHLAND = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Germany'" + ",  51.16569, 10.45153);";
        final String SQL_COSTA_RICA = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Costa-Rica'" + ", 9.748916999999999, -83.75342799999999);";
        final String SQL_ITALIEN = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Italy'" + ", 41.87194, 12.567379999999957);";
        final String SQL_NIEDERLANDE = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Netherlands'" + ", 52.13263300000001, 5.2912659999999505);";
        final String SQL_MARROKO = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Morocco'" + ", 31.791702, -7.092620000000011);";
        final String SQL_TÜRKEI = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Turkey'" + ", 38.963745, 35.243322000000035);";
        final String SQL_RUSSLAND = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Russia'" + ", 61.52401, 105.31875600000001);";
        final String SQL_ISREAL = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Israel'" + ", 31.046051, 34.85161199999993);";
        final String SQL_NEUSEELAND = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'New Zealand'" + ", -40.90055699999999, 174.88597100000004);";
        final String SQL_FRANKREICH = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'France'" + ", 46.227638, 2.213749000000007);";
        final String SQL_POLEN = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Poland'" + ", 51.919438, 19.14513599999998);";
        final String SQL_CHINA = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'China'" + ", 35.86166000000001, 104.19539699999996);";
        final String SQL_USA = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'USA'" + ", 37.09024, -95.71289100000001);";
        final String SQL_KANADA = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Canada'" + ", 56.130366, -106.34677099999999);";
        final String SQL_BRASILIEN = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Brazil'" + ", -14.235004, -51.92527999999999);";
        final String SQL_BELGIEN = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Belgium'" + ", 50.503887, 4.4699359999999615);";
        final String SQL_ARGENTINIEN = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Argentina'" + ", -38.416097, -63.616671999999994);";
        final String SQL_INDIEN = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'India'" + ",  20.593684, 78.96288000000004);";
        final String SQL_INDONESIEN = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Indonesia'" + ", -0.789275, 113.92132700000002);";
        final String SQL_THAILAND = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Thailand'" + ", 15.870032, 100.99254100000007);";
        final String SQL_MEXICO = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Mexico'" + ", 23.634501, -102.55278399999997);";
        final String SQL_MALAYSIA = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Malaysia'" + ", 4.210483999999999, 101.97576600000002);";
        final String SQL_AUSTRALIEN = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Australia'" + ", -25.274398, 133.77513599999997);";
        final String SQL_VIETNAM = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Vietnam'" + ", 14.058324, 108.277199);";
        final String SQL_SÜDKOREA = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'South Korea'" + ", 35.90775699999999, 127.76692200000002);";
        final String SQL_ÖSTERREICH = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Austria'" + ", 47.516231, 14.550072);";
        final String SQL_NORWEGEN = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Norway'" + ", 60.47202399999999, 8.46894599999996);";
        final String SQL_SCHWEDEN = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Schweden'" + ", 60.12816100000001, 18.643501000000015);";
        final String SQL_SCHWEIZ = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Switzerland'" + ", 46.818188, 8.227511999999933);";
        final String SQL_KOLUMBIEN = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Colombia'" + ", 4.570868, -74.29733299999998);";
        final String SQL_PERU = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Peru'" + ", -9.189967, -75.015152);";
        final String SQL_BOLIVIEN = "INSERT INTO HerkunftslandInformationen (name, breitengrad, längengrad) VALUES (" +
                "'Bolivia'" + ", -16.290154, -63.58865300000002);";

        ArrayList<String> alleSqlStatements2 = new ArrayList<>();
        alleSqlStatements2.add(SQL_SPANIEN);
        alleSqlStatements2.add(SQL_COSTA_RICA);
        alleSqlStatements2.add(SQL_ITALIEN);
        alleSqlStatements2.add(SQL_NIEDERLANDE);
        alleSqlStatements2.add(SQL_MARROKO);
        alleSqlStatements2.add(SQL_TÜRKEI);
        alleSqlStatements2.add(SQL_RUSSLAND);
        alleSqlStatements2.add(SQL_ISREAL);
        alleSqlStatements2.add(SQL_NEUSEELAND);
        alleSqlStatements2.add(SQL_FRANKREICH);
        alleSqlStatements2.add(SQL_POLEN);
        alleSqlStatements2.add(SQL_CHINA);
        alleSqlStatements2.add(SQL_USA);
        alleSqlStatements2.add(SQL_KANADA);
        alleSqlStatements2.add(SQL_BRASILIEN);
        alleSqlStatements2.add(SQL_BELGIEN);
        alleSqlStatements2.add(SQL_ARGENTINIEN);
        alleSqlStatements2.add(SQL_INDIEN);
        alleSqlStatements2.add(SQL_INDONESIEN);
        alleSqlStatements2.add(SQL_THAILAND);
        alleSqlStatements2.add(SQL_MEXICO);
        alleSqlStatements2.add(SQL_MALAYSIA);
        alleSqlStatements2.add(SQL_AUSTRALIEN);
        alleSqlStatements2.add(SQL_VIETNAM);
        alleSqlStatements2.add(SQL_SÜDKOREA);
        alleSqlStatements2.add(SQL_ÖSTERREICH);
        alleSqlStatements2.add(SQL_NORWEGEN);
        alleSqlStatements2.add(SQL_SCHWEDEN);
        alleSqlStatements2.add(SQL_SCHWEIZ);
        alleSqlStatements2.add(SQL_KOLUMBIEN);
        alleSqlStatements2.add(SQL_PERU);
        alleSqlStatements2.add(SQL_BOLIVIEN);
        alleSqlStatements2.add(SQL_DEUTSCHLAND);

        for(String item: alleSqlStatements2){
            db.execSQL(item);
        }
        //db.execSQL(SQL_SPANIEN);
    }

    public Lebensmittelinformationen getLebensmittelInfo(String name) {
        SQLiteDatabase rDatabase = getReadableDatabase();
        Cursor cursor = rDatabase.rawQuery("select * from " + LebensmittelContract.LebensmittelInfo.TABLE_NAME + " WHERE name = " + "'" + name + "'" , null);
        Log.d("COCOLINO0", "ES WIRD GESUCHT NACH: " + name);
        int grundhaltbarkeit = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelInfo.COLUMN_GRUNDHALTBARKEIT));
        Log.d("COCOLINO0", "Grundhaltbarkeit ist: " + grundhaltbarkeit);
        int kühlung = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelInfo.COMLUMN_KÜHLUNGSZUSCHLAG));
        double ethylen = cursor.getDouble(cursor.getColumnIndex(LebensmittelContract.LebensmittelInfo.COLUMN_ETHYLEN));
        return new Lebensmittelinformationen(name, grundhaltbarkeit,kühlung,ethylen);
    }
}

