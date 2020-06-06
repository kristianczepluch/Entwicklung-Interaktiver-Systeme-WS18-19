package com.example.kristian.fooduse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Haltbarkeitsrechner {
    public static Datenbank db = new Datenbank();
    public static Distanzrechner distanzrechner = new Distanzrechner();

    public Haltbarkeitsrechner() {
    }
    ArrayList<LebensmittelEintrag> berechneOhneEthylen(ArrayList<LebensmittelVerarbeitungsObjekt> lebensmittel_verarbeitungs_liste, int mit_ethylen, int ohne_ethylen) {
        ArrayList<LebensmittelEintrag> liste_mit_endgültigen_daten = new ArrayList<>();
        for(LebensmittelVerarbeitungsObjekt i : lebensmittel_verarbeitungs_liste) {
            LebensmittelEintrag eintrag = new LebensmittelEintrag();
            eintrag.setName(i.getName());
            eintrag.setKaufdatum(i.getKaufdatum());
            eintrag.setHerkunftsort(i.getHerkunftsland());
            eintrag.setKühlung(true);

            // Abaufdatum wird anhand der restlichen Faktoren ermittelt.
            int grundhaltbarkeit = i.getGrundhaltbarkeit();
            int kühlungsaufschlag = i.getKühlung();
            String kaufdatum = i.getKaufdatum();

            SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy" );
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

    ArrayList<LebensmittelEintrag> berechneMitEthylen(ArrayList<LebensmittelVerarbeitungsObjekt> lebensmittel_verarbeitungs_liste, int mit_ethylen, int ohne_ethylen) {
        ArrayList<LebensmittelEintrag> liste_mit_endgültigen_daten = new ArrayList<>();
        if(ohne_ethylen>0) {
            for(LebensmittelVerarbeitungsObjekt i : lebensmittel_verarbeitungs_liste) {
                LebensmittelEintrag eintrag = new LebensmittelEintrag();
                eintrag.setName(i.getName());
                eintrag.setKaufdatum(i.getKaufdatum());
                eintrag.setHerkunftsort(i.getHerkunftsland());
                eintrag.setKühlung(false);

                // Abaufdatum wird anhand der restlichen Faktoren ermittelt.
                int grundhaltbarkeit = i.getGrundhaltbarkeit();
                String kaufdatum = i.getKaufdatum();

                SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy" );
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime( dateFormat.parse(kaufdatum) );
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                int transportabzug = distanzAbzug(i.getHerkunftsland());
                if(mit_ethylen>1) {
                    cal.add(Calendar.DATE, (int) ((grundhaltbarkeit - transportabzug) - grundhaltbarkeit * i.getEthylen() ));
                } else cal.add(Calendar.DATE, (int) (grundhaltbarkeit - transportabzug));
                Date datum = cal.getTime();
                dateFormat.format(datum);
                eintrag.setAblaufdatum(datum);
                liste_mit_endgültigen_daten.add(eintrag);
            }
            return liste_mit_endgültigen_daten;
        } else {
            for(LebensmittelVerarbeitungsObjekt i : lebensmittel_verarbeitungs_liste) {
                LebensmittelEintrag eintrag = new LebensmittelEintrag();
                eintrag.setName(i.getName());
                eintrag.setKaufdatum(i.getKaufdatum());
                eintrag.setHerkunftsort(i.getHerkunftsland());
                eintrag.setKühlung(true);

                // Abaufdatum wird anhand der restlichen Faktoren ermittelt.
                int grundhaltbarkeit = i.getGrundhaltbarkeit();
                int kühlung = i.getKühlung();
                String kaufdatum = i.getKaufdatum();

                SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy" );
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime( dateFormat.parse(kaufdatum) );
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                int transportabzug = distanzAbzug(i.getHerkunftsland());

                cal.add( Calendar.DATE,grundhaltbarkeit + kühlung - transportabzug);


                Date datum = cal.getTime();
                dateFormat.format(datum);
                eintrag.setAblaufdatum(datum);
                liste_mit_endgültigen_daten.add(eintrag);
            }
            return liste_mit_endgültigen_daten;
        }
    }

    ArrayList<LebensmittelEintrag> berechneImKühlschrank(ArrayList<LebensmittelEintrag> lebensmittel_verarbeitungs_liste, int ethylan_anzahl) {
        ArrayList<LebensmittelEintrag> liste_mit_endgültigen_daten = new ArrayList<>();
        if(ethylan_anzahl > 0 && lebensmittel_verarbeitungs_liste.size() > 1){
            for(LebensmittelEintrag i : lebensmittel_verarbeitungs_liste) {

                int grundhaltbarkeit = db.getLebensmittelInfo(i.getName()).getGrundhaltbarkeit();
                String kaufdatum = i.getKaufdatum();
                int kühlung = db.getLebensmittelInfo(i.getName()).getKühlungszuschlag();
                System.out.println("Hello" + i.getName() + ethylan_anzahl);
                SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy" );
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime( dateFormat.parse(kaufdatum) );
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                int transportabzug = distanzAbzug(i.getHerkunftsort());

                cal.add( Calendar.DATE, (int)((grundhaltbarkeit + kühlung - transportabzug)*0.3) );
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

                SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy" );
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime( dateFormat.parse(kaufdatum) );
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                int transportabzug = distanzAbzug(i.getHerkunftsort());

                cal.add(Calendar.DATE,((grundhaltbarkeit + kühlung - transportabzug)));
                Date datum = cal.getTime();
                dateFormat.format(datum);
                i.setAblaufdatum(datum);
                liste_mit_endgültigen_daten.add(i);
            }
            return liste_mit_endgültigen_daten;
        }
    }

    ArrayList<LebensmittelEintrag> berechneAusserhabKühlschrank(ArrayList<LebensmittelEintrag> lebensmittel_verarbeitungs_liste, int ethylan_anzahl) {
        ArrayList<LebensmittelEintrag> liste_mit_endgültigen_daten = new ArrayList<>();
        if(ethylan_anzahl > 0 && lebensmittel_verarbeitungs_liste.size() > 1){
            for(LebensmittelEintrag i : lebensmittel_verarbeitungs_liste) {

                int grundhaltbarkeit = db.getLebensmittelInfo(i.getName()).getGrundhaltbarkeit();
                String kaufdatum = i.getKaufdatum();

                SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy" );
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime( dateFormat.parse(kaufdatum) );
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                int transportabzug = distanzAbzug(i.getHerkunftsort());

                cal.add( Calendar.DATE, (int)((grundhaltbarkeit - transportabzug)*0.3) );
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

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(dateFormat.parse(kaufdatum));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                int transportabzug = distanzAbzug(i.getHerkunftsort());

                cal.add(Calendar.DATE, (int) ((grundhaltbarkeit - transportabzug)));
                Date datum = cal.getTime();
                dateFormat.format(datum);
                i.setAblaufdatum(datum);
                liste_mit_endgültigen_daten.add(i);
            }
            return liste_mit_endgültigen_daten;
        }
    }

    private int distanzAbzug(String herkunftsland) {
        double distanz = distanzrechner.getDistanz(db.getStandortInfo("Deutschland"), db.getStandortInfo(herkunftsland));
        if(distanz==0) {
            return 1;
        } else if (distanz < 1500 ) {
            return 2;
        } else
        return 3;

    }
}
