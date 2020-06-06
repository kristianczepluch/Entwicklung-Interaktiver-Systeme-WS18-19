package com.example.kristian.fooduse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static ListView lebensmittel_listview;
    private Button next_btn;
    private Button hinzufuegen_btn;
    private static ArrayAdapter adapter;
    private static ArrayList<LebensmittelEintrag> lebensmittel_eintraege = new ArrayList<>();

    public static Date datum = new Date();
    private static ArrayList<Lebensmittel> alle_lebensmittel = new ArrayList<Lebensmittel>();
    private static ArrayList<LebensmittelVerarbeitungsObjekt> verarbeitungsobjekte_ethylen = new ArrayList<LebensmittelVerarbeitungsObjekt>();
    private static ArrayList<LebensmittelVerarbeitungsObjekt> verarbeitungsobjekte_ohne_ethylen = new ArrayList<LebensmittelVerarbeitungsObjekt>();
    private static ArrayList<LebensmittelEintrag> lebensmittel_einträge_ohne_ethylen = new ArrayList<LebensmittelEintrag>();
    private static ArrayList<LebensmittelEintrag> lebensmittel_einträge_mit_ethylen = new ArrayList<LebensmittelEintrag>();
    private static ArrayList<LebensmittelEintrag> lebensmittel_einträge_gekühlt = new ArrayList<LebensmittelEintrag>();
    private static ArrayList<LebensmittelEintrag> lebensmittel_einträge_ungekühlt = new ArrayList<LebensmittelEintrag>();
    private static ArrayList<LebensmittelEintrag> lebensmittel_einträge_verarbeitung_gekühlt = new ArrayList<LebensmittelEintrag>();
    private static ArrayList<LebensmittelEintrag> lebensmittel_einträge_verarbeitung_ungekühlt = new ArrayList<LebensmittelEintrag>();
    private static Datenbank db = new Datenbank();
    private static Distanzrechner distanzrechner = new Distanzrechner();
    private static Haltbarkeitsrechner haltbarkeitsrechner = new Haltbarkeitsrechner();
    // Ende

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lebensmittel);
        final Activity activity = this;

        // Zuweisen der View Elemente, um sie adressieren zu können
        lebensmittel_listview = findViewById(R.id.lebensmittel_listview);
        next_btn = findViewById(R.id.next_btn);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StandortErmittlung.class);
                startActivity(intent);
            }
        });

        // Dies ist ein onClickListener für die Liste. Immer wenn ein Item gedrückt wird ändert sich der Switch für die Kühlung die Haltbarkeitsdaten werden neu berechnet
        // Durch die berechneAnhandUserInputMethode();
        lebensmittel_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LebensmittelEintrag a = (LebensmittelEintrag) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "item " + a.getName() + "Was Clicked", Toast.LENGTH_LONG).show();
                if(a.isKühlung()){
                    a.setKühlung(false);
                } else a.setKühlung(true);
                adapter.notifyDataSetChanged();
                berechneAnhandUserInput(lebensmittel_eintraege);
                adapter.notifyDataSetChanged();

            }
        });

        // Dieser Button startet den QR code Scanner
        hinzufuegen_btn = findViewById(R.id.hinzufuegen_btn);
        hinzufuegen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        // Dies ist lediglich eine Animation um Lebensmittel durch wischen aus der Liste zu entfernen.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lebensmittel_listview,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    lebensmittel_eintraege.remove(position);
                                    Toast.makeText(getApplicationContext(), "item removed", Toast.LENGTH_LONG).show();
                                    adapter.notifyDataSetChanged();

                                }


                            }
                        });
        lebensmittel_listview.setOnTouchListener(touchListener);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode,data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            } else {
                // Nachdem erfolgreich aus dem QR-Code Scanner gelesen wurde wird der input geparst und eine Liste alle_lebensmittel erstellt welche die Eingabe enthält.
                // Im Anschluss werden die Haltbarkeitsdaten anhand der besten einstellungen berechnet durch berechneOptimal();
                Toast.makeText(this,result.getContents(),Toast.LENGTH_LONG).show();
                String input = result.getContents();
                String [] tokens = input.split("/");
                String datum = tokens[0];
                alle_lebensmittel.clear();
                for(int i=1; i<tokens.length; i++){
                    String[] splittedInfos = tokens[i].split("-");
                    alle_lebensmittel.add(new Lebensmittel(splittedInfos[0],splittedInfos[1],datum));
                    berechneOptimal(alle_lebensmittel);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


        super.onActivityResult(requestCode, resultCode, data);

    }

    void berechneOptimal(ArrayList<Lebensmittel> alle_lebensmittel){
        // Zunächst werden alle Lebensmittel die bereits im Kühlschrank sind mit in die Liste aufgenommen.
        for(LebensmittelEintrag it : lebensmittel_eintraege){
            alle_lebensmittel.add(new Lebensmittel((it.getName()),it.getHerkunftsort(),it.getKaufdatum()));
        }
        int mit_ethylen = 0;
        int ohne_ethylen = 0;
        // Für jedes Lebensmittel werden nun die nötigen Informationen abgefragt.
        // Dies wird simuliert durch die Klasse DB
        for(Lebensmittel item : alle_lebensmittel) {
            if(db.getLebensmittelInfo(item.getName()).getEthylen()>0) {
                mit_ethylen++;
                verarbeitungsobjekte_ethylen.add(new LebensmittelVerarbeitungsObjekt(item, db.getLebensmittelInfo(item.getName())));
            } else {
                verarbeitungsobjekte_ohne_ethylen.add(new LebensmittelVerarbeitungsObjekt(item, db.getLebensmittelInfo(item.getName())));
                ohne_ethylen++;
            }
        }

        // Nachdem die Lebensmittel emhr Infos haben und nach Ethylen/Kein Ethylen sortiert wurden. Werden die Lebensmittel und die abgefragten Infos verwendet
        // Um ein Haltbarkeitdatum zu berechnen
        // Die Berechnung selbst findet in der klasse haltbarkeitrechner statt.
        lebensmittel_einträge_ohne_ethylen = haltbarkeitsrechner.berechneOhneEthylen(verarbeitungsobjekte_ohne_ethylen,mit_ethylen,ohne_ethylen);
        lebensmittel_einträge_mit_ethylen = haltbarkeitsrechner.berechneMitEthylen(verarbeitungsobjekte_ethylen,mit_ethylen,ohne_ethylen);
        lebensmittel_eintraege.clear();
        for(LebensmittelEintrag i : lebensmittel_einträge_ohne_ethylen) {

            lebensmittel_eintraege.add(i);
        }
        for(LebensmittelEintrag i : lebensmittel_einträge_mit_ethylen) {
            lebensmittel_eintraege.add(i);
        }
        lebensmittel_einträge_mit_ethylen.clear();
        lebensmittel_einträge_ohne_ethylen.clear();
        verarbeitungsobjekte_ethylen.clear();
        verarbeitungsobjekte_ohne_ethylen.clear();

        // Zum Schluss werden alle Lebensmittel der Liste für Einträge hinzugefügt und die Liste wird aktualisiert.

        // Ende
        adapter = new EintragAdapter(getApplicationContext(),lebensmittel_eintraege);
        lebensmittel_listview.setAdapter(adapter);
        alle_lebensmittel.clear();

    }

    void berechneAnhandUserInput(ArrayList<LebensmittelEintrag> lebensmittel_eintraege){
        int mit_ethylen_gekühlt = 0;
        int mit_ethylen_ungekühlt = 0;

        // MainActivity werden ahand gekühlt bzw. ungekühlt sortiert, um sie weiter verarbeiten zu können.
        for(LebensmittelEintrag it : lebensmittel_eintraege){
            if(it.isKühlung()){
                if((db.getLebensmittelInfo(it.getName()).getEthylen())>0){
                    mit_ethylen_gekühlt++;
                }
                lebensmittel_einträge_verarbeitung_gekühlt.add(it);
            } else {
                if ((db.getLebensmittelInfo(it.getName()).getEthylen()) > 0) {
                    mit_ethylen_ungekühlt++;
                }
                lebensmittel_einträge_verarbeitung_ungekühlt.add(it);
            }
            }


            lebensmittel_einträge_gekühlt = haltbarkeitsrechner.berechneImKühlschrank(lebensmittel_einträge_verarbeitung_gekühlt,mit_ethylen_gekühlt);
            lebensmittel_einträge_ungekühlt =haltbarkeitsrechner.berechneAusserhabKühlschrank(lebensmittel_einträge_verarbeitung_ungekühlt,mit_ethylen_ungekühlt);

            lebensmittel_eintraege.clear();
            lebensmittel_eintraege.addAll(lebensmittel_einträge_ungekühlt);
            lebensmittel_eintraege.addAll(lebensmittel_einträge_gekühlt);

            lebensmittel_einträge_gekühlt.clear();
            lebensmittel_einträge_ungekühlt.clear();
            lebensmittel_einträge_verarbeitung_gekühlt.clear();
            lebensmittel_einträge_verarbeitung_ungekühlt.clear();

        }
    }


