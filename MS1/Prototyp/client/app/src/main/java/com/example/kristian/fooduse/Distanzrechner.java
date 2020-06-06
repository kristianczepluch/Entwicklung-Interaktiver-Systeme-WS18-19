package com.example.kristian.fooduse;
import java.lang.Math;

public class Distanzrechner {
    public Distanzrechner() {

    }

    double getDistanz(Standort ort1, Standort ort2){
        double breitengrad_ort_1 = ort1.getLat();
        double laengengrad_ort_1 = ort1.getLon();

        double breitengrad_ort_2 = ort2.getLat();
        double laengengrad_ort_2 = ort2.getLon();

        double breitengrad = (breitengrad_ort_1 + breitengrad_ort_2) / 2 * 0.01745;
        double dx = 111.3 * (Math.cos(breitengrad) * (laengengrad_ort_1 - laengengrad_ort_2));
        double dy = 111.3 * (breitengrad_ort_1 - breitengrad_ort_2);

        double distanz = Math.sqrt((Math.pow(dx, 2))+(Math.pow(dy, 2)));
        return  distanz;
    }
}
