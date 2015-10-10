package org.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class PollReceiver extends BroadcastReceiver {
    private static final int PERIOD        = 1000;//900000; // 15 minutes
    private static final int INITIAL_DELAY = 0;//5000; // 5 seconds

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            context.startService(new Intent(context, CheckTasksService.class));
        } else {
            scheduleAlarms(context);
        }
    }

    public static void scheduleAlarms(Context ctxt) {
        AlarmManager mgr = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(ctxt, PollReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);

        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + INITIAL_DELAY,
                PERIOD, pi);
    }
}
