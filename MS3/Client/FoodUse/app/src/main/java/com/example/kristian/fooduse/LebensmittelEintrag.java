package com.example.kristian.fooduse;

import java.util.Date;

// Diese Klasse stellt einen endgültigen Lebensmitteleintrag dar.

public class LebensmittelEintrag {
    private String name;
    private String kaufdatum;
    private Date ablaufdatum;
    private String herkunftsort;
    private boolean kühlung;
    private int menge;
    private int ID;


    public LebensmittelEintrag(String name, String kaufdatum, Date ablaufdatum, String herkunftsort, boolean kühlung, int menge) {
        this.name = name;
        this.kaufdatum = kaufdatum;
        this.ablaufdatum = ablaufdatum;
        this.herkunftsort = herkunftsort;
        this.kühlung = kühlung;
        this.menge = menge;
    }
    public LebensmittelEintrag(String name, String kaufdatum, Date ablaufdatum, String herkunftsort, boolean kühlung, int menge, int ID) {
        this.name = name;
        this.kaufdatum = kaufdatum;
        this.ablaufdatum = ablaufdatum;
        this.herkunftsort = herkunftsort;
        this.kühlung = kühlung;
        this.menge = menge;
        this.ID = ID;
    }

    public LebensmittelEintrag() {

    }

    public int getID(){
        return ID;
    }

    public void setID(int ID){
        this.ID = ID;
    }

    public int getMenge(){
        return menge;
    }

    void setMenge(int menge){
        this.menge = menge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getKaufdatum() {
        return kaufdatum;
    }

    public void setKaufdatum(String kaufdatum) {
        this.kaufdatum = kaufdatum;
    }

    public Date getAblaufdatum() {
        return ablaufdatum;
    }

    public void setAblaufdatum(Date ablaufdatum) {
        this.ablaufdatum = ablaufdatum;
    }

    public String getHerkunftsort() {
        return herkunftsort;
    }

    public void setHerkunftsort(String herkunftsort) {
        this.herkunftsort = herkunftsort;
    }

    public boolean isKühlung() {
        return kühlung;
    }

    public void setKühlung(boolean kühlung) {
        this.kühlung = kühlung;
    }
}
