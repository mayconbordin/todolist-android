package org.todolist;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

public class CheckTasksService extends IntentService {
    private static final String TAG = "CheckTasksService";
    private DBHelper mDbHelper;

    public CheckTasksService() {
        super(CheckTasksService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mDbHelper = new DBHelper(getApplicationContext());

        long start = System.currentTimeMillis();
        long end   = start + (1000 * 60 * 60 * 24); // now + 1 dia
        List<Task> pendingTasks = mDbHelper.getTasksByDate(start, end);

        Log.i(TAG, "Pending tasks: "+pendingTasks.size());

        for (Task task : pendingTasks) {
            createNotification(task);
        }
    }

    private void createNotification(Task task) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_drawer)
                .setContentTitle("Pending Task")
                .setContentText("Task: " + task.getTitle());

        Intent resultIntent = new Intent(this, TaskActivity.class);
        resultIntent.putExtra(TaskActivity.TASK_ID, task.getId());

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify((int) task.getId(), mBuilder.build());
    }
}
