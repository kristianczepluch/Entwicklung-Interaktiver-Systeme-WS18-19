package com.example.kristian.fooduse;

import java.util.Date;

// Diese Klasse stellt einen endgültigen Lebensmitteleintrag dar.

public class LebensmittelEintrag {
    private String name;
    private String kaufdatum;
    private Date ablaufdatum;
    private String herkunftsort;
    private boolean kühlung;


    public LebensmittelEintrag(String name, String kaufdatum, Date ablaufdatum, String herkunftsort, boolean kühlung) {
        this.name = name;
        this.kaufdatum = kaufdatum;
        this.ablaufdatum = ablaufdatum;
        this.herkunftsort = herkunftsort;
        this.kühlung = kühlung;
    }

    public LebensmittelEintrag() {

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
