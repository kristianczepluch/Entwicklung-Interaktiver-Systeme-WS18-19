package com.example.kristian.fooduse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class Benachrichtigungen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton hamburgermenu_Btn;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benachrichtigungen);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation4);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        drawerLayout = findViewById(R.id.drawer5);
        NavigationView sidebarNavigation = findViewById(R.id.meinebenachrichtigungen_sidebarnav);
        sidebarNavigation.setNavigationItemSelectedListener(this);
        hamburgermenu_Btn = findViewById(R.id.hamburmenu_btn_benachrichtigungen);
        hamburgermenu_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
                drawerLayout.bringToFront();
            }
        });



    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch(menuItem.getItemId()){
                case R.id.nav_home:
                    Intent intentMeinInventar = new Intent(getApplicationContext(),Hauptbildschirm.class);
                    startActivity(intentMeinInventar);
                    break;
                case R.id.nav_angebote:
                    Intent intentMeineAngebote = new Intent(getApplicationContext(), AngeboteSuchen.class);
                    startActivity(intentMeineAngebote);
                    break;
                case R.id.nav_reservierungen:
                    Intent intentMeineReservierungen = new Intent(getApplicationContext(), MeineReservierungen.class);
                    startActivity(intentMeineReservierungen);
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
}
