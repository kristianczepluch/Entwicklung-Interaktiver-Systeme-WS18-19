package com.example.kristian.fooduse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class DetailedAngebot extends AppCompatActivity implements OnMapReadyCallback {
    private String uid;
    private String zeit;
    private String titel;
    private String abholzeitpunkt;
    private String lebensmittel;
    private String ablaufdatum;
    private String beschreibung;
    private Double laengengrad;
    private Double breitengrad;
    private String entfernung;
    private String einschreankungen;
    private int abholwert;
    private String id;
    private String bild;
    private ImageView imgView;
    private TextView titel_textView;
    private TextView beschreibung_textView;
    private TextView menge_textView;
    private TextView abholzeitpunkt_textView;
    private TextView einschraenkungen_textView;
    private TextView entfernung_textView;
    private TextView haltbarkeitsdatum_textView;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_angebot);
        // Alle Informationen, welche beim starten der Activity mitgesendet wurden aufgreifen.
        Intent intent = getIntent();
        uid = intent.getExtras().getString("Uid");
        id = intent.getExtras().getString("AngebotsID");
        zeit = intent.getExtras().getString("Zeit");
        titel = intent.getExtras().getString("Titel");
        abholzeitpunkt = intent.getExtras().getString("Abholzeitpunkt");
        lebensmittel = intent.getExtras().getString("Lebensmittel");
        beschreibung = intent.getExtras().getString("Beschreibung");
        ablaufdatum = intent.getExtras().getString("Ablaufdatum");
        beschreibung = intent.getExtras().getString("Beschreibung");
        laengengrad = intent.getExtras().getDouble("Längengrad");
        breitengrad = intent.getExtras().getDouble("Breitengrad");
        entfernung = intent.getExtras().getString("Entfernung");
        einschreankungen = intent.getExtras().getString("Einschraenkungen");
        bild = intent.getExtras().getString("Bild");
        imgView = findViewById(R.id.collapsing_bar_imageview);

        // View an die Informationen anpassen
        titel_textView = findViewById(R.id.detailed_titel);
        beschreibung_textView = findViewById(R.id.details_beschreibung);
        einschraenkungen_textView = findViewById(R.id.details_einschraenkungen);
        abholzeitpunkt_textView = findViewById(R.id.details_abholzeitpunkt);
        entfernung_textView = findViewById(R.id.details_entfernung);
        haltbarkeitsdatum_textView = findViewById(R.id.details_halt);

        // Google Maps Karte laden
        initMap();

        // Bild anpassen
        if(!(bild.equals("Kein Bild")) && !bild.isEmpty()) {
            File imageFile = new File(bild);
            byte[] bytes = new byte[(int) imageFile.length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(imageFile));
                DataInputStream duf = new DataInputStream(buf);
                duf.readFully(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imgView.setImageBitmap(img);
        } else{
            imgView.setImageResource(R.drawable.frische_lebensmittel_icon);
        }
        titel_textView.setText(titel);
        beschreibung_textView.setText(beschreibung);

        einschraenkungenPasen(einschreankungen);

        abholzeitpunkt_textView.setText(abholzeitpunkt);
        Double e = Double.parseDouble(entfernung);
        e = round(e,2);
        entfernung_textView.setText(Double.toString(e) + " Km");
        haltbarkeitsdatum_textView.setText(ablaufdatum);


    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(DetailedAngebot.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(new LatLng(laengengrad,breitengrad)).title(titel));
        LatLng position = new LatLng(breitengrad,laengengrad);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(17).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void einschraenkungenPasen(String s){
        int c = 0;
        if(s.contains("VEGAN")){
            einschraenkungen_textView.append("Vegan, ");
            ++c;
        }
        if(s.contains("VEGETARIAN")){
            einschraenkungen_textView.append("Vegetarisch, ");
            ++c;
        }
        if(s.contains("PEANUT_FREE")){
            einschraenkungen_textView.append("Erdnussfrei, ");
            ++c;
        }
        if(s.contains("TREE_NUT_FREE")){
            einschraenkungen_textView.append("Baumnussfrei, ");
            ++c;
        }
        if(s.contains("ALCOHOL_FREE")){
            einschraenkungen_textView.append("Alkoholfrei, ");
            ++c;
        }
        if(c==1){
            String eintrag = removeLastChar(einschraenkungen_textView.getText().toString());
            einschraenkungen_textView.setText(eintrag);

        }
    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }
}
