package com.schedusend.schedusend;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import androidx.core.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

public class JobScheduleService extends JobService {

private boolean jobWorking = false;
public boolean jobCancel = false;

private SmsManager smsManager;
private NotificationManager notifyManager;
private NotificationChannel channel;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        jobWorking = true;
        useThread(jobParameters);
        return jobWorking;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobCancel = true;
        // if the job is done, it does not need to be rescheduled and vice versa
        boolean reschedule = jobWorking;
        // let the system know the job has been finished
        jobFinished(jobParameters, reschedule);
        // let the system know if it needs to be reschedueled
        return reschedule;
    }



    private void deliverMessage(String[] clientInfo, JobParameters parameters){
        if(jobCancel){
            // the job has been cancelled by the system do not run the task
            return;
        }
        try {
            smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(""+clientInfo[1], null, ""+ clientInfo[0], null, null);
            notifyUser(clientInfo);
        } catch (Exception error) {
            error.printStackTrace();
        }
        jobWorking = false;
        // let the system know that the job has completed and does not need to be rescheduled
        jobFinished(parameters, jobWorking);

    }

    public void notifyUser(String[] clientInfo){
        channel = new NotificationChannel("Channel_8", "Notify", NotificationManager.IMPORTANCE_HIGH);
        notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyManager.createNotificationChannel(channel);
        Notification notify = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle("Message Sent To " + clientInfo[2]).setContentText(clientInfo[0])
                .setChannelId("Channel_8").build();
        notifyManager.notify((int) (Math.random()*1001), notify);

    }

    private void useThread(final JobParameters parameter){
        // run the job on a worker thread
        new Thread(new Runnable() {
            public void run() {
                // put the thread in the background
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                try{
                    // deliver the message
                    deliverMessage(parameter.getExtras().getStringArray("Client"), parameter);

                } catch (Exception error){
                    error.printStackTrace();
                }
            }
        }).start();


    }


}
