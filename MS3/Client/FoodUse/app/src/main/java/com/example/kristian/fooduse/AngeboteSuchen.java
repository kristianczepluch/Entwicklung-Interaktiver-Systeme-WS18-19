package com.example.kristian.fooduse;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import android.widget.ProgressBar;
import android.widget.Toast;
import android.app.IntentService;
import android.content.Intent;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.net.HttpURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.Inflater;

import static com.android.volley.VolleyLog.TAG;

public class AngeboteSuchen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MobilitätsDialog.mobilitaetsdialogListener, LocationListener, RadiusDialog.radiusdialogListener {

    private ImageButton hamburger_btn;
    private DrawerLayout drawerLayout;
    private Button angebotErstellenBtn;
    private static ArrayAdapter adapter;
    public static ArrayList<Angebot> angebot_eintraege = new ArrayList<>();
    private static ListView listView;
    private String mobility = "Fuss";
    private String uid;
    private Double lat;
    private Double lon;
    private String mRadius = "10";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth firebase_auth;
    private int LOCATION_PERMISSION_CODE = 1;
    private String absPath;
    private ProgressBar progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angebote_suchen);
        firebase_auth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }

        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        lat = location.getLatitude();
        lon = location.getLongitude();

        Toolbar toolbar = findViewById(R.id.toolbar_angebote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        hamburger_btn = findViewById(R.id.hamburgermenu_angebotesuchen);
        drawerLayout = findViewById(R.id.drawer2);
        hamburger_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
                drawerLayout.bringToFront();
            }
        });

        angebotErstellenBtn = findViewById(R.id.angebotadd_btn);
        angebotErstellenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AngebotErstellen.class);
                startActivity(intent);
            }
        });

        NavigationView sidebarNavigation = findViewById(R.id.angebotesuchen_sidebarnav);
        sidebarNavigation.setNavigationItemSelectedListener(this);

        // An dieser Stelle wird die Bottom Navigation für die Activity erstellt
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation2);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        listView = findViewById(R.id.angebote_suchen_listview);
        adapter = new AngebotEintragAdapter(this, angebot_eintraege, listView);
        listView.setAdapter(adapter);
        angebot_eintraege.clear();
        adapter.clear();
        adapter.notifyDataSetChanged();
        progressDialog = findViewById(R.id.angebote_suchen_progressBar);
        progressDialog.setVisibility(View.VISIBLE);
        AngeboteAbfragen();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Angebot geklicktes_angebot = (Angebot) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Ausgewählt wurde: " + geklicktes_angebot.getTitel(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), DetailedAngebot.class);
                intent.putExtra("AngebotsID", geklicktes_angebot.getId());
                intent.putExtra("Uid", geklicktes_angebot.getUid());
                intent.putExtra("Titel", geklicktes_angebot.getTitel());
                intent.putExtra("Abholzeitpunkt", geklicktes_angebot.getAbholzeitpunkt());
                intent.putExtra("Beschreibung", geklicktes_angebot.getBeschreibung());
                intent.putExtra("Ablaufdatum", geklicktes_angebot.getAblaufdatum());
                intent.putExtra("Einschraenkungen", geklicktes_angebot.getEinschreankungen());
                intent.putExtra("Bild", geklicktes_angebot.getBild());
                intent.putExtra("Längengrad", geklicktes_angebot.getLaengengrad());
                intent.putExtra("Breitengrad", geklicktes_angebot.getBreitengrad());
                intent.putExtra("Zeit", geklicktes_angebot.getZeit());
                intent.putExtra("Entfernung", geklicktes_angebot.getEntfernung());
                intent.putExtra("Lebensmittel", geklicktes_angebot.getLebensmittel());
                startActivity(intent);
            }
        });

        //BroadcastReceiver zum erhalten von Responses bei gesendeten Requests
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("ANGEBOTE SUCHEN"));
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("from");
            if(status.equals("OK")){
                angebot_eintraege.clear();
                adapter.notifyDataSetChanged();
                try {
                    String angebotsliste = intent.getStringExtra("angebote");
                    Log.d("ANGEBOTESUCHEN", "Response erhalten: " + angebotsliste);

                    //Angebote für die ListView erstellen und einfügen
                    JSONArray angebotelistJsonArray = null;
                    try {
                        angebotelistJsonArray = new JSONArray(angebotsliste);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < angebotelistJsonArray.length(); i++) {
                        try {
                            JSONObject tmpObj = angebotelistJsonArray.getJSONObject(i);
                            String img = tmpObj.getString("Bild");
                            if (!img.equals("Kein Bild")) {
                                // Schauen ob das Angebot ein Bild hat und wenn ja muss es für die Darstellung erst im FS gespeichert werden, um Adapter daraus zugreifen zu können
                                byte[] decodedString = Base64.decode(tmpObj.getString("Bild"), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                // BILD SPEICHERN
                                String filename = "Bild" + i;
                                String string = tmpObj.getString("Bild");
                                FileOutputStream outputStream = null;
                                try {
                                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                byte[] decodedBytes = Base64.decode(string, 0);
                                try {
                                    outputStream.write(decodedBytes);
                                    outputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                absPath = AngeboteSuchen.this.getFilesDir().getAbsolutePath() + "/" + filename;

                                // Benötigt sind:  uid,zeit,titel,abholzeitpunkt,lebensmittel,ablaufdatum,beschreibung, laengengrad,breitengrad,einschränkungen,abholwert,entfernung
                                adapter.add(new Angebot(tmpObj.getString("_id"), tmpObj.getString("Uid"), tmpObj.getString("Zeit"), tmpObj.getString("Titel"), tmpObj.getString("Abholzeitpunkt")
                                        , tmpObj.getString("Lebensmittel"), tmpObj.getString("Ablaufdatum"), tmpObj.getString("Beschreibung"), tmpObj.getDouble("Längengrad"),
                                        tmpObj.getDouble("Breitengrad"), tmpObj.getString("Einschränkungen"), tmpObj.getInt("Abholwert"), tmpObj.getString("Entfernung"), absPath));
                                Log.d("BILD", "Bild gespeichert unter: " + absPath);
                            } else {
                                adapter.add(new Angebot(tmpObj.getString("_id"), tmpObj.getString("Uid"), tmpObj.getString("Zeit"), tmpObj.getString("Titel"), tmpObj.getString("Abholzeitpunkt")
                                        , tmpObj.getString("Lebensmittel"), tmpObj.getString("Ablaufdatum"), tmpObj.getString("Beschreibung"), tmpObj.getDouble("Längengrad"),
                                        tmpObj.getDouble("Breitengrad"), tmpObj.getString("Einschränkungen"), tmpObj.getInt("Abholwert"), tmpObj.getString("Entfernung"), "Kein Bild"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        absPath = "";
                    }
                    progressDialog.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (status.equals("NOCONNECTION")) {
                progressDialog.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Es kann keine Internet Verbindung aufgebaut werden!", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Es liegt ein Serverseitiges Problem vor..", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.itemangebote1:
                progressDialog.setVisibility(View.VISIBLE);
                AngeboteNachAbholwertAbfragen();
                break;
            case R.id.itemangebote2:
                MobilitätsDialog edialog = new MobilitätsDialog();
                edialog.show(getSupportFragmentManager(),"mdialog");
                break;
            case R.id.itemangebote4:
                RadiusDialog rdialog = new RadiusDialog();
                rdialog.show(getSupportFragmentManager(),"rdialog");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.angebotetoolbar,menu);
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

    @Override
    public void applyTexts(boolean fuss_ergebnis, boolean fahrrad_ergebnis, boolean auto_ergebnis) {
        if(fuss_ergebnis){
            Toast.makeText(getApplicationContext(), "Die Angebote wurden neu berechnet!", Toast.LENGTH_SHORT).show();
            mobility = "Fuss";
            // Angebote neu anfragen mit neuer Mobilität
            angebot_eintraege.clear();
            progressDialog.setVisibility(View.VISIBLE);
            AngeboteAbfragen();

        }

        if(fahrrad_ergebnis){
            Toast.makeText(getApplicationContext(), "Die Angebote wurden neu berechnet!", Toast.LENGTH_SHORT).show();
            mobility = "Fahrrad";
            angebot_eintraege.clear();
            // Angebote neu anfragen mit neuer Mobilität
            progressDialog.setVisibility(View.VISIBLE);
            AngeboteAbfragen();
        }
        if(auto_ergebnis){
            Toast.makeText(getApplicationContext(), "Die Angebote wurden neu berechnet!", Toast.LENGTH_SHORT).show();
            mobility = "Auto";
            angebot_eintraege.clear();
            // Angebote neu anfragen mit neuer Mobilität
            progressDialog.setVisibility(View.VISIBLE);
            AngeboteAbfragen();
        }
    }

    public void AngeboteAbfragen(){
            angebot_eintraege.clear();
            adapter.clear();
            adapter.notifyDataSetChanged();

            Intent myIntent = new Intent(getApplicationContext(), com.example.kristian.fooduse.HTTPUrlConnectionService.class);
            String url = getString(R.string.Domainadress) + "/Angebote/User/" + uid + "/" + lon + "/" + lat + "/" + mobility + "/" + mRadius;
            myIntent.putExtra("payload","");
            myIntent.putExtra("method","GET");
            myIntent.putExtra("from","ANGEBOTE SUCHEN");
            myIntent.putExtra("url",url);
            startService(myIntent);
    }

    public void AngeboteNachAbholwertAbfragen(){
        angebot_eintraege.clear();
        adapter.clear();
        adapter.notifyDataSetChanged();

        Intent myIntent = new Intent(getApplicationContext(), com.example.kristian.fooduse.HTTPUrlConnectionService.class);
        String url = getString(R.string.Domainadress) + "/Angebote/User/" + uid + "/" + lon + "/" + lat + "/" + mobility + "/" + mRadius + "?sortBy=" + "Abholwert" ;
        myIntent.putExtra("payload","");
        myIntent.putExtra("method","GET");
        myIntent.putExtra("from","ANGEBOTE SUCHEN");
        myIntent.putExtra("url",url);
        startService(myIntent);
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void applyTexts(String radius) {
        mRadius = radius;
        progressDialog.setVisibility(View.VISIBLE);
        AngeboteAbfragen();
    }
}



