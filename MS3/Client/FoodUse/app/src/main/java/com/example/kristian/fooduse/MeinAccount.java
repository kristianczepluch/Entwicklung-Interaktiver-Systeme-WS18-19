package com.example.kristian.fooduse;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeinAccount extends AppCompatActivity {

    private EditText vorname_edit;
    private EditText nachname_edit;
    private EditText strasse_edit;
    private EditText nr_edit;
    private EditText stadt_edit;
    private ImageButton edit_btn;
    private ImageButton save_btn;
    private String userPath = "/User";
    private String uid;
    private FirebaseAuth mAuth;
    private CircleImageView img;
    private ProgressBar progressBar;
    JSONObject mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mein_account);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        progressBar = findViewById(R.id.progressBar_mein_account);
        progressBar.setVisibility(View.INVISIBLE);
        benutzerDatenAbfragen();
        save_btn = findViewById(R.id.account_save_btn);
        edit_btn = findViewById(R.id.account_edit_btn);
        vorname_edit = findViewById(R.id.account_vorname_edit);
        nachname_edit = findViewById(R.id.account_nachname_edit);
        strasse_edit = findViewById(R.id.account_Strasse_edit);
        nr_edit = findViewById(R.id.account_nr_edit);
        stadt_edit = findViewById(R.id.account_stadt_edit);
        img = findViewById(R.id.account_bild);
        save_btn.setVisibility(View.INVISIBLE);
        vorname_edit.setEnabled(false);
        nachname_edit.setEnabled(false);
        strasse_edit.setEnabled(false);
        nr_edit.setEnabled(false);
        stadt_edit.setEnabled(false);
        save_btn.setEnabled(false);

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_btn.setVisibility(View.INVISIBLE);
                save_btn.setVisibility(View.VISIBLE);
                vorname_edit.setEnabled(true);
                nachname_edit.setEnabled(true);
                strasse_edit.setEnabled(true);
                nr_edit.setEnabled(true);
                stadt_edit.setEnabled(true);
                save_btn.setEnabled(true);
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vorname_edit.setEnabled(false);
                nachname_edit.setEnabled(false);
                strasse_edit.setEnabled(false);
                nr_edit.setEnabled(false);
                stadt_edit.setEnabled(false);
                save_btn.setEnabled(false);

                // PUT mit neuen Daten
                String vorname = vorname_edit.getText().toString().trim();
                String nachname = nachname_edit.getText().toString().trim();
                String strasse = strasse_edit.getText().toString().trim();
                String nr = nr_edit.getText().toString().trim();
                String stadt = stadt_edit.getText().toString().trim();

                JSONObject benutzer = new JSONObject();
                try {
                    benutzer.put("Vorname", vorname);
                    benutzer.put("Nachname", nachname);
                    benutzer.put("Breitengrad", mUser.getString("Längengrad"));
                    benutzer.put("Längengrad", mUser.getString("Breitengrad"));
                    benutzer.put("Straße", strasse);
                    benutzer.put("Hausnummer", nr);
                    benutzer.put("Stadt", stadt);
                    benutzer.put("Uid", uid);
                    benutzer.put("Email", mUser.getString("Email"));
                    BenutzerDatenUpdaten(benutzer);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                save_btn.setVisibility(View.INVISIBLE);
                edit_btn.setVisibility(View.VISIBLE);
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("ACCOUNT BENUTZER"));


    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("from");
            if(status.equals("OK")){
                try {
                    String user = intent.getStringExtra("user");
                    JSONArray response = new JSONArray(user);
                    mUser = response.getJSONObject(0);
                    vorname_edit.setText(mUser.getString("Vorname"));
                    nachname_edit.setText(mUser.getString("Nachname"));
                    strasse_edit.setText(mUser.getString("Straße"));
                    nr_edit.setText(mUser.getString("Hausnummer"));
                    stadt_edit.setText(mUser.getString("Stadt"));

                    byte[] decodedString = Base64.decode(mUser.getString("Bild"), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    img.setImageBitmap(decodedByte);
                    progressBar.setVisibility(View.INVISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if(status.equals("NOCONNECTION")){
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Es kann keine Internet Verbindung zum Server aufgebaut werden!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void benutzerDatenAbfragen(){
        Intent myIntent = new Intent(getApplicationContext(), com.example.kristian.fooduse.HTTPUrlConnectionService.class);
        String url = getString(R.string.Domainadress) + userPath + "/" + uid;
        myIntent.putExtra("payload","");
        myIntent.putExtra("method","GET");
        myIntent.putExtra("from","BENUTZER ACCOUNT");
        myIntent.putExtra("url",url);
        startService(myIntent);
    }

    public void BenutzerDatenUpdaten(JSONObject obj){
        Intent myIntent = new Intent(getApplicationContext(), com.example.kristian.fooduse.HTTPUrlConnectionService.class);
        String url = getString(R.string.Domainadress) + userPath + "/" + uid;
        myIntent.putExtra("payload",obj.toString());
        myIntent.putExtra("method","PUT");
        myIntent.putExtra("from","BENUTZER ACCOUNT");
        myIntent.putExtra("url",url);
        startService(myIntent);

    }
}
