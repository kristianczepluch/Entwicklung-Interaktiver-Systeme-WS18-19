package com.example.kristian.fooduse;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

public class RadiusDialog extends AppCompatDialogFragment {


    private radiusdialogListener listener;
    private TextInputEditText radiusInput;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Checkboxes adressieren
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.radius_dialog,null);
        radiusInput = view.findViewById(R.id.radius_inputText);

        builder.setView(view)
                .setTitle("Radius wählen (in Km)")
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = radiusInput.getText().toString();
                        listener.applyTexts(input);
                    }
                });
        return builder.create();
    }
    public interface radiusdialogListener{
        void applyTexts(String radius);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (radiusdialogListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
