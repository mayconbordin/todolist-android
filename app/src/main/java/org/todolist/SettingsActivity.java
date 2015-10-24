package org.todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private CheckBox cbBgService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        cbBgService = (CheckBox) findViewById(R.id.cbBgService);

        // get instance of app shared preferences
        sharedPreferences = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);

        // set default option, if not set default to true
        cbBgService.setChecked(sharedPreferences.getBoolean(Constants.RUN_BG_SERVICE, true));

        // listen to changes on the checkbox
        cbBgService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeRunBackgroundService(isChecked);
            }
        });
    }

    private void changeRunBackgroundService(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.RUN_BG_SERVICE, value);

        if (value) {
            PollReceiver.scheduleAlarms(this);
        } else {
            PollReceiver.cancelAlarms(this);
        }
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
