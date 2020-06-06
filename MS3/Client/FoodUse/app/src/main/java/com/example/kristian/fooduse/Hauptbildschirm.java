package com.example.kristian.fooduse;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Hauptbildschirm extends AppCompatActivity implements Lebensmitteldialog1.dialogListener1, NavigationView.OnNavigationItemSelectedListener {

    // Variablen für die Berechnungen des Halbarkeitsdatums
    private static ListView lebensmittel_listview;
    private Button hinzufuegen_btn;
    private static ArrayAdapter adapter;
    public static ArrayList<LebensmittelEintrag> lebensmittel_eintraege = new ArrayList<>();
    String uid;

    // Dies sind verschiedene Listen, welche für die Berechnung der Haltbarkeit benötigt werden.
    public static ArrayList<Lebensmittel> alle_lebensmittel = new ArrayList<Lebensmittel>();
    private static ArrayList<LebensmittelVerarbeitungsObjekt> verarbeitungsobjekte_ethylen = new ArrayList<LebensmittelVerarbeitungsObjekt>();
    private static ArrayList<LebensmittelVerarbeitungsObjekt> verarbeitungsobjekte_ohne_ethylen = new ArrayList<LebensmittelVerarbeitungsObjekt>();
    private static ArrayList<LebensmittelEintrag> lebensmittel_einträge_ohne_ethylen = new ArrayList<LebensmittelEintrag>();
    private static ArrayList<LebensmittelEintrag> lebensmittel_einträge_mit_ethylen = new ArrayList<LebensmittelEintrag>();
    private static ArrayList<LebensmittelEintrag> lebensmittel_einträge_gekühlt = new ArrayList<LebensmittelEintrag>();
    private static ArrayList<LebensmittelEintrag> lebensmittel_einträge_ungekühlt = new ArrayList<LebensmittelEintrag>();
    private static ArrayList<LebensmittelEintrag> lebensmittel_einträge_verarbeitung_gekühlt = new ArrayList<LebensmittelEintrag>();
    private static ArrayList<LebensmittelEintrag> lebensmittel_einträge_verarbeitung_ungekühlt = new ArrayList<LebensmittelEintrag>();
    private FirebaseAuth mAuth;

    // Variablen für die Datenbank
    private static Datenbank db;
    private static Haltbarkeitsrechner haltbarkeitsrechner;
    private static SQLiteDatabase mDatabase, readdb;
    // Variablen für die Navigation
    DrawerLayout drawerLayout;
    ImageButton hamburgerBtn;

    final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hauptbildschirm);
        db = new Datenbank(this);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();


        haltbarkeitsrechner = new Haltbarkeitsrechner(this);
        /* Zunächst werden hier die einzelenen View Elemente adressiert, um im späteren
        Verlauf mit diesen arbeiten zu können. Desweiteren wird der Datenbank Helfer und die
        Datenbank initialisert, um die LebensmittelEinträge speichern zu können.
         */
        lebensmittel_listview = findViewById(R.id.lebensmittel_inventar_listview);
        hinzufuegen_btn = findViewById(R.id.lebensmittelAdd_btn);
        drawerLayout = findViewById(R.id.drawer1);
        NavigationView sidebarNavigation = findViewById(R.id.hauptbildschirm_sidebarnav);
        sidebarNavigation.setNavigationItemSelectedListener(this);
        hamburgerBtn = findViewById(R.id.imageButton5);
        hamburgerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
                drawerLayout.bringToFront();
            }
        });
        LebensmittelDBHelper dbHelper = new LebensmittelDBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();
        readdb = dbHelper.getReadableDatabase();
        lebensmittel_eintraege = alleLebensmiitelEinträgeAuslesen();
        adapter = new EintragAdapter(this, lebensmittel_eintraege, lebensmittel_listview);
        lebensmittel_listview.setAdapter(adapter);

        //An dieser Stelle wird die Bottom Navigation erstellt um zu anderen Activites gelangen zu können.
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        /*
        Die ArrayListe lebensmittel_eintaege beeinhaltet immer alle aktuellen Lebensmitteleinträge und wird
        mit Hilfe eines Customs Adapters (adapter) an die ListView mit den Einträgen gebunden. Sollten
        veränderungen in der Liste eintreten, so muss sowohl das Array, als auch die Datenbank aktualisert werden.
         */

        lebensmittel_eintraege = alleLebensmiitelEinträgeAuslesen();
        adapter = new EintragAdapter(this, lebensmittel_eintraege, lebensmittel_listview);
        Toolbar toolbar = findViewById(R.id.toolbar_inventar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        /*
         Dieser Button startet einen Dialog, indem der Benutzer auswählen kann ob er die Eingabe
         manuell oder mit Hilfe des QR-Reader erledigen will.
          */
        hinzufuegen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        /*
        Falls auf ein Item innerhalb der ListView gedrückt wird, so wird diese Funktion getriggert und
        die Kühlung wird verändert. Hierzu wird der entsprechende LebensmittelEintrag manipuliert
        (sowohl im Adapter als auch in der Datenbank) und zum Schluss die Haltbarkeitsdaten anhand des User
        Inputs berechnet, da nun ggf. ethylenhaltige und nicht ethylenhaltige Lebensmittel zusammen gelagert werden.
         */
        lebensmittel_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LebensmittelEintrag a = (LebensmittelEintrag) parent.getItemAtPosition(position);
                if(a.isKühlung()){
                    a.setKühlung(false);
                } else a.setKühlung(true);
                ContentValues cv = new ContentValues();
                cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_NAME, a.getName());
                cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_MENGE, a.getMenge());
                cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_ABLAUFDATUM, a.getAblaufdatum().toString());
                cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_HERKUNFTSORT, a.getHerkunftsort());
                cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KAUFDATUM, a.getKaufdatum());
                cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KÜHLUNG, a.isKühlung());
                updateLebensmittelEintrag(cv, a.getID());
                lebensmittel_eintraege = alleLebensmiitelEinträgeAuslesen();
                adapter = new EintragAdapter(getApplicationContext(), alleLebensmiitelEinträgeAuslesen(), lebensmittel_listview);
                adapter.notifyDataSetChanged();
                berechneAnhandUserInput(alleLebensmiitelEinträgeAuslesen());

            }
        });
        /*
        Dies ist ebenfalls ein Listener für die ListView, welcher eine zusätzliche Swipe Animation beeinhaltet,
        um die Menge der Lebensmittel zu verringern bzw. das Lebensmittel zu entfernen, falls die Menge = 0 ist.
        Hierzu wird die ArrayList und die Datenbank manipuliert und im Anschluss wird die optimale Lagerung für die
        restlichen Lebensmittel berechnet, da sich diese ggf. verändert hat.
         */
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

                                    lebensmittel_eintraege = alleLebensmiitelEinträgeAuslesen();
                                    int aktuelleMenge = lebensmittel_eintraege.get(position).getMenge();
                                    int ID = lebensmittel_eintraege.get(position).getID();
                                    aktuelleMenge--;
                                    if(aktuelleMenge==0){
                                        deleteData(LebensmittelContract.LebensmittelDbEintrag.TABLE_NAME, ID);
                                        lebensmittel_eintraege.remove(position);
                                    } else {
                                        ContentValues cv = new ContentValues();
                                        cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_NAME, lebensmittel_eintraege.get(position).getName());
                                        cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_MENGE, aktuelleMenge);
                                        cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_ABLAUFDATUM, lebensmittel_eintraege.get(position).getAblaufdatum().toString());
                                        cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_HERKUNFTSORT, lebensmittel_eintraege.get(position).getHerkunftsort());
                                        cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KAUFDATUM, lebensmittel_eintraege.get(position).getKaufdatum());
                                        cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KÜHLUNG, lebensmittel_eintraege.get(position).isKühlung());
                                        mDatabase.update(LebensmittelContract.LebensmittelDbEintrag.TABLE_NAME, cv, "_ID=" + ID, null);
                                        lebensmittel_eintraege.get(position).setMenge(aktuelleMenge);
                                    }
                                    alle_lebensmittel.clear();
                                    berechneOptimal(alle_lebensmittel);

                                }


                            }
                        });
        lebensmittel_listview.setOnTouchListener(touchListener);

    }

    /*
     Die apply Methode wird aufgerufen, falls der Benutzer sich für die Eingabe via QR-Code entschieden hat.
     der QR-Code wird gestartet und falls das auslesen erfolgreich durchgeführt wurde, wird die Methode on Activity
     Result ausgelöst. (Activity Lifecycle)
      */
    @Override
    public void apply(boolean ergebnis) {
        if(ergebnis) {
            IntentIntegrator integrator = new IntentIntegrator(activity);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("QR-Code einscannen!");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
        }
    }

    // Diese Methode startet einen Custom dialog. Der Dialog wird in der Klasse Lebensmitteldialog1 festgelegt.
    private void openDialog() {
        Lebensmitteldialog1 ersterDialog = new Lebensmitteldialog1();
        ersterDialog.show(getSupportFragmentManager(), "ersterDialog");
    }

    // Diese Methode erstellt das Menü nach der Vorlage im Menü Ordner.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inventartoolbar,menu);
        return true;
    }
    // An dieser Stelle wird ein OnClickListener für die Navigation erstellt, welche zu den anderen Activities navigiert.
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch(menuItem.getItemId()){
                case R.id.nav_home:
                    Intent intentMeinInventar = new Intent(getApplicationContext(), Hauptbildschirm.class);
                    startActivity(intentMeinInventar);
                    break;
                case R.id.nav_angebote:
                    Intent intentMeineAngebote = new Intent(getApplicationContext(), AngeboteSuchen.class);
                    startActivity(intentMeineAngebote);
                    break;
                case R.id.nav_reservierungen:
                    Toast.makeText(getApplicationContext(), "Dieser Bereich wurde leider nicht implementiert!", Toast.LENGTH_LONG).show();
                    break;
                case R.id.nav_benachrichtigungen:
                    Toast.makeText(getApplicationContext(), "Dieser Bereich wurde leider nicht implementiert!", Toast.LENGTH_LONG).show();
                    break;
            }
            return true;
        }
    };



    /*
    Dies ist die Methode, welche getriggert wird sobald das QR-Code auslesen erfolgreich durchgeführt wurde.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode,data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "Das Scannen wurde abgebrochen", Toast.LENGTH_LONG).show();
            } else {
                // Nachdem erfolgreich aus dem QR-Code Scanner gelesen wurde wird der input geparst und eine Liste alle_lebensmittel erstellt welche die Eingabe enthält.
                // Im Anschluss werden die Haltbarkeitsdaten anhand der besten einstellungen berechnet durch berechneOptimal();
                String input = result.getContents();
                String [] tokens = input.split("/");
                String datum = tokens[0];
                alle_lebensmittel.clear();
                for(int i=1; i<tokens.length; i++){
                    String[] splittedInfos = tokens[i].split(",");
                    alle_lebensmittel.add(new Lebensmittel(splittedInfos[0],splittedInfos[1],datum,Integer.parseInt(splittedInfos[2])));
                }
                // Lebensmittel Einträge erstellen für die Vorlieben Berechnung auf der Server Seite
                entreageErstellen(alle_lebensmittel);
                // Optimale Lagerung und Haltbarkeitsdaten berechnen
                berechneOptimal(alle_lebensmittel);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


        super.onActivityResult(requestCode, resultCode, data);

    }
    void berechneOptimal(ArrayList<Lebensmittel> alle_lebensmittel){
        // Zunächst werden alle Lebensmittel die bereits im Kühlschrank sind mit in die Liste aufgenommen.
        for(LebensmittelEintrag it : lebensmittel_eintraege){
            alle_lebensmittel.add(new Lebensmittel((it.getName()),it.getHerkunftsort(),it.getKaufdatum(), it.getMenge()));
        }

        lebensmittel_eintraege.clear();

        // Diese Variablen geben die Menge der eingelesnen Lebensmittel an mit/ohne Ethylen
        int mit_ethylen = 0;
        int ohne_ethylen = 0;

        // Für jedes Lebensmittel werden nun die nötigen Informationen abgefragt.
        for(Lebensmittel item : alle_lebensmittel) {
            if(db.getLebensmittelInfo(item.getName()).getEthylen()>0) {
                mit_ethylen+=item.getMenge();
                verarbeitungsobjekte_ethylen.add(new LebensmittelVerarbeitungsObjekt(item, db.getLebensmittelInfo(item.getName())));
            } else {
                verarbeitungsobjekte_ohne_ethylen.add(new LebensmittelVerarbeitungsObjekt(item, db.getLebensmittelInfo(item.getName())));
                ohne_ethylen+=item.getMenge();
            }
        }

        /* Durch die Summe der Lebensmittel wird entschieden, wie die Haltbarkeitsdaten berechnet werden sollen.
           Eine grafische Darstellung dieses Vorgehens ist in der Projektdokumentation: Anwendungslogik Client Ablaufdiagramm zu finden.
         */
        int summe = mit_ethylen + ohne_ethylen;

        // Da nun die Lagerung und das Haltbarkeitsdatum für alle Lebensmittel neu berechnet werden muss, werden alle Einträge in der Datenbank gelöscht.
        mDatabase.delete(LebensmittelContract.LebensmittelDbEintrag.TABLE_NAME, null, null);

         if(summe==1) {
            if (mit_ethylen == 1) {
                lebensmittel_einträge_ohne_ethylen = haltbarkeitsrechner.berechneOhneEthylenMitKühlung(verarbeitungsobjekte_ethylen);
                for (LebensmittelEintrag i : lebensmittel_einträge_ohne_ethylen) {
                    lebensmittel_eintraege.add(i);
                }
                lebensmittel_einträge_ohne_ethylen.get(0);
                // Einträge der Datenbank hinzufügen
                for(LebensmittelEintrag item: lebensmittel_eintraege){
                    lebensmittelEintragInDbSchreiben(item);
                }
                // Für die nächsten Berechnungsvorgänge werden alle Listen geflusht.
                adapter = new EintragAdapter(this, alleLebensmiitelEinträgeAuslesen(), lebensmittel_listview);
                lebensmittel_listview.setAdapter(adapter);
                alle_lebensmittel.clear();
                lebensmittel_einträge_mit_ethylen.clear();
                lebensmittel_einträge_ohne_ethylen.clear();
                verarbeitungsobjekte_ethylen.clear();
                verarbeitungsobjekte_ohne_ethylen.clear();
            } else {

                lebensmittel_einträge_mit_ethylen = haltbarkeitsrechner.berechneOhneEthylenMitKühlung(verarbeitungsobjekte_ohne_ethylen);
                for (LebensmittelEintrag i : lebensmittel_einträge_mit_ethylen) {
                    lebensmittel_eintraege.add(i);
                }
                // Einträge der Datenbank hinzufügen
                for(LebensmittelEintrag item: lebensmittel_eintraege){
                    lebensmittelEintragInDbSchreiben(item);
                }
                adapter = new EintragAdapter(this, alleLebensmiitelEinträgeAuslesen(), lebensmittel_listview);
                lebensmittel_listview.setAdapter(adapter);
                // Für die nächsten Berechnungsvorgänge werden alle Listen geflusht.
                alle_lebensmittel.clear();
                lebensmittel_einträge_mit_ethylen.clear();
                lebensmittel_einträge_ohne_ethylen.clear();
                verarbeitungsobjekte_ethylen.clear();
                verarbeitungsobjekte_ohne_ethylen.clear();
            }

        } else if(summe>1){
            if(mit_ethylen==0){
                // Liste ohne Ethylen in den Kühlschrank (Grundhaltbarkeit + Kühlung - Transport)
                lebensmittel_einträge_ohne_ethylen = haltbarkeitsrechner.berechneOhneEthylenMitKühlung(verarbeitungsobjekte_ohne_ethylen);
                for (LebensmittelEintrag i : lebensmittel_einträge_ohne_ethylen) {
                    lebensmittel_eintraege.add(i);
                }
                // Einträge der Datenbank hinzufügen
                for(LebensmittelEintrag item: lebensmittel_eintraege){
                    lebensmittelEintragInDbSchreiben(item);
                }

                adapter = new EintragAdapter(this, alleLebensmiitelEinträgeAuslesen(), lebensmittel_listview);
                lebensmittel_listview.setAdapter(adapter);
                // Für die nächsten Berechnungsvorgänge werden alle Listen geflusht.
                alle_lebensmittel.clear();
                lebensmittel_einträge_mit_ethylen.clear();
                lebensmittel_einträge_ohne_ethylen.clear();
                verarbeitungsobjekte_ethylen.clear();
                verarbeitungsobjekte_ohne_ethylen.clear();
            } else if(ohne_ethylen==0){
                // Liste mit Ethylen in den Kühlschrank (Grundhaltbarkeit + Kühlung - Transport) *Ethylen
                lebensmittel_einträge_mit_ethylen = haltbarkeitsrechner.berechneMitEthylenMitKühlung(verarbeitungsobjekte_ethylen);
                for (LebensmittelEintrag i : lebensmittel_einträge_mit_ethylen) {
                    lebensmittel_eintraege.add(i);
                }
                // Einträge der Datenbank hinzufügen
                for(LebensmittelEintrag item: lebensmittel_eintraege){
                    lebensmittelEintragInDbSchreiben(item);
                }
                adapter = new EintragAdapter(this, alleLebensmiitelEinträgeAuslesen(), lebensmittel_listview);
                lebensmittel_listview.setAdapter(adapter);
                // Für die nächsten Berechnungsvorgänge werden alle Listen geflusht.
                alle_lebensmittel.clear();
                lebensmittel_einträge_mit_ethylen.clear();
                lebensmittel_einträge_ohne_ethylen.clear();
                verarbeitungsobjekte_ethylen.clear();
                verarbeitungsobjekte_ohne_ethylen.clear();
            } else{
                // Die Lebensmittel werden getrennt voneinander berechnet und in einer Liste zusammengeführt.
                lebensmittel_einträge_ohne_ethylen = haltbarkeitsrechner.berechneOhneEthylenMitKühlung(verarbeitungsobjekte_ohne_ethylen);
                if(mit_ethylen>1){
                    lebensmittel_einträge_mit_ethylen=haltbarkeitsrechner.berechneMitEthylenOhneKühlung(verarbeitungsobjekte_ethylen);
                } else {
                    lebensmittel_einträge_mit_ethylen=haltbarkeitsrechner.berechneOhneEthylenOhneKühlung(verarbeitungsobjekte_ethylen);
                }

                for (LebensmittelEintrag i : lebensmittel_einträge_mit_ethylen) {
                    lebensmittel_eintraege.add(i);
                }
                for (LebensmittelEintrag i : lebensmittel_einträge_ohne_ethylen) {
                    lebensmittel_eintraege.add(i);
                }

                // Einträge der Datenbank hinzufügen
                for(LebensmittelEintrag item: lebensmittel_eintraege){
                    lebensmittelEintragInDbSchreiben(item);
                }

                adapter = new EintragAdapter(this, alleLebensmiitelEinträgeAuslesen(), lebensmittel_listview);
                lebensmittel_listview.setAdapter(adapter);
                alle_lebensmittel.clear();
                lebensmittel_einträge_mit_ethylen.clear();
                lebensmittel_einträge_ohne_ethylen.clear();
                verarbeitungsobjekte_ethylen.clear();
                verarbeitungsobjekte_ohne_ethylen.clear();
            }

        }
        Toast.makeText(getApplicationContext(), "Die Haltbarkeitsdaten und optimalen Lagerbedingungen wurde ermittelt!", Toast.LENGTH_LONG).show();
    }

        // Gibt der Benutzer vor, wo er die Lebensmittel lagert, so wird strikt nach seinen angaben die Haltbarkeit berechnet.
        void berechneAnhandUserInput(ArrayList<LebensmittelEintrag> lebensmittel_eintraege){
            int mit_ethylen_gekühlt = 0;
            int ohne_ethylen_gekühlt = 0;
            int mit_ethylen_ungekühlt = 0;
            int ohne_ethylen_ungekühlt = 0;

            mDatabase.delete(LebensmittelContract.LebensmittelDbEintrag.TABLE_NAME, null, null);

            // MainActivity werden ahand gekühlt bzw. ungekühlt sortiert, um sie weiter verarbeiten zu können.
            for(LebensmittelEintrag it : lebensmittel_eintraege){
                if(it.isKühlung()){
                    if((db.getLebensmittelInfo(it.getName()).getEthylen())>0){
                        mit_ethylen_gekühlt+=it.getMenge();
                    }
                    else{
                        ohne_ethylen_gekühlt+=it.getMenge();
                    }
                    lebensmittel_einträge_verarbeitung_gekühlt.add(it);
                } else {
                    if ((db.getLebensmittelInfo(it.getName()).getEthylen()) > 0) {
                        mit_ethylen_ungekühlt+=it.getMenge();
                    }else{
                        ohne_ethylen_ungekühlt+=it.getMenge();
                    }
                    lebensmittel_einträge_verarbeitung_ungekühlt.add(it);
                }
            }

            lebensmittel_einträge_gekühlt = haltbarkeitsrechner.berechneImKühlschrank(lebensmittel_einträge_verarbeitung_gekühlt,mit_ethylen_gekühlt,ohne_ethylen_gekühlt);
            lebensmittel_einträge_ungekühlt =haltbarkeitsrechner.berechneAusserhabKühlschrank(lebensmittel_einträge_verarbeitung_ungekühlt,mit_ethylen_ungekühlt, ohne_ethylen_ungekühlt);

            lebensmittel_eintraege.clear();
            lebensmittel_eintraege.addAll(lebensmittel_einträge_ungekühlt);
            lebensmittel_eintraege.addAll(lebensmittel_einträge_gekühlt);

            for(LebensmittelEintrag item: lebensmittel_eintraege){
                Log.v("Debug","Item Ablaufdatum: " + item.getAblaufdatum());
                lebensmittelEintragInDbSchreiben(item);
            }
            adapter = new EintragAdapter(this, alleLebensmiitelEinträgeAuslesen(), lebensmittel_listview);
            lebensmittel_listview.setAdapter(adapter);
            lebensmittel_einträge_gekühlt.clear();
            lebensmittel_einträge_ungekühlt.clear();
            lebensmittel_einträge_verarbeitung_gekühlt.clear();
            lebensmittel_einträge_verarbeitung_ungekühlt.clear();

        }

    // Dies ist eine Methode, welche einem den Cursor aus der Datenbank mit den Lebensmittel zurückliefert.
    private static Cursor allesLesen(){
        return readdb.rawQuery("select * from " + LebensmittelContract.LebensmittelDbEintrag.TABLE_NAME, null);
    }
    // Diese Methode schreibt einen LebensmittelEintrag in die Datenbank.
    public void lebensmittelEintragInDbSchreiben(LebensmittelEintrag la){

        // Eintrag wird in einzelne Bestandteile aufgeteilt, um einen Eintrag in der Datenbank machen zu können.
        String name = la.getName();
        int menge = la.getMenge();
        int kühlung;
        if(la.isKühlung()){
            kühlung = 1;
        }else{
            kühlung = 0;
        }
        String herkunftsland = la.getHerkunftsort();
        String ablaufdatum = la.getAblaufdatum().toString();
        Log.v("Debug","LebensmittelEintragInDBSchreiben als: " + ablaufdatum);
        String kaufdatum = la.getKaufdatum();
        ContentValues cv = new ContentValues();
        cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_NAME, name);
        cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_MENGE, menge);
        cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_ABLAUFDATUM, ablaufdatum);
        cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_HERKUNFTSORT, herkunftsland);
        cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KAUFDATUM, kaufdatum);
        cv.put(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KÜHLUNG, kühlung);
        mDatabase.insert(LebensmittelContract.LebensmittelDbEintrag.TABLE_NAME,null, cv);
    }
    // Diese Methode liest alle Einträge aus der Datenbank und erstellt Objekte vom Typ LebensmittelEintrag und liefert eine Liste mit allen Objekten zurück.
    public static ArrayList<LebensmittelEintrag> alleLebensmiitelEinträgeAuslesen(){
        Cursor cursor = allesLesen();
        ArrayList<LebensmittelEintrag> alleEinträge = new ArrayList<>();

        String name;
        String kaufdatum;
        int menge;
        Date ablaufdatum;
        Boolean kühlung;
        String herkunftsort;
        String TempAblaufdatum;
        int ID;
        if (cursor.moveToFirst()){
            name = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_NAME));
            menge = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_MENGE));
            kaufdatum = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KAUFDATUM));
            TempAblaufdatum = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_ABLAUFDATUM));
            ID = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag._ID));
            Log.v("Debug","Ablaufdatum aus DB ausgelesen:" + TempAblaufdatum);
            try {
                ablaufdatum = new SimpleDateFormat(
                        "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(TempAblaufdatum);
            } catch (ParseException e) {
                e.printStackTrace();
                ablaufdatum = new Date();
            }

            int num = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KÜHLUNG));
            if(num==1){
                kühlung = true;
            } else {
                kühlung = false;
            }
            herkunftsort = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_HERKUNFTSORT));
            Log.v("Debug","Ablaufdatum aus DB ausgelesen NACH DEM PARSEN:" + ablaufdatum);
            alleEinträge.add(new LebensmittelEintrag(name, kaufdatum, ablaufdatum,  herkunftsort,  kühlung,  menge, ID));

            while(cursor.moveToNext()){
                name = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_NAME));
                menge = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_MENGE));
                kaufdatum = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KAUFDATUM));
                TempAblaufdatum = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_ABLAUFDATUM));
                ID = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag._ID));
                Log.v("Debug","Ablaufdatum aus DB ausgelesen:" + TempAblaufdatum);
                try {
                    ablaufdatum =new SimpleDateFormat(
                            "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(TempAblaufdatum);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                num = cursor.getInt(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_KÜHLUNG));
                if(num==1){
                    kühlung = true;
                } else {
                    kühlung = false;
                }
                Log.v("Debug","Ablaufdatum aus DB ausgelesen NACH DEM PARSEN:" + ablaufdatum);
                herkunftsort = cursor.getString(cursor.getColumnIndex(LebensmittelContract.LebensmittelDbEintrag.COLUMN_HERKUNFTSORT));
                alleEinträge.add(new LebensmittelEintrag(name, kaufdatum, ablaufdatum,  herkunftsort,  kühlung,  menge, ID));
            }
        } else {
        }

        return alleEinträge;
    }
    // Mit dieser Methode wird ein LebensmittelEintrag aus der Datenbank anhand seiner ID gelöscht.
    public void deleteData(String tableName, Integer id) {

        try {
            if (mDatabase != null) {
                mDatabase.execSQL("delete from " + LebensmittelContract.LebensmittelDbEintrag.TABLE_NAME + " Where _ID = " + id);
            }
        } catch (Exception _exception) {
            _exception.printStackTrace();
        }
    }

    // Mit dieser Methode wird ein Lebensmitteleintrag aus der Datenbank anhand seiner ID aktualisiert.
    public static void updateLebensmittelEintrag(ContentValues cv, int ID){
        mDatabase.update(LebensmittelContract.LebensmittelDbEintrag.TABLE_NAME, cv, "_ID=" + ID, null);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.meinaccount:
                Intent intentAccount = new Intent(getApplicationContext(), MeinAccount.class);
                startActivity(intentAccount);
                break;
            case R.id.ernährungsprofil:
                Intent intentErnährungsprofil = new Intent(getApplicationContext(), MeinErnaehrungsprofil.class);
                startActivity(intentErnährungsprofil);
                break;
            case R.id.meineangebote:
                Intent intentAngebote = new Intent(getApplicationContext(), MeineAngebote.class);
                startActivity(intentAngebote);
                break;
            case R.id.meinenachrichten:
                Toast.makeText(getApplicationContext(), "Dieser Bereich wurde leider nicht implementiert!", Toast.LENGTH_LONG).show();
                break;
            case R.id.rezeptefinden:
                Toast.makeText(getApplicationContext(), "Dieser Bereich wurde leider nicht implementiert!", Toast.LENGTH_LONG).show();
                break;
            case R.id.hilfe:
                Toast.makeText(getApplicationContext(), "Dieser Bereich wurde leider nicht implementiert!", Toast.LENGTH_LONG).show();
                break;
            case R.id.impressum:
                Toast.makeText(getApplicationContext(), "Dieser Bereich wurde leider nicht implementiert!", Toast.LENGTH_LONG).show();
                break;
            case R.id.logout:
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("eingeloggt", false);
                editor.commit();
                Intent intentMain = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intentMain);
                break;
            default:
                Toast.makeText(getApplicationContext(), "Navigation gedrückt!", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    public void entreageErstellen(ArrayList<Lebensmittel> alle_lebensmittel){
        for(Lebensmittel item: alle_lebensmittel){
            Log.d("DSDS", "eintraegeErstellen wurde getriggert.");
            Intent myIntent = new Intent(getApplicationContext(), com.example.kristian.fooduse.HTTPUrlConnectionService.class);
            JSONObject obj = new JSONObject();
            try {
                obj.put("Name", item.getName());
                obj.put("Anzahl", item.getMenge());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = getString(R.string.Domainadress) + "/User" + "/" + uid + "/Eintraege";
            myIntent.putExtra("payload",obj.toString());
            myIntent.putExtra("method","POST");
            myIntent.putExtra("from","BENUTZER EINTRAG");
            myIntent.putExtra("url",url);
            startService(myIntent);
        }
    }
}
