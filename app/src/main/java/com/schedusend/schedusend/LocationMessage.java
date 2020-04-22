package com.schedusend.schedusend;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationMessage extends AppCompatActivity {
    private GeofencingClient geofencingClient;

//    @Override
//    protected void onCreate(Bundle savedInstance){
//        super.onCreate(savedInstance);
//        geofencingClient = LocationServices.getGeofencingClient(this);
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (permissionRationaleAlreadyShown) {
//                ActivityCompat.requestPermissions(this,
//                        new String[] { Manifest.permission.ACCESS_BACKGROUND_LOCATION },
//                        background-location-permission-request-code);
//            } else {
//                // Show an explanation to the user as to why your app needs the
//                // permission. Display the explanation *asynchronously* -- don't block
//                // this thread waiting for the user's response!
//            }
//        } else {
//            // Background location runtime permission already granted.
//            geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
//                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            // Geofences added
//                            // ...
//                        }
//                    })
//                    .addOnFailureListener(this, new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Failed to add geofences
//                            // ...
//                        }
//                    });
//        }
//        setContentView(R.layout.location_message);
//    }
//
//    private PendingIntent getGeofencePendingIntent() {
//        // Reuse the PendingIntent if we already have it.
//        if (geofencePendingIntent != null) {
//            return geofencePendingIntent;
//        }
//        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
//        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
//        // calling addGeofences() and removeGeofences().
//        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
//                FLAG_UPDATE_CURRENT);
//        return geofencePendingIntent;
//    }
//
//    private GeofencingRequest getGeofencingRequest() {
//        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
//        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
//        builder.addGeofences(geofenceList);
//        return builder.build();
//    }



}
