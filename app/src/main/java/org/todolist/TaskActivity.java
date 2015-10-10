package org.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class TaskActivity extends AppCompatActivity
        implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    public static final String TASK_ID = "taskId";

    private enum Update {
        ALL, TIME, DATE
    };

    private EditText edtTaskTitle;
    private TextView txtTaskDate;
    private TextView txtTaskTime;
    private Button btnSaveTask;

    private DBHelper mDbHelper;
    private Task task;

    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        edtTaskTitle = (EditText) findViewById(R.id.edtTaskTitle);
        txtTaskDate = (TextView) findViewById(R.id.txtTaskDate);
        txtTaskTime = (TextView) findViewById(R.id.txtTaskTime);
        btnSaveTask  = (Button) findViewById(R.id.btnSaveTask);

        mDbHelper = new DBHelper(this);

        final Intent intent = getIntent();
        long id = intent.getLongExtra(TASK_ID, -1);
        task = mDbHelper.getTask(id);

        // check if task has a due date
        if (task.getDate() != null) {
            calendar.setTime(task.getDate());
            calendarToView(Update.ALL);
        }

        setTitle(task.getTitle());
        edtTaskTitle.setText(task.getTitle());

        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setTitle(edtTaskTitle.getText().toString());
                task.setDate(calendar.getTime());

                Log.i("TaskActivity", "Date="+calendar.getTime());

                mDbHelper.updateTask(task);

                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, intent);
                } else {
                    getParent().setResult(Activity.RESULT_OK, intent);
                }

                finish();
            }
        });

        txtTaskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setListener(TaskActivity.this);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        txtTaskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setListener(TaskActivity.this);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task, menu);
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

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        calendarToView(Update.TIME);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear + 1);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        calendarToView(Update.DATE);
    }

    /**
     * Update date and/or time on view from the calendar.
     * @param op
     */
    private void calendarToView(Update op) {
        if (op == Update.DATE || op == Update.ALL) {
            String date = String.format("%d/%d/%d", calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
            txtTaskDate.setText(date);
        }

        if (op == Update.TIME || op == Update.ALL) {
            String time = String.format("%d:%d:00", calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));
            txtTaskTime.setText(time);
        }
    }
}
