package com.example.kristian.fooduse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MeinErnaehrungsprofil extends AppCompatActivity implements ErnaehrungstypDialog.ernaehrungsdialogListener, EinschraenkungenDialog.einschraenkungsdialogListener, AllergienDialog.allergiendialogListener {
    private ImageButton ernaehrungstypBtn;
    private ImageButton allergienBtn;
    private ImageButton unverträglichkeitenBtn;
    private TextView ernaehrungstyp_liste;
    private TextView einschraenkungs_liste;
    private TextView allergien_liste;
    private String userPath = "/User";
    private String uid;
    private FirebaseAuth mAuth;
    private ArrayList<String> mList;
    private JSONArray obj;
    private String ausgeleseneEinsch;
    private JSONObject jobj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mein_ernaehrungsprofil);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        einschränkungenAbfragen();
        ernaehrungstypBtn = findViewById(R.id.ernaehrungstyp_button);
        allergienBtn = findViewById(R.id.allergien_btn);
        unverträglichkeitenBtn = findViewById(R.id.unvertraeglichkeiten_btn);
        ernaehrungstyp_liste = findViewById(R.id.ernaehrungstyp_textview_liste);
        einschraenkungs_liste = findViewById(R.id.unvertraeglichkeiten_textview_liste);
        allergien_liste = findViewById(R.id.allergien_textview_liste);

        ernaehrungstypBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dialog hier öffnen
                ErnaehrungstypDialog edialog = new ErnaehrungstypDialog();
                edialog.show(getSupportFragmentManager(),"edialog");
            }
        });

        allergienBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllergienDialog edialog = new AllergienDialog();
                edialog.show(getSupportFragmentManager(),"adialog");
            }
        });

        unverträglichkeitenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    EinschraenkungenDialog edialog = new EinschraenkungenDialog();
                    edialog.show(getSupportFragmentManager(),"eedialog");
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("BENUTZER EINSCHRAENKUNGEN"));
    }

    @Override
    public void applyTexts(boolean keine_ergebnis, boolean vegan_ergebnis, boolean vegetarisch_ergebnis, boolean pescetarisch_ergebnis, boolean kein_alkohol_ergebnis) {
        ernaehrungstyp_liste.setText("");
        if(keine_ergebnis){
            ernaehrungstyp_liste.append(" ");
        }

        if(vegan_ergebnis){
            boolean found = false;
            for(int i=0; i<obj.length()-1;i++){
                try {
                    jobj = obj.getJSONObject(i);
                    ausgeleseneEinsch = jobj.getString("Name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(ausgeleseneEinsch.equals("VEGAN")){ found = true; }
            }
            if(!found){
                einschränkungenEintragen("VEGAN");
            }
        } else {
            deleteEintrag("VEGAN");
        }

        if(vegetarisch_ergebnis){
            boolean found = false;
            for(int i=0; i<obj.length()-1;i++){
                try {
                    jobj = obj.getJSONObject(i);
                    ausgeleseneEinsch = jobj.getString("Name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(ausgeleseneEinsch.equals("VEGETARIAN")){ found = true; }
            }
            if(!found){
                einschränkungenEintragen("VEGETARIAN");
            }
        } else {
            deleteEintrag("VEGETARIAN");
        }

        if(pescetarisch_ergebnis){
            boolean found = false;
            for(int i=0; i<obj.length()-1;i++){
                try {
                    jobj = obj.getJSONObject(i);
                    ausgeleseneEinsch = jobj.getString("Name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(ausgeleseneEinsch.equals("PESCATARIAN")){ found = true; }
            }
            if(!found){
                einschränkungenEintragen("PESCATARIAN");
            }
        } else {
            deleteEintrag("PESCATARIAN");
        }

        if(kein_alkohol_ergebnis){
            boolean found = false;
            for(int i=0; i<obj.length()-1;i++){
                try {
                    jobj = obj.getJSONObject(i);
                    ausgeleseneEinsch = jobj.getString("Name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(ausgeleseneEinsch.equals("NO_ALCOHOL")){ found = true; }
            }
            if(!found){
                einschränkungenEintragen("NO_ALCOHOL");
            }
        } else {
            deleteEintrag("NO_ALCOHOL");
        }
        einschränkungenAbfragen();
    }

    @Override
    public void applyTexts(boolean keine_ergebnis, boolean laktose_ergebnis, boolean gluten_ergebnis, boolean nieren_ergebnis, boolean molke_ergebnis, boolean eifrei_ergebnis) {
        einschraenkungs_liste.setText("");
        if(keine_ergebnis){
            einschraenkungs_liste.append("Keine");
        }

        if(laktose_ergebnis){
            boolean found = false;
            for(int i=0; i<obj.length()-1;i++){
                try {
                    jobj = obj.getJSONObject(i);
                    ausgeleseneEinsch = jobj.getString("Name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(ausgeleseneEinsch.equals("MILK_FREE")){ found = true; }
            }
            if(!found){
                einschränkungenEintragen("MILK_FREE");
            }
        } else {
            deleteEintrag("MILK_FREE");
        }

        if(gluten_ergebnis){
            boolean found = false;
            for(int i=0; i<obj.length()-1;i++){
                try {
                    jobj = obj.getJSONObject(i);
                    ausgeleseneEinsch = jobj.getString("Name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(ausgeleseneEinsch.equals("GLUTEN_FREE")){ found = true; }
            }
            if(!found){
                einschränkungenEintragen("GLUTEN_FREE");
            }
        } else {
            deleteEintrag("GLUTEN_FREE");
        }

        if(nieren_ergebnis){
            boolean found = false;
            for(int i=0; i<obj.length()-1;i++){
                try {
                    jobj = obj.getJSONObject(i);
                    ausgeleseneEinsch = jobj.getString("Name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(ausgeleseneEinsch.equals("KIDNEY_FRIENDLY")){ found = true; }
            }
            if(!found){
                einschränkungenEintragen("KIDNEY_FRIENDLY");
            }
        } else {
            deleteEintrag("KIDNEY_FRIENDLY");
        }

        if(molke_ergebnis){
            boolean found = false;
            for(int i=0; i<obj.length()-1;i++){
                try {
                    jobj = obj.getJSONObject(i);
                    ausgeleseneEinsch = jobj.getString("Name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(ausgeleseneEinsch.equals("DAIRY_FREE")){ found = true; }
            }
            if(!found){
                einschränkungenEintragen("DAIRY_FREE");
            }
        } else {
            deleteEintrag("DAIRY_FREE");
        }
        if(eifrei_ergebnis){
            boolean found = false;
            for(int i=0; i<obj.length()-1;i++){
                try {
                    jobj = obj.getJSONObject(i);
                    ausgeleseneEinsch = jobj.getString("Name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(ausgeleseneEinsch.equals("EGG_FREE")){ found = true; }
            }
            if(!found){
                einschränkungenEintragen("EGG_FREE");
            }
        } else {
            deleteEintrag("EGG_FREE");
        }
        einschränkungenAbfragen();
    }

    @Override
    public void applyTexts(boolean keine_ergebnis, boolean nuss_ergebnis) {
        allergien_liste.setText("");
        if(keine_ergebnis){
            allergien_liste.append("Keine");
        }

        if(nuss_ergebnis){
            boolean found = false;
            for(int i=0; i<obj.length()-1;i++){
                try {
                    jobj = obj.getJSONObject(i);
                    ausgeleseneEinsch = jobj.getString("Name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(ausgeleseneEinsch.equals("PEANUT_FREE")){ found = true; }
            }
            if(!found){
                einschränkungenEintragen("PEANUT_FREE");
            }
        } else {
            deleteEintrag("PEANUT_FREE");
        }
        einschränkungenAbfragen();
    }

    public void einschränkungenAbfragen(){
        ArrayList<String> alleEinschränkungen = new ArrayList();
            Intent myIntent = new Intent(getApplicationContext(), com.example.kristian.fooduse.HTTPUrlConnectionService.class);
            String url = getString(R.string.Domainadress) + userPath + "/" + uid + "/Einschraenkungen";
            myIntent.putExtra("payload","");
            myIntent.putExtra("method","GET");
            myIntent.putExtra("from","BENUTZER EINSCHRAENKUNGEN");
            myIntent.putExtra("url",url);
            startService(myIntent);
    }

    public void einschränkungenEintragen(String name){
        JSONObject myObj=null;
        try {
            myObj = new JSONObject();
            myObj.put("Name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent myIntent = new Intent(getApplicationContext(), com.example.kristian.fooduse.HTTPUrlConnectionService.class);
        String url = getString(R.string.Domainadress) + userPath + "/" + uid + "/Einschraenkungen";
        myIntent.putExtra("payload",myObj.toString());
        myIntent.putExtra("method","POST");
        myIntent.putExtra("from","BENUTZER EINSCHRAENKUNGEN");
        myIntent.putExtra("url",url);
        startService(myIntent);
    }
    private void deleteEintrag(String name){
        for(int i=0; i<obj.length()-1; i++){
            try {
                jobj = obj.getJSONObject(i);
                ausgeleseneEinsch = jobj.getString("Name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(ausgeleseneEinsch.equals(name)){
                try {
                    String id = jobj.getString("_id");
                    Intent myIntent = new Intent(getApplicationContext(), com.example.kristian.fooduse.HTTPUrlConnectionService.class);
                    String url = getString(R.string.Domainadress) + userPath + "/" + uid + "/Einschraenkungen/" + id;
                    myIntent.putExtra("payload","");
                    myIntent.putExtra("method","DELETE");
                    myIntent.putExtra("from","BENUTZER EINSCHRAENKUNGEN DELETE");
                    myIntent.putExtra("url",url);
                    startService(myIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String status = intent.getStringExtra("from");
            if(status.equals("OK")){
                allergien_liste.setText("");
                einschraenkungs_liste.setText("");
                ernaehrungstyp_liste.setText("");
                String einsch = intent.getStringExtra("einschraenkungen");
                // Einschränkungen eintragen
                obj = null;
                try {
                    obj = new JSONArray(einsch);
                    for(int i=0; i<obj.length()-1; i++){
                        JSONObject jobj = obj.getJSONObject(i);
                        String ausgeleseneEinsch = jobj.getString("Name");
                        if(ausgeleseneEinsch.equals("VEGAN") || ausgeleseneEinsch.equals("VEGETARIAN") || ausgeleseneEinsch.equals("PESCATARIAN")
                                || ausgeleseneEinsch.equals("NO_ALCOHOL")){
                            ernaehrungstyp_liste.append("#"+ ausgeleseneEinsch + " ");
                        } else if(ausgeleseneEinsch.equals("MILK_FREE") || ausgeleseneEinsch.equals("DAIRY_FREE")|| ausgeleseneEinsch.equals("KIDNEY_FRIENDLY")
                                || ausgeleseneEinsch.equals("EGG_FREE")|| ausgeleseneEinsch.equals("GLUTEN_FREE")){
                            einschraenkungs_liste.append("#"+ ausgeleseneEinsch + " ");
                        } else if(ausgeleseneEinsch.equals("PEANUT_FREE")){
                            allergien_liste.append("#"+ ausgeleseneEinsch + " ");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(status.equals("NOCONNECTION")){
                Toast.makeText(getApplicationContext(), "Es kann keine Internet Verbindung zum Server aufgebaut werden!", Toast.LENGTH_SHORT).show();
            }

        }
    };
}
