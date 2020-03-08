package com.schedusend.schedusend;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

public class WelcomeScreen extends AppCompatActivity {
    private GeofencingClient geofencingClient;

    public WelcomeScreen(){

    }
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.welcome_screen);
        geofencingClient = LocationServices.getGeofencingClient(this);
        requestPermissions();
    }

    public void requestPermissions(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS},1);

    }
    public void moveTimeScreen (View view){
        Intent intent = new Intent(this, TimedMessage.class);
        startActivity(intent);
    }

    public void moveLocationScreen (View view){
        Intent intent = new Intent(this, LocationMessage.class);
        startActivity(intent);
    }
}
