package com.schedusend.schedusend;
import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

private boolean correctYear = false;
private boolean correctMonth = false;
private boolean correctDay = false;
private boolean correctTime = false;
private boolean correctContact = false;
private boolean allowSchedule = false;

private EditText editYear;
private EditText editMonth;
private EditText editDay;
private EditText editTime;
private EditText editContact;
private EditText editMessage;

private int monthIndex = -1;
private int targetYear;
private int targetDay;
private int targetHour;
private int targetMinute;
private int numScheduled = 0;
private int uniqueID = 0;
private static final int YEARCONVERSION = 1900;

private String phoneNumber;


private static final String[] MONTHS = {"January","February","March","April","May","June","July",
        "August","September","October","November","December"};
private ArrayList<String> names = new ArrayList<>();
private ArrayList<String> numbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        initializeUI();


    }

    public void initializeUI(){
        editYear = (EditText)findViewById(R.id.editYear);
        editMonth = (EditText)findViewById(R.id.editMonth);
        editDay = (EditText)findViewById(R.id.editDay);
        editTime = (EditText)findViewById(R.id.editTime);
        editContact = (EditText)findViewById(R.id.editContact);
        editMessage = (EditText)findViewById(R.id.editMessage);

        // setting to current times
        editYear.setText(""+Calendar.getInstance().get(Calendar.YEAR));
        editMonth.setText(MONTHS[Calendar.getInstance().get(Calendar.MONTH)]);
        editDay.setText(""+Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        // getting current hours and min
        int hours = (int)Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = (int)Calendar.getInstance().get(Calendar.MINUTE)+1;
        String convertHours = Integer.toString(hours);
        String convertMins = Integer.toString(min);
        // formatting time
        if(min<10&&hours<10){
            editTime.setText("0"+ convertHours + ":0" + convertMins);
        }else if(min<10){
            editTime.setText(convertHours + ":0" + convertMins);
        }else if(hours<10){
            editTime.setText("0"+ convertHours + ":" + convertMins);
        }else{
            editTime.setText(convertHours + ":" + convertMins);
        }


        // freezing each textview
        editMonth.setFocusable(false);
        editDay.setFocusable(false);
        editTime.setFocusable(false);
        editContact.setFocusable(false);
        editMessage.setFocusable(false);



    }

    public void readContacts(){
        Cursor readIn = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
        // keep reading in contacts while possible
        while (readIn.moveToNext()){
           names.add(readIn.getString(readIn.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
           numbers.add(readIn.getString(readIn.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
       }
       readIn.close();
       formatNumbers(numbers);
    }

    public void formatNumbers(ArrayList<String> numbers){
        String number = "";
        for(int i = 0;i<numbers.size();i++){
            number = numbers.get(i);
            // removing hyphens and brackets from nummbers for ease of message decoding
            numbers.set(i,number.replaceAll("[\\s\\-()]", ""));
        }

    }

    public void setYear(View view){
        targetYear = 0;
        String year = editYear.getText().toString();
        if(!year.equals("")){
            targetYear = Integer.parseInt(year) - YEARCONVERSION;
        }

        if(targetYear == 0){
            Toast.makeText(this,"Please enter a year", Toast.LENGTH_LONG).show();
        }

        else if(targetYear>=(Calendar.getInstance().get(Calendar.YEAR))-YEARCONVERSION
                &&(targetYear-(Calendar.getInstance().get(Calendar.YEAR)-YEARCONVERSION))==0){
            // increase the count of the correct inputs
            correctYear=true;
            // disable re-editing
            editYear.setFocusable(false);
            // allow editing of next
            editMonth.setFocusableInTouchMode(true);
        }else{
            Toast.makeText(this,"Please enter a valid year", Toast.LENGTH_LONG).show();
        }
        readContacts();
    }

    public void setMonth(View view){
        if(correctYear){
            String month = editMonth.getText().toString();
            boolean found = false;
            for(int i = 0; i<12 && !found; i++){
                if(MONTHS[i].equalsIgnoreCase(month)){
                    monthIndex = i;
                    found= true;
                }
            }
            // check if a month is even entered
            if(monthIndex==-1){
                Toast.makeText(this,"Please enter a proper month", Toast.LENGTH_LONG).show();
            }else
                // if its December
                if(Calendar.getInstance().get(Calendar.MONTH)==11&&!(monthIndex==0||monthIndex==11)){
                    Toast.makeText(this,"Cannot schedule for longer than a month", Toast.LENGTH_LONG).show();
                }else
                if(monthIndex>(Calendar.getInstance().get(Calendar.MONTH)+1) && monthIndex!=11){
                    Toast.makeText(this,"Cannot schedule for longer than a month", Toast.LENGTH_LONG).show();
                }
                // the month has passed
                else if(Calendar.getInstance().get(Calendar.MONTH)-monthIndex<0){
                    Toast.makeText(this,"Cannot schedule in the past", Toast.LENGTH_LONG).show();
                }

                else{
                    editMonth.setFocusable(false);
                    editDay.setFocusableInTouchMode(true);
                    correctMonth = true;
                }
        }

    }

    public void setDay(View view){
        if(correctMonth){
            int date = 0;
            if(!editDay.getText().toString().equals("")){
                date = Integer.parseInt(editDay.getText().toString());
            }

            if(date == -1){
                Toast.makeText(this,"No date entered", Toast.LENGTH_LONG).show();
            }// date is before current date
            else if(date< Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
                Toast.makeText(this,"Date is not before the current date", Toast.LENGTH_LONG).show();
            }
            else if(date == 0){
                Toast.makeText(this,"Date is not in month", Toast.LENGTH_LONG).show();
            }
            else if(monthIndex==0&&date>31){
                Toast.makeText(this,"Date is not in month", Toast.LENGTH_LONG).show();
            }else if(monthIndex!=0 && monthIndex!=11 && monthIndex%2 == 1 && date>30){
                Toast.makeText(this,"Date is not in month", Toast.LENGTH_LONG).show();
            }else if(date>31&&monthIndex%2==0 && monthIndex!=11){
                Toast.makeText(this,"Date is not in month", Toast.LENGTH_LONG).show();
            }else if(date>31 && monthIndex ==11){
                Toast.makeText(this,"Date is not in month", Toast.LENGTH_LONG).show();
            }

            // the day is valid
            else{
                editDay.setFocusable(false);
                editTime.setFocusableInTouchMode(true);
                targetDay = date;
                correctDay = true;
            }
        }



    }

    public void setTime(View view){
        if(correctDay) {
            int index = 0;
            String userTime = editTime.getText().toString();
            // if the string entered is 5 characters
            if (userTime.length() == 5) {

                // find how many colons there are
                for (int i = 0; i < 5; i++) {
                    if (userTime.charAt(i) == ':') {
                        index++;
                    }
                }
                // there is more than one colon
                if (index > 1) {
                    Toast.makeText(this, "More than one colon", Toast.LENGTH_LONG).show();
                }
                // one colon in middle
                else if (userTime.charAt(2) == ':') {
                    String[] hrMin = userTime.split(":");
                    int hr = Integer.parseInt(hrMin[0]);
                    int min = Integer.parseInt(hrMin[1]);
                    int accHours = (int)Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    int accMin = (int)Calendar.getInstance().get(Calendar.MINUTE);
                    Date compare = new Date(targetYear,monthIndex,targetDay,hr,min);

                    if (hr > 24 || min > 60) {
                        Toast.makeText(this, "Please enter a valid time", Toast.LENGTH_LONG).show();
                    }
                    // check if time is before the time right now
                    else if(!compare.after(Calendar.getInstance().getTime())){
                        Toast.makeText(this, "The entered time has passed", Toast.LENGTH_LONG).show();
                    }
                    // CORRECT TIME
                    else {
                        targetHour = hr;
                        targetMinute = min;
                        correctTime = true;
                        editTime.setFocusable(false);
                        editContact.setFocusableInTouchMode(true);
                    }
                }
                // there's no colon in the centre
                else {
                    Toast.makeText(this, "Please seperate hrs and min with ':'", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Please enter a 5 character time", Toast.LENGTH_LONG).show();
            }
        }



    }

    public void setContact(View view){
        if(correctTime){
            String contact = editContact.getText().toString();
            // check if a valid contact is entered
            int index = names.indexOf(contact);
            if(index!=-1){
                // if it is a valid phone number
                if(PhoneNumberUtils.isWellFormedSmsAddress(numbers.get(index))){
                    phoneNumber = numbers.get(index);
                    Toast.makeText(this,phoneNumber, Toast.LENGTH_LONG).show();
                    editContact.setFocusable(false);
                    editMessage.setFocusableInTouchMode(true);
                    allowSchedule = true;
                    correctContact = true;
                }else{
                    Toast.makeText(this, "The entered contact does not have a valid number", Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(this, "Please enter a valid contact", Toast.LENGTH_LONG).show();
            }
        }


    }


    public void reset(View view){
        editYear.setFocusableInTouchMode(true);
        editMonth.setFocusable(false);
        editDay.setFocusable(false);
        editTime.setFocusable(false);
        editMessage.setFocusable(false);
        editContact.setFocusable(false);
        correctYear = false;
        correctMonth = false;
        correctDay = false;
        correctTime = false;
        correctContact = false;
        allowSchedule = false;
    }

    public void callToSchedule(View view){
        // only run id they have scheduled 3 or less messages
        if(numScheduled<4){
            // get message
            String message = editMessage.getText().toString();
            if(!message.equals("")){
                // only run if all times are proper
                if(correctYear&&correctMonth&&correctDay&&correctTime&&correctContact&&allowSchedule){

                    scheduleBackend(createJobInfo(calculateTimeUntil(targetYear,monthIndex,targetDay,targetHour,targetMinute)));

                }else{
                    Toast.makeText(this, "Message cannot be scheduled with current configuration", Toast.LENGTH_LONG).show();
                }
            }
            else{
                if(!allowSchedule){
                    Toast.makeText(this, "Two messages cannot be scheduled at the same time", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this, "Message cannot be scheduled with an empty message", Toast.LENGTH_LONG).show();
                }

            }
            allowSchedule = false;
            numScheduled++;
        }else{
            Toast.makeText(this, "You cannot schedule more than 3 messages", Toast.LENGTH_LONG).show();
        }

    }





    public int calculateTimeUntil(int year, int month, int date, int hours, int min){
        Date targetDay = new Date(year,month,date,hours,min);
        int waitTime = 0;
        if(targetDay.getTime()>Calendar.getInstance().getTimeInMillis()) {
            waitTime = (int) (targetDay.getTime() - Calendar.getInstance().getTimeInMillis());
        }
        //if entered date is before current date
        if(waitTime<0){
            Toast.makeText(this,"The entered date has passed",Toast.LENGTH_LONG).show();
        }
        return waitTime;
    }


    JobInfo createJobInfo(int timeWait){
        ComponentName service = new ComponentName(this, JobScheduleService.class);
        // get message
        PersistableBundle text = new PersistableBundle();
        // get the length of the message
        int messgLength = editMessage.getText().toString().length();
        // concatenate message and number
        String packaged = editMessage.getText().toString()+phoneNumber;
        // add the length of the message to the concatenated string
        packaged += ""+messgLength;
        text.putString("Text", packaged);
        JobInfo info = new JobInfo.Builder(uniqueID, service).setExtras(text).setMinimumLatency(timeWait).setOverrideDeadline(timeWait+10000).build();
        uniqueID++;
        if(timeWait<0){
            return null;
        } else {
            return info;
        }


    }

    public void scheduleBackend(JobInfo info){
        JobScheduler scheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);

    }

    public void requestPermissions(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS},1);

    }
}