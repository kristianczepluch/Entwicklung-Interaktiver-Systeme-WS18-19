package com.example.kristian.fooduse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;


public class AngebotEintragAdapter extends ArrayAdapter<Angebot> {
    private String titel;
    private String entfernung;
    private String abholwert;
    private Context mContext;
    public View eintragrow;
    private TextView angebotTitel;
    private TextView angebotEntfernung;
    private TextView angebotAbholwert;


    AngebotEintragAdapter(Context context, ArrayList<Angebot> angebote, ListView lv) {
        super(context,R.layout.eintrag_row,angebote);
        mContext = context;

    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
        eintragrow = LayoutInflater.from(getContext()).inflate(R.layout.cardviewangebote,parent,false);

        Angebot einzelnesAngebot = getItem(position);

        // Bild laden falls vorhanden
        ImageView angebotsBild = eintragrow.findViewById(R.id.angebote_image);
        String pfad = einzelnesAngebot.getBild();
        if(!(pfad.equals("Kein Bild")) && !pfad.isEmpty()) {
            File imageFile = new File(pfad);
            byte[] bytes = new byte[(int) imageFile.length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(imageFile));
                DataInputStream duf = new DataInputStream(buf);
                duf.readFully(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            angebotsBild.setImageBitmap(img);
        } else{
            angebotsBild.setImageResource(R.drawable.frische_lebensmittel_icon);
        }


        angebotTitel = eintragrow.findViewById(R.id.angebot_titel);
        angebotEntfernung = eintragrow.findViewById(R.id.angebot_entfernung);
        angebotAbholwert = eintragrow.findViewById(R.id.abholwert_pkt_textView);
        titel = einzelnesAngebot.getTitel();
        entfernung = einzelnesAngebot.getEntfernung();
        abholwert = Integer.toString(einzelnesAngebot.getAbholwert());
        angebotAbholwert.setText(abholwert + " Pkt");
        angebotTitel.setText(titel);
        angebotEntfernung.setText("ca. " + (int) round(Double.parseDouble(entfernung), 2)*1000 +"m");
        return eintragrow;
    }

    // Funktion, um die Entfernung korrekt auf 2 Nachkommastellen gerundet darzustellen
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
