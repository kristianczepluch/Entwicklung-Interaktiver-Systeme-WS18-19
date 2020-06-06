package com.example.kristian.fooduse;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrierenActivity extends AppCompatActivity implements LocationListener {

    private String email;
    private String passwort;
    private String vorname;
    private String nachname;
    private String strasse;
    private String hausnr;
    private String stadt;
    private EditText email_editText;
    private EditText passwort_editText;
    private EditText vorname_editText;
    private EditText nachname_editText;
    private EditText strasse_editText;
    private EditText hausnr_editText;
    private EditText stadt_editText;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Double lat;
    private Double lon;
    private int LOCATION_PERMISSION_CODE = 1;
    private String uid;
    private SharedPreferences prefs;
    private FirebaseAuth firebase_auth;
    private Button registrierenButton;
    private String pathUser = "/User";
    private CircleImageView img;
    private String encoded = "Kein Bild";
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrieren);
        firebase_auth = FirebaseAuth.getInstance();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("BENUTZER_ERSTELLEN"));
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
        vorname_editText = findViewById(R.id.vorname_editText);
        nachname_editText = findViewById(R.id.nachname_editText);
        strasse_editText = findViewById(R.id.strasse_editText);
        hausnr_editText = findViewById(R.id.hausnummer_editText);
        stadt_editText = findViewById(R.id.stadt_edittext);
        progressBar = findViewById(R.id.registrieren_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        email_editText = findViewById(R.id.email_edittext);
        passwort_editText = findViewById(R.id.passwort_edittext);
        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        lat = location.getLatitude();
        lon = location.getLongitude();
        img = findViewById(R.id.profile_img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });

        registrierenButton = findViewById(R.id.registrieren_btn);
        registrierenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                email = email_editText.getText().toString().trim();
                passwort = passwort_editText.getText().toString().trim();
                vorname = vorname_editText.getText().toString().trim();
                nachname = nachname_editText.getText().toString().trim();
                strasse = strasse_editText.getText().toString().trim();
                hausnr = hausnr_editText.getText().toString().trim();
                stadt = stadt_editText.getText().toString().trim();



                firebase_auth.createUserWithEmailAndPassword(email, passwort)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    mAuth.signInWithEmailAndPassword(email, passwort)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {

                                                        // Benutzer konnte erfolgreich erstellt und eingeloggt werden nun müssen die Daten auf den Server gelangen
                                                        currentUser = mAuth.getCurrentUser();
                                                        currentUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    String firebaseToken = FirebaseInstanceId.getInstance().getToken();
                                                                    String uid = currentUser.getUid();

                                                                    JSONObject mytoken = new JSONObject();

                                                                    try {
                                                                        mytoken.put("Uid", uid);
                                                                        mytoken.put("Token", firebaseToken);
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                    Intent myIntent = new Intent(getApplicationContext(), com.example.kristian.fooduse.HTTPUrlConnectionService.class);
                                                                    JSONObject benutzer = new JSONObject();
                                                                    String url = getString(R.string.Domainadress) + pathUser;

                                                                    try {
                                                                        benutzer.put("Uid", uid);
                                                                        benutzer.put("Vorname", vorname);
                                                                        benutzer.put("Nachname", nachname);
                                                                        benutzer.put("Breitengrad", lat);
                                                                        benutzer.put("Längengrad", lon);
                                                                        benutzer.put("Straße", strasse);
                                                                        benutzer.put("Hausnummer", hausnr);
                                                                        benutzer.put("Stadt", stadt);
                                                                        benutzer.put("Email", email);
                                                                        benutzer.put("Token", mytoken);
                                                                        benutzer.put("Bild", encoded);
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    myIntent.putExtra("payload", benutzer.toString());
                                                                    myIntent.putExtra("method", "POST");
                                                                    myIntent.putExtra("from", "BENUTZER_ERSTELLEN");
                                                                    myIntent.putExtra("url", url);
                                                                    startService(myIntent);
                                                                }
                                                            }
                                                        });

                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Email und Passwort stimmen nicht überein", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    String err = "Ein Fehler ist aufgetreten";
                                    if((task.getException().getMessage()).contains("network")){
                                        err = "Internet Verbindung notwendig!";
                                    }
                                    if((task.getException().getMessage()).contains("email")){
                                        err = "Bitte eine gültige Email angeben!";
                                    }
                                    if((task.getException().getMessage()).contains("password")){
                                        err = "Das Passwort ist zu schwach! Mindestens 6 Zeichen sind notwenig!";
                                    }


                                    Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        img.setImageBitmap(bitmap);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("pic", encoded);
        edit.apply();


    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String result = intent.getStringExtra("from");
            if(result.equals("OK")){
                progressBar.setVisibility(View.INVISIBLE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("eingeloggt", true);
                editor.commit();
                Intent inventarIntent = new Intent(getApplicationContext(), Hauptbildschirm.class);
                startActivity(inventarIntent);
                Toast.makeText(getApplicationContext(), "Willkommen!", Toast.LENGTH_SHORT).show();
            } else if(result.equals("FAIL")){
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Bitte alle Felder korrekt ausfüllen!", Toast.LENGTH_SHORT).show();
                mAuth.getCurrentUser().delete();
            } else if(result.equals("NOCONNECTION")) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Es kann keine Internet Verbindung zum Server aufgebaut werden!", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                mAuth.getCurrentUser().delete();
                Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        lat = location.getLatitude();
        lon = location.getLongitude();
    }

}
