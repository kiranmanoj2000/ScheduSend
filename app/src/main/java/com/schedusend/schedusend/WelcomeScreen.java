package com.schedusend.schedusend;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class WelcomeScreen extends AppCompatActivity {

    public WelcomeScreen(){

    }
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.welcome_screen);
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
