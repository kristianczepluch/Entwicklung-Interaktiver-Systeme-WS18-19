package com.example.kristian.fooduse;

public class Datenbank {

    public Datenbank() {

    }

    Lebensmittelinformationen apfel_info = new Lebensmittelinformationen("Apfel", 21, 24, 0.3);
    Lebensmittelinformationen blaubeere_info = new Lebensmittelinformationen("Blaubeere", 2, 5, 0);
    Lebensmittelinformationen orange_info = new Lebensmittelinformationen("Orange", 21, 24, 0);
    Lebensmittelinformationen banane_info  = new Lebensmittelinformationen("Banane", 4, 1, 0);
    Lebensmittelinformationen notFound  = new Lebensmittelinformationen("NotFound", 0, 0, 0);

    Standort spanien = new Standort(40.46367,3.749220);
    Standort deutschland = new Standort(51.16569,10.45153);
    Standort costa_rica = new Standort(9.748917,-83.75343);
    Standort not_found = new Standort(0,0);

    Lebensmittelinformationen getLebensmittelInfo(String name) {
        if(name.equals("Apfel")) {
            return apfel_info;
        } else if(name.equals("Blaubeere")) {
            return blaubeere_info;
        } else if(name.equals("Orange")) {
            return orange_info;
        } else if(name.equals("Banane")) {
            return banane_info;
        } else return notFound;
    }

    Standort getStandortInfo(String name) {
        if(name.equals("Spanien")) {
            return spanien;
        } else if(name.equals("Costa Rica")) {
            return costa_rica;
        } else if(name.equals("Deutschland")) {
            return deutschland;
        } else return not_found;
    }
}
