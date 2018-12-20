package com.schedusend.schedusend;
import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import static android.Manifest.permission.SEND_SMS;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        schedule(createJobInfo(calculateTimeUntil()));
    }


    int calculateTimeUntil(){
        Date targetDay = new Date(118,11,20,11,30);
        int waitTime = 0;
        if(targetDay.getTime()>Calendar.getInstance().getTimeInMillis()){
            waitTime = (int) (targetDay.getTime()-Calendar.getInstance().getTimeInMillis());
        }
        //if entered date is before current date
        if(waitTime<0){
            Toast.makeText(this,"The entered date has passed",Toast.LENGTH_LONG).show();;
        }
        return waitTime;
    }


    JobInfo createJobInfo(int timeWait){
        ComponentName service = new ComponentName(this, JobScheduleService.class);
        JobInfo info = new JobInfo.Builder(101, service).setMinimumLatency(10000).build();
        if(timeWait<0){
            return null;
        } else {
            return info;
        }

    }

    void schedule(JobInfo info){
        JobScheduler scheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);
    }

    void requestPermissions(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
    }
}