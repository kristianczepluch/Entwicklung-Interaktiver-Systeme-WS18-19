package com.example.kristian.fooduse;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

public class MobilitätsDialog extends AppCompatDialogFragment {

    private RadioButton fuss;
    private RadioButton fahrrad;
    private RadioButton auto;
    private mobilitaetsdialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Checkboxes adressieren
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.mobilitaet_dialog,null);
        fuss = view.findViewById(R.id.fuss_radioBtn);
        fahrrad = view.findViewById(R.id.fahhrad_radioBtn);
        auto = view.findViewById(R.id.auto_radioBtn);

        fuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fahrrad.setChecked(false);
                auto.setChecked(false);
            }
        });

        fahrrad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fuss.setChecked(false);
                auto.setChecked(false);
            }
        });

        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fuss.setChecked(false);
                fahrrad.setChecked(false);
            }
        });
        builder.setView(view)
                .setTitle("Mobilität wählen")
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean fuss_ergebnis = fuss.isChecked();
                        boolean fahrrad_ergebnis = fahrrad.isChecked();
                        boolean auto_ergebnis = auto.isChecked();

                        listener.applyTexts(fuss_ergebnis, fahrrad_ergebnis, auto_ergebnis);
                    }
                });
        return builder.create();
    }
    public interface mobilitaetsdialogListener{
        void applyTexts(boolean fuss_ergebnis, boolean fahrrad_ergebnis,boolean auto_ergebnis);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (mobilitaetsdialogListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
