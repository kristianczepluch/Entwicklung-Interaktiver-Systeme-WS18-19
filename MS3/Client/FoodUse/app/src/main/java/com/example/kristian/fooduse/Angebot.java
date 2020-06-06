package com.example.kristian.fooduse;

public class Angebot {

    private String uid;
    private String zeit;
    private String titel;
    private String abholzeitpunkt;
    private String lebensmittel;
    private String ablaufdatum;
    private String beschreibung;
    private Double laengengrad;
    private Double breitengrad;
    private String entfernung;
    private String einschreankungen;
    private int abholwert;
    private String id;
    private String bild;


    public Angebot(String id,String uid, String zeit, String titel, String abholzeitpunkt, String lebensmittel, String ablaufdatum, String beschreibung, Double laengengrad, Double breitengrad, String einschreankungen, int abholwert, String entfernung, String bild) {
        this.id = id;
        this.uid = uid;
        this.zeit = zeit;
        this.titel = titel;
        this.abholzeitpunkt = abholzeitpunkt;
        this.lebensmittel = lebensmittel;
        this.ablaufdatum = ablaufdatum;
        this.beschreibung = beschreibung;
        this.laengengrad = laengengrad;
        this.breitengrad = breitengrad;
        this.einschreankungen = einschreankungen;
        this.abholwert = abholwert;
        this.entfernung = entfernung;
        this.bild = bild;
    }

    public Angebot( String titel, String entfernung) {
        this.titel = titel;
        this.entfernung = entfernung;
    }



    public String getEntfernung() {
        return entfernung;
    }

    public void setEntfernung(String entfernung) {
        this.entfernung = entfernung;
    }

    public String getUid() {

        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getZeit() {
        return zeit;
    }

    public void setZeit(String zeit) {
        this.zeit = zeit;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getAbholzeitpunkt() {
        return abholzeitpunkt;
    }

    public void setAbholzeitpunkt(String abholzeitpunkt) {
        this.abholzeitpunkt = abholzeitpunkt;
    }

    public String getLebensmittel() {
        return lebensmittel;
    }

    public void setLebensmittel(String lebensmittel) {
        this.lebensmittel = lebensmittel;
    }

    public String getAblaufdatum() {
        return ablaufdatum;
    }

    public void setAblaufdatum(String ablaufdatum) {
        this.ablaufdatum = ablaufdatum;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public Double getLaengengrad() {
        return laengengrad;
    }

    public void setLaengengrad(Double laengengrad) {
        this.laengengrad = laengengrad;
    }

    public Double getBreitengrad() {
        return breitengrad;
    }

    public void setBreitengrad(Double breitengrad) {
        this.breitengrad = breitengrad;
    }

    public String getEinschreankungen() {
        return einschreankungen;
    }

    public void setEinschreankungen(String einschreankungen) {
        this.einschreankungen = einschreankungen;
    }

    public int getAbholwert() {
        return abholwert;
    }

    public void setAbholwert(int abholwert) {
        this.abholwert = abholwert;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBild() {
        return bild;
    }

    public void setBild(String bild) {
        this.bild = bild;
    }
}
