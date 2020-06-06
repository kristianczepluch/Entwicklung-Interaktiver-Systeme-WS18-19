package com.example.kristian.fooduse;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;


class EintragAdapter extends ArrayAdapter<LebensmittelEintrag> {
    public View eintragrow;
    Switch kuehlschrank;
    ListView lv;
    ArrayList <LebensmittelEintrag> aktuelleListe;
    TextView menge;
    Context mContext;
    ImageButton imgBtn;
    int position;
    int aktuelleMenge;
    String aktuellerName;
    int aktuelleID;

    EintragAdapter(Context context, ArrayList <LebensmittelEintrag> lebensmittel, ListView lv) {
        super(context,R.layout.eintrag_row,lebensmittel);
        this.aktuelleListe = lebensmittel;
        mContext = context;

    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        eintragrow = LayoutInflater.from(getContext()).inflate(R.layout.cardviewlebensmittel,parent,false);

        LebensmittelEintrag einzelnesLebensmittel = getItem(position);
        TextView lebensmittel = (TextView) eintragrow.findViewById(R.id.essenseintrag_edittext1);
        TextView ablaufdatum = (TextView) eintragrow.findViewById(R.id.ablaufdatum_edittext1);
        kuehlschrank = eintragrow.findViewById(R.id.kuehlschrank_switch1);
        ImageView img = eintragrow.findViewById(R.id.Lebensmitteleintrag_img);
        img.setImageResource(R.drawable.frische_lebensmittel_icon);
        imgBtn = eintragrow.findViewById(R.id.ImgBtnLebensmittel);
        imgBtn.setImageResource((R.drawable.ic_more_vert_black_24dp));
        imgBtn.setTag(position);
        menge = eintragrow.findViewById(R.id.mengentxt);
        menge.setText(Integer.toString(einzelnesLebensmittel.getMenge()));

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {



                final int position = (Integer) v.getTag();
                LebensmittelEintrag l = aktuelleListe.get(position);
                aktuelleMenge = Integer.parseInt(menge.getText().toString());
                aktuellerName = l.getName();
                aktuelleID = l.getID();

                PopupMenu popup = new PopupMenu(mContext, v);
                popup.inflate(R.menu.lebensmittel_popupmenu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getItemId()){
                            case R.id.item11:
                                Toast.makeText(eintragrow.getContext(), "Menge verringern wurde gedrückt! Aktuelle Menge: " + aktuellerName , Toast.LENGTH_LONG).show();
                                LebensmittelEintrag l = ((Hauptbildschirm)mContext).lebensmittel_eintraege.get(position);
                                int aktuelleMenge = l.getMenge();
                                if((aktuelleMenge-1)==0){
                                    ((Hauptbildschirm)mContext).lebensmittel_eintraege.remove(position);
                                    ((Hauptbildschirm)mContext).deleteData(LebensmittelContract.LebensmittelDbEintrag.TABLE_NAME, aktuelleID);
                                    ((Hauptbildschirm)mContext).berechneOptimal(new ArrayList<Lebensmittel>());
                                    return true;
                                } else {
                                    ContentValues cv = new ContentValues();
                                    cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_NAME, l.getName());
                                    cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_MENGE, l.getMenge() - 1);
                                    cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_ABLAUFDATUM, l.getAblaufdatum().toString());
                                    cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_HERKUNFTSORT, l.getHerkunftsort());
                                    cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KAUFDATUM, l.getKaufdatum());
                                    cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KÜHLUNG, l.isKühlung());
                                    ((Hauptbildschirm) mContext).updateLebensmittelEintrag(cv, l.getID());
                                    l.setMenge(l.getMenge() - 1);
                                    ((Hauptbildschirm) mContext).berechneOptimal(new ArrayList<Lebensmittel>());
                                    return true;
                                }

                            case R.id.item12:
                                try {
                                    ((Hauptbildschirm)mContext).lebensmittel_eintraege.remove(position);
                                    ((Hauptbildschirm)mContext).deleteData(LebensmittelContract.LebensmittelDbEintrag.TABLE_NAME, aktuelleID);
                                    ((Hauptbildschirm)mContext).berechneOptimal(new ArrayList<Lebensmittel>());
                                    notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return true;

                            case R.id.item13:
                                Toast.makeText(eintragrow.getContext(), "Eintrag anbieten wurde gedrückt!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(mContext, AngebotErstellen.class);
                                LebensmittelEintrag la = ((Hauptbildschirm)mContext).lebensmittel_eintraege.get(position);
                                intent.putExtra("Name", la.getName());
                                intent.putExtra("Menge",la.getMenge());
                                SimpleDateFormat dateFormat = new SimpleDateFormat( "dd.MM.yyyy" );
                                String ablaufdatumString = dateFormat.format(la.getAblaufdatum());
                                intent.putExtra("Ablaufdatum",ablaufdatumString);
                                mContext.startActivity(intent);
                                return true;

                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        SimpleDateFormat dateFormat = new SimpleDateFormat( "dd.MM.yyyy" );
        lebensmittel.setText(einzelnesLebensmittel.getName());
        String ablaufdatumAlsString = dateFormat.format(einzelnesLebensmittel.getAblaufdatum());

        String formatiertesAblaufdatum = ablaufdatumAlsString.substring(0, 10);
        ablaufdatum.setText(ablaufdatumAlsString);
        kuehlschrank.setChecked(einzelnesLebensmittel.isKühlung());
        kuehlschrank.setEnabled(false);
        return eintragrow;
    }

    public Switch getSwitch(){
        return kuehlschrank;
    }


}

