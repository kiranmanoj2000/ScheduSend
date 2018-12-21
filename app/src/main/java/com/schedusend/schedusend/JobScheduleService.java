package com.schedusend.schedusend;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.telephony.SmsManager;
import android.widget.Toast;

public class JobScheduleService extends JobService {
private boolean jobWorking = false;
public boolean jobCanel = false;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        jobWorking = true;
        //useThread(jobParameters);
        deliverMessage(jobParameters.getExtras().getString("Text"), jobParameters);
        return jobWorking;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobCanel = true;
        boolean reschedule = jobWorking;
        jobFinished(jobParameters, reschedule);
        return reschedule;
    }




    private void deliverMessage(String text, JobParameters parameters){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("5195751229", null, ""+ text, null, null);
            Toast.makeText(this, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception error) {
            Toast.makeText(this,error.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            error.printStackTrace();
        }
        if(jobCanel){
            return;
        }
        jobWorking = false;
        boolean reschedule = false;
        jobFinished(parameters, reschedule);

    }

    private void useThread(final JobParameters parameter){
        new Thread(new Runnable() {
            public void run() {
                deliverMessage(parameter.getExtras().getString("Text"), parameter);
            }
        }).start();
        

    }


}
