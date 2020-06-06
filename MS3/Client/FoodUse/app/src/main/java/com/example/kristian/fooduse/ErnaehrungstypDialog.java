package com.example.kristian.fooduse;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ErnaehrungstypDialog extends AppCompatDialogFragment {
    private CheckBox keine;
    private CheckBox vegan;
    private CheckBox vegetarisch;
    private CheckBox pescetarisch;
    private CheckBox kein_alkohol;
    private ernaehrungsdialogListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Checkboxes adressieren
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.ernaehrungstyp_dialog,null);
        keine = view.findViewById(R.id.ernaerhungstyp_keine_checkbox);
        vegan = view.findViewById(R.id.ernaerhungstyp_vegan_checkbox);
        vegetarisch = view.findViewById(R.id.ernaerhungstyp_vegetarisch_checkbox);
        pescetarisch = view.findViewById(R.id.ernaerhungstyp_pescetarisch_checkbox);
        kein_alkohol = view.findViewById(R.id.ernaerhungstyp_alkoholfrei_checkbox);

        builder.setView(view)
                .setTitle("Ernährungstyp wählen")
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean keine_ergebnis = keine.isChecked();
                        boolean vegan_ergebnis = vegan.isChecked();
                        boolean vegetarisch_ergebnis = vegetarisch.isChecked();
                        boolean pescetarisch_ergebnis = pescetarisch.isChecked();
                        boolean kein_alkohol_ergebnis = kein_alkohol.isChecked();
                        listener.applyTexts(keine_ergebnis, vegan_ergebnis, vegetarisch_ergebnis, pescetarisch_ergebnis, kein_alkohol_ergebnis);
                    }
                });
        return builder.create();
    }
    public interface ernaehrungsdialogListener{
        void applyTexts(boolean keine_ergebnis, boolean vegan_ergebnis, boolean vegetarisch_ergebnis, boolean pescetarisch_ergebnis, boolean kein_alkohol_ergebnis );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ernaehrungsdialogListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
