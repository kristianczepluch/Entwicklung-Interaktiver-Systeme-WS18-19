package com.example.kristian.fooduse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;


class EintragAdapter extends ArrayAdapter<LebensmittelEintrag> {

    Switch kuehlschrank;
    EintragAdapter(Context context, ArrayList <LebensmittelEintrag> lebensmittel) {
        super(context,R.layout.eintrag_row,lebensmittel);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View eintragrow = LayoutInflater.from(getContext()).inflate(R.layout.eintrag_row,parent,false);

        LebensmittelEintrag einzelnesLebensmittel = getItem(position);
        TextView lebensmittel = (TextView) eintragrow.findViewById(R.id.essenseintrag_edittext);
        TextView ablaufdatum = (TextView) eintragrow.findViewById(R.id.ablaufdatum_edittext);
        kuehlschrank = eintragrow.findViewById(R.id.kuehlschrank_switch);

        lebensmittel.setText(einzelnesLebensmittel.getName());
        ablaufdatum.setText(einzelnesLebensmittel.getAblaufdatum().toString());
        kuehlschrank.setChecked(einzelnesLebensmittel.isKühlung());
        return eintragrow;
    }

    public Switch getSwitch(){
        return kuehlschrank;
    }

}
