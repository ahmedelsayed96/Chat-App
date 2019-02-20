package ahmed.example.com.chatapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import ahmed.example.com.chatapp.ui.LoginActivity;

/**
 * Created by root on 05/10/17.
 */

public class MyJobServices extends JobService {


    @Override
    public boolean onStartJob(JobParameters job) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                                                                0,
                                                                new Intent(this,
                                                                           LoginActivity.class
                                                                ),
                                                                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true)
                    .setContentTitle(getString(R.string.dont_forget))
                    .setContentText(getString(R.string.check_out))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setShowWhen(true);
        manager.notify(1212, notification.build());


        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
