package com.schedusend.schedusend;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
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
        deliverMessage(jobParameters.getExtras().getString("Text"), jobParameters);
        return jobWorking;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobCancel = true;
        boolean reschedule = jobWorking;
        jobFinished(jobParameters, reschedule);
        return reschedule;
    }



    private void deliverMessage(String text, JobParameters parameters){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            // decode message
            // retrieve length of message
            int length = Character.getNumericValue(text.charAt(text.length()-1));
            String message = text.substring(0,length);
            String number = text.substring(length, text.length()-1);
            Toast.makeText(this, message,
                    Toast.LENGTH_LONG).show();
            Toast.makeText(this, number,
                    Toast.LENGTH_LONG).show();
            smsManager.sendTextMessage(""+number, null, ""+ message, null, null);
            notifyUser("Message Sent", message);
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

    public void notifyUser(String contact, String message){
        channel = new NotificationChannel("Channel_1", "Notify", NotificationManager.IMPORTANCE_HIGH);
        notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyManager.createNotificationChannel(channel);
        Notification notify = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(contact).setContentText(message)
                .setChannelId("Channel_1").build();
        notifyManager.notify((int) (Math.random()*1001), notify);

    }

  //  private void useThread(final JobParameters parameter){
    //    new Thread(new Runnable() {
      //      public void run() {
        //        deliverMessage(parameter.getExtras().getString("Text"), parameter);
          //  }
      //  }).start();
        

  //  }


}
