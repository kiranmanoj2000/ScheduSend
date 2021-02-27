package com.schedusend.schedusend;


import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;


public class LocationSendService extends Service {
    private static final String TAG = "MyLocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 10f;
    private boolean message_sent = false;
    private long initTime= System.currentTimeMillis();
    private Location LZ = new Location("");
    private SmsManager smsManager;
    private NotificationManager notifyManager;
    private NotificationChannel channel;

    private String number;
    private String message;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;




        public LocationListener(String provider, Location target) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
            LZ = target;

        }

        @Override
        public void onLocationChanged(Location location) {
            // check if the location is the specified location
            Log.e(TAG, "onLocationChanged: " + location);

            // if you are in a 40 m radius
            if(LZ.distanceTo(location) <= 40f){
                useThread();
            }
            mLastLocation.set(location);

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

        public void sendMessage(){
            try {
                smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(""+number, null, ""+ message, null, null);
                notifyUser();
                message_sent = true;
                stopSelf();
            } catch (Exception error) {
                error.printStackTrace();
                stopSelf();
            }
        }

        private void useThread(){
            if(!message_sent) {
                // run the job on a worker thread
                new Thread(new Runnable() {
                    public void run() {
                        // put the thread in the background
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                        try {
                            // deliver the message
                            sendMessage();

                        } catch (Exception error) {
                            error.printStackTrace();
                        }
                    }
                }).start();

            }
        }

        public void notifyUser(){
            channel = new NotificationChannel("Channel_4", "Notify", NotificationManager.IMPORTANCE_HIGH);
            notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notifyManager.createNotificationChannel(channel);
            Notification notify = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle("Message Sent To " + number).setContentText(message)
                    .setChannelId("Channel_4").build();
            notifyManager.notify((int) (Math.random()*1001), notify);


        }
    }



    LocationListener[] mLocationListeners;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        // get all the data from the intent
        Bundle data = intent.getExtras();

        message = data.get("message").toString();
        number = data.get("number").toString();
        LZ = (Location) data.get("location");

        mLocationListeners = new LocationListener[]{
                new LocationListener(LocationManager.GPS_PROVIDER, LZ)
        };

        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[0]
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }
    public void toastMessage(String text){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onCreate() {
//        Intent notificationIntent = new Intent(this, LocationMessage.class);
//        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"Channel 1")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("TEST")
//                .setContentText("HELLO")
//                .setTicker("TICKER")
//                .setContentIntent(pendingIntent);
//        Notification notification=builder.build();

        //startForeground((int) (Math.random()*1001), notification);
        super.onCreate();
        //this.stopSelf();
        Log.e(TAG, "onCreate");
        // start thread to kill service after certain amount of time
        /*new Thread(new Runnable() {
            public void run() {
                // put the thread in the background
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                while(true){
                    System.currentTimeMillis()
                }
            }
        }).start();*/

        //toastMessage(""+huron.distanceTo(steckle));





    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex);
                }
            }
        }
        message_sent = true;
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: "+ LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }


}