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

public class EinschraenkungenDialog extends AppCompatDialogFragment {

    private CheckBox keine;
    private CheckBox laktose;
    private CheckBox gluten;
    private CheckBox nieren;
    private CheckBox molke;
    private CheckBox eifrei;
    private einschraenkungsdialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.einschreankungen_dialog,null);

        keine = view.findViewById(R.id.einschraenkungen_keine_checkbox);
        laktose = view.findViewById(R.id.einschraenkungen_laktose_checkbox);
        gluten = view.findViewById(R.id.einschraenkungen_glutenfrei_checkbox);
        nieren = view.findViewById(R.id.einschraenkungen_nierenfreundlich_checkbox);
        molke = view.findViewById(R.id.einschraenkungen_Molkeunversträglichkeit_checkbox);
        eifrei = view.findViewById(R.id.einschraenkungen_eifrei_checkbox);

        builder.setView(view)
                .setTitle("Einschränkungen wählen")
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean keine_ergebnis = keine.isChecked();
                        boolean laktose_ergebnis = laktose.isChecked();
                        boolean gluten_ergebnis = gluten.isChecked();
                        boolean nieren_ergebnis = nieren.isChecked();
                        boolean molke_ergebnis = molke.isChecked();
                        boolean eifrei_ergebnis = eifrei.isChecked();
                        listener.applyTexts(keine_ergebnis, laktose_ergebnis, gluten_ergebnis, nieren_ergebnis, molke_ergebnis,eifrei_ergebnis);
                    }
                });
        return builder.create();
    }
    public interface einschraenkungsdialogListener{
        void applyTexts(boolean keine_ergebnis, boolean laktose_ergebnis, boolean gluten_ergebnis, boolean nieren_ergebnis, boolean molke_ergebnis, boolean eifrei_ergebnis );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (einschraenkungsdialogListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
