package com.example.kristian.fooduse;

import java.util.Date;

// Diese Klasse stellt die Informationen für die Lebensmittel dar.

public class Lebensmittelinformationen {

    private String name;
    private int grundhaltbarkeit;
    private int kühlungszuschlag;
    private double ethylen;

    public Lebensmittelinformationen(String name, int grundhaltbarkeit, int kühlungszuschlag, double ethylen) {
        this.name = name;
        this.grundhaltbarkeit = grundhaltbarkeit;
        this.kühlungszuschlag = kühlungszuschlag;
        this.ethylen = ethylen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrundhaltbarkeit() {
        return grundhaltbarkeit;
    }

    public void setGrundhaltbarkeit(int grundhaltbarkeit) {
        this.grundhaltbarkeit = grundhaltbarkeit;
    }

    public int getKühlungszuschlag() {
        return kühlungszuschlag;
    }

    public void setKühlungszuschlag(int kühlungszuschlag) {
        this.kühlungszuschlag = kühlungszuschlag;
    }

    public double getEthylen() {
        return ethylen;
    }

    public void setEthylen(double ethylen) {
        this.ethylen = ethylen;
    }
}
