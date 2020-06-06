package com.example.kristian.fooduse;

/*
Die Klasse Beschreibt einen LebensmittelEintrag. Diese Eintrag wird erstellt, sobald ein Lebensmittel eingelesen wird.
Neben dem Konsturktor und Getter und Setter Methoden ist an dieser Stelle nichts weiter implementiert.
 */
public class Lebensmittel {

    private String name;
    private String herkunftsland;
    private String einkaufsdatum;
    private int menge;

    public Lebensmittel(String name, String herkunftsland, String einkaufsdatum, int menge) {
        this.name = name;
        this.herkunftsland = herkunftsland;
        this.einkaufsdatum = einkaufsdatum;
        this.menge = menge;
    }

    public Lebensmittel() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHerkunftsland() {
        return herkunftsland;
    }

    public void setHerkunftsland(String herkunftsland) {
        this.herkunftsland = herkunftsland;
    }

    public String getEinkaufsdatum() {
        return einkaufsdatum;
    }

    public void setEinkaufsdatum(String einkaufsdatum) {
        this.einkaufsdatum = einkaufsdatum;
    }

    public void setMenge(int menge) {
        this.menge = menge;
    }

    public int getMenge(){
        return menge;
    }

}
