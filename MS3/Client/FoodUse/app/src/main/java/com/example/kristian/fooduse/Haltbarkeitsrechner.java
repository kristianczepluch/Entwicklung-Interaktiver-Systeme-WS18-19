package com.example.kristian.fooduse;
import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
 An dieser Stelle sind die ganzen Berechnungen für die Haltbarkeitsdaten gespeichert. Es gibt verschiedene Kombinationen, welche
 entscheidend für die Berechnung sind.
  */

public class Haltbarkeitsrechner {
    public  Datenbank db;
    public static Distanzrechner distanzrechner = new Distanzrechner();
    private Context context;
    public Haltbarkeitsrechner(Context context) {
        this.context = context;
        db = new Datenbank(context);
    }

    ArrayList<LebensmittelEintrag> berechneOhneEthylenMitKühlung(ArrayList<LebensmittelVerarbeitungsObjekt> lebensmittel_verarbeitungs_liste) {
        ArrayList<LebensmittelEintrag> liste_mit_endgültigen_daten = new ArrayList<>();
        for(LebensmittelVerarbeitungsObjekt i : lebensmittel_verarbeitungs_liste) {
            LebensmittelEintrag eintrag = new LebensmittelEintrag();
            eintrag.setName(i.getName());
            eintrag.setKaufdatum(i.getKaufdatum());
            eintrag.setHerkunftsort(i.getHerkunftsland());
            eintrag.setKühlung(true);
            eintrag.setMenge(i.getMenge());
            // Abaufdatum wird anhand der restlichen Faktoren ermittelt.
            int grundhaltbarkeit = i.getGrundhaltbarkeit();
            int kühlungsaufschlag = i.getKühlung();
            String kaufdatum = i.getKaufdatum();


            SimpleDateFormat dateFormat = new SimpleDateFormat( "dd.MM.yyyy" );
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime( dateFormat.parse(kaufdatum) );
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            int transportabzug = distanzAbzug(i.getHerkunftsland());

            cal.add( Calendar.DATE, grundhaltbarkeit+kühlungsaufschlag-transportabzug);
            Date datum = cal.getTime();
            dateFormat.format(datum);
            eintrag.setAblaufdatum(datum);
            liste_mit_endgültigen_daten.add(eintrag);
        }
        return liste_mit_endgültigen_daten;
    }

    ArrayList<LebensmittelEintrag> berechneMitEthylenMitKühlung(ArrayList<LebensmittelVerarbeitungsObjekt> lebensmittel_verarbeitungs_liste) {
        ArrayList<LebensmittelEintrag> liste_mit_endgültigen_daten = new ArrayList<>();
        for(LebensmittelVerarbeitungsObjekt i : lebensmittel_verarbeitungs_liste) {
            LebensmittelEintrag eintrag = new LebensmittelEintrag();
            eintrag.setName(i.getName());
            eintrag.setKaufdatum(i.getKaufdatum());
            eintrag.setHerkunftsort(i.getHerkunftsland());
            eintrag.setKühlung(true);
            eintrag.setMenge(i.getMenge());
            // Abaufdatum wird anhand der restlichen Faktoren ermittelt.
            int grundhaltbarkeit = i.getGrundhaltbarkeit();
            int kühlungsaufschlag = i.getKühlung();
            String kaufdatum = i.getKaufdatum();


            SimpleDateFormat dateFormat = new SimpleDateFormat( "dd.MM.yyyy" );
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime( dateFormat.parse(kaufdatum) );
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            int transportabzug = distanzAbzug(i.getHerkunftsland());

            cal.add( Calendar.DATE, (int)((grundhaltbarkeit+kühlungsaufschlag-transportabzug)*0.7));
            Date datum = cal.getTime();
            dateFormat.format(datum);
            eintrag.setAblaufdatum(datum);
            liste_mit_endgültigen_daten.add(eintrag);
        }
        return liste_mit_endgültigen_daten;
    }

    ArrayList<LebensmittelEintrag> berechneMitEthylenOhneKühlung(ArrayList<LebensmittelVerarbeitungsObjekt> lebensmittel_verarbeitungs_liste) {
        ArrayList<LebensmittelEintrag> liste_mit_endgültigen_daten = new ArrayList<>();
        for(LebensmittelVerarbeitungsObjekt i : lebensmittel_verarbeitungs_liste) {
            LebensmittelEintrag eintrag = new LebensmittelEintrag();
            eintrag.setName(i.getName());
            eintrag.setKaufdatum(i.getKaufdatum());
            eintrag.setHerkunftsort(i.getHerkunftsland());
            eintrag.setKühlung(false);
            eintrag.setMenge(i.getMenge());
            // Abaufdatum wird anhand der restlichen Faktoren ermittelt.
            int grundhaltbarkeit = i.getGrundhaltbarkeit();
            int kühlungsaufschlag = i.getKühlung();
            String kaufdatum = i.getKaufdatum();


            SimpleDateFormat dateFormat = new SimpleDateFormat( "dd.MM.yyyy" );
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime( dateFormat.parse(kaufdatum) );
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            int transportabzug = distanzAbzug(i.getHerkunftsland());

            cal.add( Calendar.DATE, (int)((grundhaltbarkeit-transportabzug)*0.7));
            Date datum = cal.getTime();
            dateFormat.format(datum);
            eintrag.setAblaufdatum(datum);
            liste_mit_endgültigen_daten.add(eintrag);
        }
        return liste_mit_endgültigen_daten;
    }

    ArrayList<LebensmittelEintrag> berechneOhneEthylenOhneKühlung(ArrayList<LebensmittelVerarbeitungsObjekt> lebensmittel_verarbeitungs_liste) {
        ArrayList<LebensmittelEintrag> liste_mit_endgültigen_daten = new ArrayList<>();
        for(LebensmittelVerarbeitungsObjekt i : lebensmittel_verarbeitungs_liste) {
            LebensmittelEintrag eintrag = new LebensmittelEintrag();
            eintrag.setName(i.getName());
            eintrag.setKaufdatum(i.getKaufdatum());
            eintrag.setHerkunftsort(i.getHerkunftsland());
            eintrag.setKühlung(false);
            eintrag.setMenge(i.getMenge());
            // Abaufdatum wird anhand der restlichen Faktoren ermittelt.
            int grundhaltbarkeit = i.getGrundhaltbarkeit();
            int kühlungsaufschlag = i.getKühlung();
            String kaufdatum = i.getKaufdatum();


            SimpleDateFormat dateFormat = new SimpleDateFormat( "dd.MM.yyyy" );
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime( dateFormat.parse(kaufdatum));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            int transportabzug = distanzAbzug(i.getHerkunftsland());

            cal.add( Calendar.DATE, ((grundhaltbarkeit-transportabzug)));
            Date datum = cal.getTime();
            dateFormat.format(datum);
            eintrag.setAblaufdatum(datum);
            liste_mit_endgültigen_daten.add(eintrag);
        }
        return liste_mit_endgültigen_daten;
    }



    ArrayList<LebensmittelEintrag> berechneImKühlschrank(ArrayList<LebensmittelEintrag> lebensmittel_verarbeitungs_liste, int ethylan_anzahl,int ohne_ethylen_gekühlt) {
        ArrayList<LebensmittelEintrag> liste_mit_endgültigen_daten = new ArrayList<>();
        // Wenn es nur ein Lebensmittel gibt so gibt es keinen Ethylenabzug
        int summe = ethylan_anzahl + ohne_ethylen_gekühlt;
        if (summe == 1) {
            // Berechne mit Kühlung ohne Ethylen
            for (LebensmittelEintrag i : lebensmittel_verarbeitungs_liste) {

                int grundhaltbarkeit = db.getLebensmittelInfo(i.getName()).getGrundhaltbarkeit();
                String kaufdatum = i.getKaufdatum();
                int kühlung = db.getLebensmittelInfo(i.getName()).getKühlungszuschlag();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(dateFormat.parse(kaufdatum));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                int transportabzug = distanzAbzug(i.getHerkunftsort());

                cal.add(Calendar.DATE, (grundhaltbarkeit + kühlung - transportabzug));
                Date datum = cal.getTime();
                dateFormat.format(datum);
                i.setAblaufdatum(datum);
                liste_mit_endgültigen_daten.add(i);
            }
            return liste_mit_endgültigen_daten;
        }

        if (summe > 1) {
            // Gibt es mindestens 1 Lebensmittel mit Ethylen? Wenn ja -> Berechne Mit Kühlung Mit Ethylen
            if (ethylan_anzahl > 0) {
                for (LebensmittelEintrag i : lebensmittel_verarbeitungs_liste) {

                    int grundhaltbarkeit = db.getLebensmittelInfo(i.getName()).getGrundhaltbarkeit();
                    String kaufdatum = i.getKaufdatum();
                    int kühlung = db.getLebensmittelInfo(i.getName()).getKühlungszuschlag();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    Calendar cal = Calendar.getInstance();
                    try {
                        cal.setTime(dateFormat.parse(kaufdatum));
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    int transportabzug = distanzAbzug(i.getHerkunftsort());

                    cal.add(Calendar.DATE, (int) ((grundhaltbarkeit + kühlung - transportabzug) * 0.7));
                    Date datum = cal.getTime();
                    dateFormat.format(datum);
                    i.setAblaufdatum(datum);
                    liste_mit_endgültigen_daten.add(i);
                }
                return liste_mit_endgültigen_daten;
            } else {
                // Falls nein so ist kein Ethylenabzug notwendig
                for (LebensmittelEintrag i : lebensmittel_verarbeitungs_liste) {

                    int grundhaltbarkeit = db.getLebensmittelInfo(i.getName()).getGrundhaltbarkeit();
                    String kaufdatum = i.getKaufdatum();
                    int kühlung = db.getLebensmittelInfo(i.getName()).getKühlungszuschlag();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    Calendar cal = Calendar.getInstance();
                    try {
                        cal.setTime(dateFormat.parse(kaufdatum));
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    int transportabzug = distanzAbzug(i.getHerkunftsort());

                    cal.add(Calendar.DATE, (grundhaltbarkeit + kühlung - transportabzug));
                    Date datum = cal.getTime();
                    dateFormat.format(datum);
                    i.setAblaufdatum(datum);
                    liste_mit_endgültigen_daten.add(i);
                }
            }
        }
        return liste_mit_endgültigen_daten;
    }


    ArrayList<LebensmittelEintrag> berechneAusserhabKühlschrank(ArrayList<LebensmittelEintrag> lebensmittel_verarbeitungs_liste, int ethylan_anzahl, int ohne_anzahl) {
        ArrayList<LebensmittelEintrag> liste_mit_endgültigen_daten = new ArrayList<>();

        int summe = ethylan_anzahl + ohne_anzahl;

        if(summe==1){
            for(LebensmittelEintrag i : lebensmittel_verarbeitungs_liste) {

                int grundhaltbarkeit = db.getLebensmittelInfo(i.getName()).getGrundhaltbarkeit();
                String kaufdatum = i.getKaufdatum();

                SimpleDateFormat dateFormat = new SimpleDateFormat( "dd.MM.yyyy" );
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime( dateFormat.parse(kaufdatum) );
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                int transportabzug = distanzAbzug(i.getHerkunftsort());

                cal.add( Calendar.DATE, ((grundhaltbarkeit - transportabzug)) );
                Date datum = cal.getTime();
                dateFormat.format(datum);
                i.setAblaufdatum(datum);
                liste_mit_endgültigen_daten.add(i);
            }
            return liste_mit_endgültigen_daten;

        } else{
            if(ethylan_anzahl>1){
                for(LebensmittelEintrag i : lebensmittel_verarbeitungs_liste) {

                    int grundhaltbarkeit = db.getLebensmittelInfo(i.getName()).getGrundhaltbarkeit();
                    String kaufdatum = i.getKaufdatum();

                    SimpleDateFormat dateFormat = new SimpleDateFormat( "dd.MM.yyyy" );
                    Calendar cal = Calendar.getInstance();
                    try {
                        cal.setTime( dateFormat.parse(kaufdatum) );
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    int transportabzug = distanzAbzug(i.getHerkunftsort());

                    cal.add( Calendar.DATE, (int)((grundhaltbarkeit - transportabzug)*0.7) );
                    Date datum = cal.getTime();
                    dateFormat.format(datum);
                    i.setAblaufdatum(datum);
                    liste_mit_endgültigen_daten.add(i);
                }
                return liste_mit_endgültigen_daten;

            } else{
                for(LebensmittelEintrag i : lebensmittel_verarbeitungs_liste) {
                    int grundhaltbarkeit = db.getLebensmittelInfo(i.getName()).getGrundhaltbarkeit();
                    String kaufdatum = i.getKaufdatum();
                    int kühlung = db.getLebensmittelInfo(i.getName()).getKühlungszuschlag();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    Calendar cal = Calendar.getInstance();
                    try {
                        cal.setTime(dateFormat.parse(kaufdatum));
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    int transportabzug = distanzAbzug(i.getHerkunftsort());

                    cal.add(Calendar.DATE, ((grundhaltbarkeit - transportabzug)));
                    Date datum = cal.getTime();
                    dateFormat.format(datum);
                    i.setAblaufdatum(datum);
                    liste_mit_endgültigen_daten.add(i);
                }

            }
        }
        return liste_mit_endgültigen_daten;
    }

    private int distanzAbzug(String herkunftsland) {
        double distanz = distanzrechner.getDistanz(db.getStandortInfo("Germany"), db.getStandortInfo(herkunftsland));
        if(distanz==0) {
            return 1;
        } else if (distanz < 1700 ) {
            return 2;
        } else{
        return 3;}

    }
}
