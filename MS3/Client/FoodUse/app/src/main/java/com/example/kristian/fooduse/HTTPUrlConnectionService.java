package com.example.kristian.fooduse;

import android.app.IntentService;
import android.content.Intent;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


import java.io.BufferedReader;
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

public class HTTPUrlConnectionService extends IntentService {
    public HTTPUrlConnectionService() {
        super("name");
    }

    private String response = null;
    private String method;
    private String from;
    HttpURLConnection connection = null;
    Handler mHandler;

    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("HTTPURLCONNECTION", "HTTPService hat einen Intent erhalten!" + intent.getStringExtra("url") + intent.getStringExtra("method") + intent.getStringExtra("from"));
        String payload = intent.getStringExtra("payload");
        String uri = intent.getStringExtra("url");
        method = intent.getStringExtra("method");
        from = intent.getStringExtra("from");

        try {
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(10000);
            if (method.equals("POST") || method.equals("PUT")) {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
                streamWriter.write(payload);
                streamWriter.flush();
            } else if (method.equals("GET")) {
                connection.setUseCaches(false);
                connection.setAllowUserInteraction(false);
                connection.connect();
                Log.d("HTTPURLCONNECTION", "Verbindung wurde aufgebaut");
            } else if(method.equals("DELETE")){
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("DELETE");
                connection.connect();
            }
            sendJSON(uri, payload);
        }  catch (SocketTimeoutException e) {
            verbindungsTimeout(from);
            Log.d("HTTPURLCONNECTION1", "FEHLER: " + e.getMessage());
        } catch (IOException e) {
            Log.d("HTTPURLCONNECTION2", "FEHLER: " + e.getMessage());
            verbindungsTimeout(from);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                }
            });
        } catch (NoSuchAlgorithmException e) {
            Log.d("HTTPURLCONNECTION3", "FEHLER: " + e.getMessage());
            e.printStackTrace();
        } catch (KeyManagementException e) {
            Log.d("HTTPURLCONNECTION4", "FEHLER: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void sendJSON(String uri, String payload) throws KeyManagementException, NoSuchAlgorithmException {

        try {
            StringBuilder stringBuilder = new StringBuilder();
            // Status der anfrage wird ermittelt
            int status = connection.getResponseCode();

            Log.d("HTTPURLCONNECTION", "Status der Anfrage: " + status + connection.getResponseMessage());
            Intent intent = new Intent();
            switch(status){
                case 400:
                    switch (from) {
                        case "ANGEBOT_ERSTELLEN":
                            intent = new Intent("user");
                            intent.putExtra("user", stringBuilder.toString());
                            intent.putExtra("from", "FAIL");
                            break;
                        case "BENUTZER_ERSTELLEN":
                            intent = new Intent("BENUTZER_ERSTELLEN");
                            intent.putExtra("user", stringBuilder.toString());
                            intent.putExtra("from", "FAIL");
                            break;
                        case "TOKEN ERSTELLEN":
                            intent = new Intent("TOKEN ERSTELLEN");
                            intent.putExtra("token", stringBuilder.toString());
                            intent.putExtra("from", "FAIL");
                            break;
                        case "ANGEBOTE SUCHEN":
                            intent = new Intent("ANGEBOTE SUCHEN");
                            intent.putExtra("angebote", stringBuilder.toString());
                            intent.putExtra("from", "FAIL");
                            break;
                        case "BENUTZER ACCOUNT":
                            intent = new Intent("ACCOUNT BENUTZER");
                            intent.putExtra("user", stringBuilder.toString());
                            intent.putExtra("from", "FAIL");
                            break;
                        case "BENUTZER EINSCHRAENKUNGEN":
                            intent = new Intent("BENUTZER EINSCHRAENKUNGEN");
                            intent.putExtra("einschraenkungen", stringBuilder.toString());
                            intent.putExtra("from", "FAIL");
                            break;
                    }
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    break;
                case 200:
                case 201:
                    InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(streamReader);

                    // Der Response Payloud with ausgelesen
                    while ((response = bufferedReader.readLine()) != null) {
                        stringBuilder.append(response + "\n");
                    }
                    bufferedReader.close();
                    Log.d("HTTPURLCONNECTION", "HTTPURLCONNECTION: Daten die abgefragt wurden: " + stringBuilder.toString());

                    // Die jeweilige aanfragende Activity wird benachrichtigt.
                    switch (from) {
                        case "ANGEBOT_ERSTELLEN":
                            intent = new Intent("user");
                            intent.putExtra("user", stringBuilder.toString());
                            intent.putExtra("from", "OK");
                            break;
                        case "BENUTZER_ERSTELLEN":
                            intent = new Intent("BENUTZER_ERSTELLEN");
                            intent.putExtra("user", stringBuilder.toString());
                            intent.putExtra("from", "OK");
                            break;
                        case "TOKEN ERSTELLEN":
                            intent = new Intent("TOKEN ERSTELLEN");
                            intent.putExtra("token", stringBuilder.toString());
                            intent.putExtra("from", "OK");
                            break;
                        case "ANGEBOTE SUCHEN":
                            intent = new Intent("ANGEBOTE SUCHEN");
                            intent.putExtra("angebote", stringBuilder.toString());
                            intent.putExtra("from", "OK");
                            break;
                        case "BENUTZER ACCOUNT":
                            intent = new Intent("ACCOUNT BENUTZER");
                            intent.putExtra("user", stringBuilder.toString());
                            intent.putExtra("from", "OK");
                            break;
                        case "BENUTZER EINSCHRAENKUNGEN":
                            intent = new Intent("BENUTZER EINSCHRAENKUNGEN");
                            intent.putExtra("einschraenkungen", stringBuilder.toString());
                            intent.putExtra("from", "OK");
                            break;
                    }
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    break;
            }
        } catch (SocketTimeoutException e) {
        } catch (Exception exception) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                }
            });

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    // Bei einem Timeout wird die Activity ebenfalls benachrichtigt.
    public void verbindungsTimeout(String from){
        Intent intent = new Intent("default");
        switch (from) {
            case "ANGEBOT_ERSTELLEN":
                intent = new Intent("user");
                intent.putExtra("from", "NOCONNECTION");
                break;
            case "BENUTZER_ERSTELLEN":
                intent = new Intent("BENUTZER_ERSTELLEN");
                intent.putExtra("from", "NOCONNECTION");
                break;
            case "TOKEN ERSTELLEN":
                intent = new Intent("TOKEN ERSTELLEN");
                intent.putExtra("from", "NOCONNECTION");
                break;
            case "ANGEBOTE SUCHEN":
                intent = new Intent("ANGEBOTE SUCHEN");
                intent.putExtra("from", "NOCONNECTION");
                break;
            case "BENUTZER ACCOUNT":
                intent = new Intent("ACCOUNT BENUTZER");
                intent.putExtra("from", "NOCONNECTION");
                break;
            case "BENUTZER EINSCHRAENKUNGEN":
                intent = new Intent("BENUTZER EINSCHRAENKUNGEN");
                intent.putExtra("from", "NOCONNECTION");
                break;
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}

