package com.example.kristian.fooduse;

public class LebensmittelVerarbeitungsObjekt {
    Lebensmittel lebensmittel;
    Lebensmittelinformationen lebensmittel_info;


    public LebensmittelVerarbeitungsObjekt(Lebensmittel lebensmittel, Lebensmittelinformationen lebensmittel_info) {
        this.lebensmittel = lebensmittel;
        this.lebensmittel_info = lebensmittel_info;
    }

    String getName() {
        return lebensmittel.getName();
    }

    String getKaufdatum() {
        return lebensmittel.getEinkaufsdatum();
    }
    String getHerkunftsland() {
        return lebensmittel.getHerkunftsland();
    }

    int getKühlung() {
        return lebensmittel_info.getKühlungszuschlag();
    }

    int getMenge() { return lebensmittel.getMenge();}

    int getGrundhaltbarkeit() {
        return lebensmittel_info.getGrundhaltbarkeit();
    }

    double getEthylen() {
        return lebensmittel_info.getEthylen();
    }
}
