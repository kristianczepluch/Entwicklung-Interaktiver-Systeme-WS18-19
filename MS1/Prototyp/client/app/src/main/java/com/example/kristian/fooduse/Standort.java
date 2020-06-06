package com.example.kristian.fooduse;


// Diese Klasse fasst lediglich laengen und breitegrade zusammen;
public class Standort {

    double lat;
    double lon;
    String straße;
    int haus_nummer;
    String ort;
    String land;

    public Standort(double lat, double lon, String straße, int haus_nummer, String ort) {
        super();
        this.lat = lat;
        this.lon = lon;
        this.straße = straße;
        this.haus_nummer = haus_nummer;
        this.ort = ort;
    }

    public Standort(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getStraße() {
        return straße;
    }

    public void setStraße(String straße) {
        this.straße = straße;
    }

    public int getHaus_nummer() {
        return haus_nummer;
    }

    public void setHaus_nummer(int haus_nummer) {
        this.haus_nummer = haus_nummer;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }
}


