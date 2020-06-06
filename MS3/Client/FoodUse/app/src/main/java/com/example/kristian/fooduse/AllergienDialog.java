package com.example.kristian.fooduse;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

public class AllergienDialog extends AppCompatDialogFragment {

    private CheckBox keine;
    private CheckBox nuss;
    private allergiendialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Checkboxes adressieren
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.allergien_dialog,null);
        keine = view.findViewById(R.id.allergien_keine_checkbox);
        nuss = view.findViewById(R.id.allergien_nuss_checkbox);

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
                        boolean nuss_ergebnis = nuss.isChecked();
                        listener.applyTexts(keine_ergebnis, nuss_ergebnis);
                    }
                });
        return builder.create();
    }
    public interface allergiendialogListener{
        void applyTexts(boolean keine_ergebnis, boolean nuss_ergebnis);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (allergiendialogListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
