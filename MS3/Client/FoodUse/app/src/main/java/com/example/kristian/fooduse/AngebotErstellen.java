package com.example.kristian.fooduse;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class AngebotErstellen extends AppCompatActivity implements LocationListener {


    private Button erstellen_button;
    private TextInputEditText titel;
    private TextInputEditText lebensmittel;
    private TextInputEditText menge;
    private TextInputEditText beschreibung;
    private TextInputEditText abholzeitpunkt;
    private TextInputEditText ablaufdatum;
    private ImageView fertiges_bild;
    private ImageButton photoButton;
    private ImageView hintergrund;
    private String encoded = "Kein Bild";
    private Bitmap img;
    private Button testButton1;
    private Button testButton2;
    private Double lat;
    private Double lon;
    private FirebaseAuth mAuth;
    private FirebaseAuth firebase_auth;
    private int LOCATION_PERMISSION_CODE = 1;
    private String uid;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angebot_erstellen);

        firebase_auth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        progressBar = findViewById(R.id.angebot_erstellen_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }

        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        lat = location.getLatitude();
        lon = location.getLongitude();

        ablaufdatum = findViewById(R.id.ablaufdatum_text_angeboteerstellen);
        erstellen_button = findViewById(R.id.angeboterstellen_btn);
        titel = findViewById(R.id.angebottitel_text);
        abholzeitpunkt = findViewById(R.id.abholzeitpunkt_text);
        lebensmittel = findViewById(R.id.angebotname_text);
        menge = findViewById(R.id.angebotmenge_text);
        beschreibung = findViewById(R.id.angebotbeschreibung_text);
        Intent erhaltenerIntent = getIntent();
        int mengeIntent = erhaltenerIntent.getIntExtra("Menge", 10000);
        if(!(mengeIntent == 10000)){
        menge.setText(String.valueOf(mengeIntent));}
        lebensmittel.setText(getIntent().getStringExtra("Name"));
        ablaufdatum.setText(getIntent().getStringExtra("Ablaufdatum"));
        photoButton = findViewById(R.id.photoButton);
        hintergrund = findViewById(R.id.hintergrundbild);
        fertiges_bild = findViewById(R.id.BildVomAngebot);
        fertiges_bild.setVisibility(View.INVISIBLE);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("user"));
        erstellen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String angebotsTitel = titel.getText().toString();
                String angebotsAbholzeitpunkt = abholzeitpunkt.getText().toString();
                String angebotsLebensmittel = lebensmittel.getText().toString();
                String angebotsAblaufdatum = ablaufdatum.getText().toString();
                String angebotsBeschreibung = beschreibung.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                erstelleAngebot(uid, angebotsTitel, angebotsAbholzeitpunkt, angebotsLebensmittel, angebotsAblaufdatum, angebotsBeschreibung, lat.toString(), lon.toString(), encoded );

            }
        });
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intent2 = new Intent(getApplicationContext(), AngeboteSuchen.class);

            try {
               String s = intent.getStringExtra("from");
                if (s.equals("FAIL")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Bitte alle Felder ausfülen!", Toast.LENGTH_SHORT).show();
                    return;
                }else if(s.equals("OK")){
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Angebot wurde erstellt!", Toast.LENGTH_SHORT).show();
                    startActivity(intent2);
                }
                else if(s.equals("NOCONNECTION")){
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Es kann keine Internet Verbindung aufgebaut werden!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public void erstelleAngebot(String uid, String titel, String abholzeitpunkt, String lebensmittel, String ablaufdatum, String beschreibung, String lat, String lon, String bild){
        String URL = getString(R.string.Domainadress) + "/Angebote";
        JSONObject angebot = new JSONObject();
        try {
            angebot.put("Uid",uid);
            angebot.put("Status","offen");
            angebot.put("Titel", titel);
            angebot.put("Abholzeitpunkt",abholzeitpunkt);
            angebot.put("Lebensmittel", lebensmittel);
            angebot.put("Ablaufdatum", ablaufdatum);
            angebot.put("Beschreibung",beschreibung);
            angebot.put("Längengrad", lon);
            angebot.put("Breitengrad",lat);
            angebot.put("Bild", bild);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent myIntent = new Intent(getApplicationContext(), com.example.kristian.fooduse.HTTPUrlConnectionService.class);
        //Paramter fuer den eigenen HTTP Service definieren und starten
        myIntent.putExtra("payload",angebot.toString());
        myIntent.putExtra("method","POST");
        myIntent.putExtra("from","ANGEBOT_ERSTELLEN");
        myIntent.putExtra("url",URL);
        startService(myIntent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) 300) / width;
        float scaleHeight = ((float) 300) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);

        fertiges_bild.setImageBitmap(resizedBitmap);
        photoButton.setVisibility(View.INVISIBLE);
        fertiges_bild.setVisibility(View.VISIBLE);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Log.d("BILDINBASE", encoded);

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
}






