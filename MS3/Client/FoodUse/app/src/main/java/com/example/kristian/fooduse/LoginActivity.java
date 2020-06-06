package com.example.kristian.fooduse;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private EditText email_editText;
    private EditText passwort_editText;
    private String email;
    private String passwort;
    private Button loginBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private TextView registrieren;
    private SharedPreferences prefs;
    private static final String TAG = "Honigball";
    private static final int ERROR_DIALOG_REQUEST = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Google Maps Services überprüfen

        isServiceOK();

        setContentView(R.layout.activity_login);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        // Falls der Benutzer sich bereits einmal eingeloggt hat wird er direkt weiter geleitet zum Lebensmittelinventar
        if (prefs.getBoolean("eingeloggt", true)) {
            Intent inventarIntent = new Intent(getApplicationContext(), Hauptbildschirm.class);
            startActivity(inventarIntent);
        }

        // Variablen der View werden adressiert und die Progress-bar wird zunächst auf INVISIBLE gesetzt.
        email_editText = findViewById(R.id.email_edittext);
        passwort_editText = findViewById(R.id.passwort_edittext);
        loginBtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.login_progressbar);
        registrieren = findViewById(R.id.keinBenutzer_textview);
        progressBar.setVisibility(View.INVISIBLE);


        // Sollte der Benutzer noch kein Konto haben, so gelangt er durch den Button auf die Registrierungsactivity.
        registrieren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registrierenIntent = new Intent(getApplicationContext(), RegistrierenActivity.class);
                startActivity(registrierenIntent);
            }
        });

        // An dieser Stelle wird mit der Methode signInWithEmailAndPasswort der Benutzer versucht eingeloggt zu werden.
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginBtn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                email = email_editText.getText().toString().trim();
                passwort = passwort_editText.getText().toString().trim();

                if(email.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Email eingeben", Toast.LENGTH_SHORT).show();
                    loginBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else if (passwort.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Passwort eingeben", Toast.LENGTH_SHORT).show();
                    loginBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    mAuth.signInWithEmailAndPassword(email, passwort)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        finish();
                                        // In den Shared Preferences wird festgehalten, dass der Benutzer eingeloggt ist.
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putBoolean("eingeloggt", true);
                                        editor.commit();
                                        Intent inventarIntent = new Intent(getApplicationContext(), Hauptbildschirm.class);
                                        startActivity(inventarIntent);
                                        loginBtn.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getApplicationContext(), "Willkommen", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Email und Passwort stimmen nicht überein", Toast.LENGTH_SHORT).show();
                                        loginBtn.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
            }
        });

    }
    public boolean isServiceOK(){
        Log.d(TAG, "IsServicesOK: Service überprüfen");
        int verfügbar = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LoginActivity.this);
        if(verfügbar == ConnectionResult.SUCCESS){
            Log.d(TAG, "IsServicesOK: Service ist da!");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(verfügbar)){
            Log.d(TAG, "IsServiceOK: Nicht ok aber reparierbar");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity.this, verfügbar, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else{
            Toast.makeText(this,"Google Maps funktioniert auf diesem Gerät leider nicht.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
