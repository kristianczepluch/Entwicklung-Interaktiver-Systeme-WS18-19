package com.example.kristian.fooduse;

public class Lebensmittel {

    private String name;
    private String herkunftsland;
    private String einkaufsdatum;

    public Lebensmittel(String name, String herkunftsland, String einkaufsdatum) {
        this.name = name;
        this.herkunftsland = herkunftsland;
        this.einkaufsdatum = einkaufsdatum;
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

}
