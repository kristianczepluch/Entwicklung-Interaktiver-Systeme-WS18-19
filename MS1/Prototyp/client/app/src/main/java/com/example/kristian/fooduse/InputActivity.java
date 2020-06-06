package com.example.kristian.fooduse;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;

public class InputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        Button start_btn = findViewById(R.id.haltbarkeitberechnen_btn);
        TextInputLayout name1 = findViewById(R.id.lebensmittel_name_1);
        TextInputLayout name2 = findViewById(R.id.lebensmittel_name_2);
        TextInputLayout ort1 = findViewById(R.id.Lebensmittel_ort_1);
        TextInputLayout ort2 = findViewById(R.id.Lebensmittel_ort_2);
        TextInputLayout datum1 = findViewById(R.id.Lebensmittel_kaufdatum1);
        TextInputLayout datum2 = findViewById(R.id.Lebensmittel_kaufdatum2);


    }
}
