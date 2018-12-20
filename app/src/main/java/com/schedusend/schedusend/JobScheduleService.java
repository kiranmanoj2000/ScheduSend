package com.schedusend.schedusend;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.telephony.SmsManager;
import android.widget.Toast;

public class JobScheduleService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        // sending message
        deliverMessage();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    void deliverMessage(){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("5195751229", null, "Good Morning!", null, null);
            //Toast.makeText(this, "Message Sent",
            //       Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(this,ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
