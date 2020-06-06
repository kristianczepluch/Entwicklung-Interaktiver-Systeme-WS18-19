package com.example.kristian.fooduse;

import android.provider.BaseColumns;

public class LebensmittelContract {
    /*
    Diese Klasse hält lediglich einige Konstanten bereit, welche für die Arbeit mit der Datenbank notwendig sind. Da
    es häufig zu Fehlern bei falschen Namen in diesem Bereich kommt, macht es Sinn diese einmal anzulegen und immer verfügbar zu halten.
     */

    private LebensmittelContract() {}

    public static final class LebensmittelDbEintrag implements BaseColumns {
        public static final String TABLE_NAME = "LebensmittelListe";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_MENGE = "menge";
        public static final String COLUMN_ABLAUFDATUM = "ablaufdatum";
        public static final String COLUMN_KAUFDATUM = "kaufdatum";
        public static final String COLUMN_HERKUNFTSORT = "herkunftsort";
        public static final String COLUMN_KÜHLUNG = "kühlung";

    }

    public static final class LebensmittelInfo implements BaseColumns {
        public static final String TABLE_NAME = "Lebensmittelinformationen";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_GRUNDHALTBARKEIT = "grundhaltbarkeit";
        public static final String COMLUMN_KÜHLUNGSZUSCHLAG = "kühlungszuschlag";
        public static final String COLUMN_ETHYLEN = "ethylen";
    }

    public static final class HerkunftslandInfo implements BaseColumns {
        public static final String TABLE_NAME = "HerkunftslandInformationen";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LAT = "breitengrad";
        public static final String COMLUMN_LON = "längengrad";
    }
}
