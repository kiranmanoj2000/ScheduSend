package com.schedusend.schedusend;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

public class JobScheduleService extends JobService {
private boolean jobWorking = false;
public boolean jobCancel = false;

private NotificationManager notifyManager;
private NotificationChannel channel;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        jobWorking = true;
        //useThread(jobParameters);
        deliverMessage(jobParameters.getExtras().getStringArray("Client"), jobParameters);
        return jobWorking;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobCancel = true;
        boolean reschedule = jobWorking;
        jobFinished(jobParameters, reschedule);
        return reschedule;
    }



    private void deliverMessage(String[] clientInfo, JobParameters parameters){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            Toast.makeText(this, clientInfo[0],
                    Toast.LENGTH_LONG).show();
            Toast.makeText(this, clientInfo[1],
                    Toast.LENGTH_LONG).show();
            Toast.makeText(this, clientInfo[2],
                    Toast.LENGTH_LONG).show();


            smsManager.sendTextMessage(""+clientInfo[1], null, ""+ clientInfo[0], null, null);
            notifyUser(clientInfo);
        } catch (Exception error) {
            Toast.makeText(this, error.getMessage(),
                    Toast.LENGTH_LONG).show();
            error.printStackTrace();
        }
        if(jobCancel){
            return;
        }
        jobWorking = false;
        boolean reschedule = false;
        jobFinished(parameters, reschedule);

    }

    public void notifyUser(String[] clientInfo){
        channel = new NotificationChannel("Channel_1", "Notify", NotificationManager.IMPORTANCE_HIGH);
        notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyManager.createNotificationChannel(channel);
        Notification notify = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle("Message Sent To " + clientInfo[2]).setContentText(clientInfo[0])
                .setChannelId("Channel_1").build();
        notifyManager.notify((int) (Math.random()*1001), notify);

    }

   // private void useThread(final JobParameters parameter){
     //   new Thread(new Runnable() {
       //     public void run() {
         //       android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
           //     try{
             //       deliverMessage(parameter.getExtras().getStringArray("Client"), parameter);

               // } catch (Exception error){
                 //   error.printStackTrace();
                //}
          //  }
        //}).start();
        

    //}


}
