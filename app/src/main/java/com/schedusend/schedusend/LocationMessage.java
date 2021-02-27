package com.schedusend.schedusend;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationMessage extends AppCompatActivity {

    private EditText editAddress;
    private EditText editMessage2;
    private AutoCompleteTextView editAutoContact2;
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> noDuplicateName = new ArrayList<>();
    private ArrayList<String> numbers = new ArrayList<>();
    private Geocoder coder = new Geocoder(this);
    private List<Address> addresses;
    private Location location = new Location("");

    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate((savedInstance));
        setContentView(R.layout.location_message);
        initializeUI();

        // start the service
        //startService(new Intent(this, LocationSendService.class));


        //toastMessage("Service Started");
    }

    public void initializeUI() {
        editAddress = (EditText) findViewById(R.id.editAddress);
        editAutoContact2 = (AutoCompleteTextView) findViewById(R.id.autoContact2);
        editMessage2 = (EditText) findViewById(R.id.editMessage2);
        readContacts();
    }

    //public void getCo

    public void toastMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    public void readContacts() {
        Cursor readIn = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        // keep reading in contacts while possible
        while (readIn.moveToNext()) {
            names.add(readIn.getString(readIn.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            numbers.add(readIn.getString(readIn.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
        }
        readIn.close();
        // nicely format all of the numbers and names
        formatNumbers(numbers);
        formatNames(names);
        // initialize a string array of contact suggestions to provide to the user
        ArrayAdapter<String> namesAuto = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noDuplicateName);
        editAutoContact2.setAdapter(namesAuto);
    }

    public void formatNames(ArrayList<String> names) {
        int max = names.size();
        for (int i = 0; i < max; i++) {
            // if the name isnt already in the new array
            if (!noDuplicateName.contains(names.get(i))) {
                // add the value
                noDuplicateName.add(names.get(i));
            }
        }
    }

    public void formatNumbers(ArrayList<String> numbers) {
        String number = "";
        for (int i = 0; i < numbers.size(); i++) {
            number = numbers.get(i);
            // removing hyphens and brackets from nummbers for ease of message decoding
            numbers.set(i, number.replaceAll("[\\s\\-()]", ""));
        }

    }


    public void setLocationSchedule(View view) {
        if (checkAddress(editAddress.getText().toString()) && setContact() && editMessage2.getText().toString()!= "") {
            toastMessage("good to go");

            Intent schedule_loc_message = new Intent(this, LocationSendService.class);
            schedule_loc_message.putExtra("location", location);
            schedule_loc_message.putExtra("number", phoneNumber);
            schedule_loc_message.putExtra("message", editMessage2.getText().toString());
            startService(schedule_loc_message);
        }
    }

    public boolean checkAddress(String address) {
        try {
            // May throw an IOException
            addresses = coder.getFromLocationName(address, 5);
            if (address == null) {
                return false;
            }

            Address addy = addresses.get(0);
            location.setLatitude(addy.getLatitude());
            location.setLongitude(addy.getLongitude());
            toastMessage("Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
            return true;
        } catch (IOException ex) {
            toastMessage("Invalid location entered");
            ex.printStackTrace();
            return false;
        }
    }

    public boolean setContact() {
        String contact = editAutoContact2.getText().toString();
        // check if a valid contact is entered
        int index = names.indexOf(contact);
        if (index != -1) {
            // if it is a valid phone number
            if (PhoneNumberUtils.isWellFormedSmsAddress(numbers.get(index))) {
                phoneNumber = numbers.get(index);
                return true;
            }
            else
                toastMessage("The entered contact does not have a valid number");
        } else
            toastMessage("Please enter a valid contact");

        return false;

    }
    }




