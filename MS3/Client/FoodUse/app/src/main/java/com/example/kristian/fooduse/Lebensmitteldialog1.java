package com.example.kristian.fooduse;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

/*
Diese Klasse stellt, den ersten Dialog dar, welcher aufgerufen wird, sobald der hinzufügen Button in der Activity
Hauptbildschirm gedrückt wird. An dieser Stelle wird ebenfalls das Interface dialogListener1 erstellt, welche die Kommunikation
zwischen der Activity und dem Dialog erlaubt, um zu übermitteln, welcher Button gedrückt wurde.
 */

public class Lebensmitteldialog1 extends AppCompatDialogFragment {

     private ImageButton qrbutton;
     private ImageButton manuellButton;
     public dialogListener1 listener;
     public boolean ergebnis;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.lebensmittel_dialog,null);
        qrbutton = view.findViewById(R.id.qr_imageButton);
        manuellButton = view.findViewById(R.id.manuell_btn);
        manuellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ergebnis=false;
                listener.apply(ergebnis);
                getDialog().dismiss();
            }
        });

        qrbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ergebnis = true;
                listener.apply(ergebnis);
                getDialog().dismiss();
            }
        });
        builder.setView(view);
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (dialogListener1)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must " +
                    "must implement ExampleDialogListener");
        }
    }

    public interface dialogListener1{
        void apply(boolean ergebnis);
    }

}
