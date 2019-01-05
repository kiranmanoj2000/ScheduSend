package com.schedusend.schedusend;
import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
private boolean hasReadContacts = false;
private boolean inDoze = false;
private boolean inDozeSet = false;

private EditText editYear;
private AutoCompleteTextView editAutoMonth;
private EditText editDay;
private EditText editTime;
private AutoCompleteTextView editAutoContact;
private EditText editMessage;

private Button yearButton;
private Button monthButton;
private Button dayButton;
private Button timeButton;
private Button contactButton;
private Button sendButton;
private Button dozeButton;

private int monthIndex = -1;
private int targetYear;
private int targetDay;
private int targetHour;
private int targetMinute;
private int numScheduled = 0;
private int uniqueID = 0;
private int count = 0;
private static final int YEARCONVERSION = 1900;

private String phoneNumber;
private String check = new String(Character.toChars(0x2705));

private JobInfo info;

private static final String[] MONTHS = {"January","February","March","April","May","June","July",
        "August","September","October","November","December"};
private ArrayList<String> names = new ArrayList<>();
private ArrayList<String> noDuplicateName = new ArrayList<>();
private ArrayList<String> numbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        initializeUI();
    }

    public void initializeUI(){
        // removing notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // initializing editTexts
        editYear = (EditText)findViewById(R.id.editYear);
        editAutoMonth = (AutoCompleteTextView)findViewById(R.id.autoMonth);
        editDay = (EditText)findViewById(R.id.editDay);
        editTime = (EditText)findViewById(R.id.editTime);
        editAutoContact = (AutoCompleteTextView)findViewById(R.id.autoContact);
        editMessage = (EditText)findViewById(R.id.editMessage);

        // initializing buttons
        yearButton = (Button)findViewById(R.id.yearButton);
        monthButton = (Button)findViewById(R.id.monthButton);
        dayButton = (Button)findViewById(R.id.dayButton);
        timeButton = (Button)findViewById(R.id.timeButton);
        contactButton = (Button)findViewById(R.id.contactButton);
        sendButton = (Button)findViewById(R.id.sendButton);
        dozeButton = (Button)findViewById(R.id.dozeButton);


        // setting to current times
        editYear.setText(""+Calendar.getInstance().get(Calendar.YEAR));
        editAutoMonth.setText(MONTHS[Calendar.getInstance().get(Calendar.MONTH)]);
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

        // setting the colours of buttons
        setButtonColourRed(yearButton);
        setButtonColourRed(monthButton);
        setButtonColourRed(dayButton);
        setButtonColourRed(timeButton);
        setButtonColourRed(contactButton);
        setButtonColourRed(sendButton);



        // freezing each TextView
        editAutoMonth.setFocusable(false);
        editDay.setFocusable(false);
        editTime.setFocusable(false);
        editAutoContact.setFocusable(false);
        editMessage.setFocusable(false);

        // initialize suggestion of months for the user
        ArrayAdapter<String> monthsAuto = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MONTHS);
        editAutoMonth.setAdapter(monthsAuto);

    }

    public void setButtonColourRed(Button button){
        button.setBackgroundColor(Color.rgb(254, 57,84));
    }

    public void setButtonColourGreen(Button button){
        button.setBackgroundColor(Color.rgb(57,254,81));
        if(button!=sendButton){
            button.setText(check);
        }
    }

    public void setDoze(View view){
        // alternated between doze and not in doze
        count++;
        if(count%2!=0){
            dozeButton.setText("Device will NOT be used 30 min prior to message being sent");
            inDoze= true;
        }else{
            dozeButton.setText("Device WILL be used 30 min prior to message being sent");
            inDoze = false;
        }
        inDozeSet = true;
    }

    public void toastMessage(String text){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    public void readContacts(){
        Cursor readIn = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
        // keep reading in contacts while possible
        while (readIn.moveToNext()){
           names.add(readIn.getString(readIn.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
           numbers.add(readIn.getString(readIn.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
       }
       readIn.close();
        // nicely format all of the numbers and names
       formatNumbers(numbers);
       formatNames(names);
       // initialize a string array of contact suggestions to provide to the user
        ArrayAdapter<String> namesAuto = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noDuplicateName);
        editAutoContact.setAdapter(namesAuto);
    }

    public void formatNames(ArrayList<String> names){
        int max = names.size();
        for(int i = 0; i<max; i++){
            // if the name isnt already in the new array
            if(!noDuplicateName.contains(names.get(i))){
                // add the value
                noDuplicateName.add(names.get(i));
            }
        }
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
        // only allow this code to be executed if a correct year has not been entered
        if(!correctYear){
            targetYear = 0;
            String year = editYear.getText().toString();
            if(!year.equals("")){
                targetYear = Integer.parseInt(year) - YEARCONVERSION;
            }

            if(targetYear == 0){
                toastMessage("Please enter a year");
            }

            else if(targetYear>=(Calendar.getInstance().get(Calendar.YEAR))-YEARCONVERSION
                    &&(targetYear-(Calendar.getInstance().get(Calendar.YEAR)-YEARCONVERSION))==0){
                // increase the count of the correct inputs
                correctYear=true;
                // disable re-editing
                editYear.setFocusable(false);
                // change colour
                setButtonColourGreen(yearButton);
                // allow editing of next
                editAutoMonth.setFocusableInTouchMode(true);
            }else{
                toastMessage("Please enter a valid year");
            }
            // if the contacts havent been read in (contacts will only be read in once)
            if(!hasReadContacts){
                readContacts();

            }

        }

    }

    public void setMonth(View view){
        if(correctYear&&!correctMonth){
            String month = editAutoMonth.getText().toString();
            boolean found = false;
            for(int i = 0; i<12 && !found; i++){
                if(MONTHS[i].equalsIgnoreCase(month)){
                    monthIndex = i;
                    found= true;
                }
            }
            // check if a month is even entered
            if(monthIndex==-1){
                toastMessage("Please enter a proper month");
            }else
                // if its December
                if(Calendar.getInstance().get(Calendar.MONTH)==11&&!(monthIndex==0||monthIndex==11)){
                    toastMessage("Cannot schedule for longer than a month");
                }

                else
                if(monthIndex>(Calendar.getInstance().get(Calendar.MONTH)+1)){
                    toastMessage("Cannot schedule for longer than a month");
                }
                // the month has passed
                else if(Calendar.getInstance().get(Calendar.MONTH)-monthIndex>0){
                    toastMessage("Cannot schedule in the past");
                }

                else{
                    editAutoMonth.setFocusable(false);
                    editDay.setFocusableInTouchMode(true);
                    setButtonColourGreen(monthButton);
                    correctMonth = true;
                }
        }

    }

    public void setDay(View view){
        if(correctMonth&&!correctDay){
            int date = 0;
            if(!editDay.getText().toString().equals("")){
                date = Integer.parseInt(editDay.getText().toString());
            }

            if(date == -1){
                toastMessage("No date entered");
            }// date is before current date
            else if(date< Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
                toastMessage("Date is not before the current date");
            }
            else if(date == 0){
                toastMessage("Date is not in month");
            }
            else if(monthIndex==0&&date>31){
                toastMessage("Date is not in month");
            }else if(monthIndex!=0 && monthIndex!=11 && monthIndex%2 == 1 && date>30){
                toastMessage("Date is not in month");
            }else if(date>31&&monthIndex%2==0 && monthIndex!=11){
                toastMessage("Date is not in month");
            }else if(date>31 && monthIndex ==11){
                toastMessage("Date is not in month");
            }

            // the day is valid
            else{
                editDay.setFocusable(false);
                editTime.setFocusableInTouchMode(true);
                targetDay = date;
                setButtonColourGreen(dayButton);
                correctDay = true;
            }
        }



    }

    public void setTime(View view){
        if(correctDay&&!correctTime) {
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
                    toastMessage("More than one colon");
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
                        toastMessage("Please enter a valid time");
                    }
                    // check if time is before the time right now
                    else if(!compare.after(Calendar.getInstance().getTime())){
                        toastMessage("The entered time has passed");
                    }
                    // CORRECT TIME
                    else {
                        targetHour = hr;
                        targetMinute = min;
                        correctTime = true;
                        editTime.setFocusable(false);
                        setButtonColourGreen(timeButton);
                        editAutoContact.setFocusableInTouchMode(true);
                    }
                }
                // there's no colon in the centre
                else {
                    toastMessage("Please separate hrs and min with ':'");
                }
            } else {
                toastMessage("Please enter a 5 character time");
            }
        }



    }

    public void setContact(View view){
        if(correctTime&&!correctContact){
            String contact = editAutoContact.getText().toString();
            // check if a valid contact is entered
            int index = names.indexOf(contact);
            if(index!=-1){
                // if it is a valid phone number
                if(PhoneNumberUtils.isWellFormedSmsAddress(numbers.get(index))){
                    phoneNumber = numbers.get(index);
                    Toast.makeText(this,phoneNumber, Toast.LENGTH_SHORT).show();
                    editAutoContact.setFocusable(false);
                    editMessage.setFocusableInTouchMode(true);
                    allowSchedule = true;
                    correctContact = true;
                    // prevents the users contacts from being scanned in again
                    hasReadContacts = true;
                    setButtonColourGreen(contactButton);
                }else{
                    toastMessage("The entered contact does not have a valid number");
                }

            }else{
                toastMessage("Please enter a valid contact");
            }
        }


    }


    public void reset(View view){
        // lock all textviews
        editYear.setFocusableInTouchMode(true);
        editAutoMonth.setFocusable(false);
        editDay.setFocusable(false);
        editTime.setFocusable(false);
        editMessage.setFocusable(false);
        editAutoContact.setFocusable(false);
        // none of the info entered from the client has been verified
        correctYear = false;
        correctMonth = false;
        correctDay = false;
        correctTime = false;
        correctContact = false;
        allowSchedule = false;
        inDozeSet = false;
        // reset the colours of the buttons to red
        setButtonColourRed(yearButton);
        setButtonColourRed(monthButton);
        setButtonColourRed(dayButton);
        setButtonColourRed(timeButton);
        setButtonColourRed(contactButton);
        setButtonColourRed(sendButton);
        // remove check mark and reset text
        yearButton.setText("SET");
        monthButton.setText("SET");
        dayButton.setText("SET");
        timeButton.setText("SET");
        contactButton.setText("SET");


    }

    public void callToSchedule(View view){
        // only run if they have scheduled 3 or less messages
        if(numScheduled<3&&correctYear&&correctMonth&&correctDay&&correctTime&&correctContact&&allowSchedule){
            // get message
            String message = editMessage.getText().toString();
            if(!message.equals("")){
                // only run if all times are proper
                if(correctYear&&correctMonth&&correctDay&&correctTime&&correctContact&&allowSchedule){

                    scheduleBackend(createJobInfo(calculateTimeUntil(targetYear,monthIndex,targetDay,targetHour,targetMinute)));
                    setButtonColourGreen(sendButton);
                    toastMessage("Your message has been scheduled");
                }else{
                    toastMessage("Message cannot be scheduled with current configuration");
                }
            }
            else{
                if(!allowSchedule){
                    toastMessage("Two messages cannot be scheduled at the same time");
                }else{
                    toastMessage("Message cannot be scheduled with an empty message");
                }

            }
            allowSchedule = false;
            numScheduled++;
        }else{
            if(numScheduled>3){
                toastMessage("You cannot schedule more than 3 messages");
            }else{
                toastMessage("Message cannot be scheduled with current configuration");
            }
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
            toastMessage("The entered date has passed");
        }
        return waitTime;
    }


    JobInfo createJobInfo(int timeWait){
        ComponentName service = new ComponentName(this, JobScheduleService.class);
        // Create a string array to store the message, number, and contact name
        String[] clientInfo = {editMessage.getText().toString(), phoneNumber, editAutoContact.getText().toString()};
        // create a bundle to pass the client info to jobSchedulerService
        PersistableBundle text = new PersistableBundle();
        text.putStringArray("Client", clientInfo);
        // create the job
        // if the job will be performed in a dozed state
        if(inDoze){
            info = new JobInfo.Builder(uniqueID, service).setExtras(text).setRequiresDeviceIdle(true)
                    .setMinimumLatency(timeWait).setOverrideDeadline(timeWait+1)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_CELLULAR).build();
        }else {
            info = new JobInfo.Builder(uniqueID, service).setExtras(text)
                    .setMinimumLatency(timeWait).setOverrideDeadline(timeWait).setRequiredNetworkType(JobInfo.NETWORK_TYPE_CELLULAR).build();
        }


        // make a new ID
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