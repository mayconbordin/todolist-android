package org.todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private SharedPreferences sharedPreferences;
    private CheckBox cbBgService;
    private TextView txtBackupData;
    private DBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        cbBgService = (CheckBox) findViewById(R.id.cbBgService);
        txtBackupData = (TextView) findViewById(R.id.txtBackupData);

        // get instance of app shared preferences
        sharedPreferences = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);

        // get instance of database helper
        mDbHelper = new DBHelper(this);

        // set default option, if not set default to true
        cbBgService.setChecked(sharedPreferences.getBoolean(Constants.RUN_BG_SERVICE, true));

        // listen to changes on the checkbox
        cbBgService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeRunBackgroundService(isChecked);
            }
        });

        txtBackupData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Backup!");
                backupData();
            }
        });
    }

    private void backupData() {
        if (!isExternalStorageWritable()) {
            Log.e(TAG, "Can't write to external storage.");
            return;
        }

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!dir.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }

        File file = new File(dir, "todo-list.json");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);

            JSONArray tasks = new JSONArray();

            for (Task task : mDbHelper.getAllTasks()) {
                tasks.put(task.toJSON());
            }

            pw.print(tasks.toString());
            pw.flush();
            pw.close();
            f.close();

            Toast.makeText(this, "Backup saved", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found.", e);
        } catch (IOException e) {
            Log.e(TAG, "Unable to write backup file.", e);
        }
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void changeRunBackgroundService(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.RUN_BG_SERVICE, value);

        if (value) {
            PollReceiver.scheduleAlarms(this);
        } else {
            PollReceiver.cancelAlarms(this);
        }

        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
