package org.todolist;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int EDIT_TASK_REQUEST = 1;

    private ListView listView;
    private Button btnAddTask;
    private EditText edtTaskTitle;
    private ProgressBar spinner;
    private TaskAdapter adapter;
    private List<Task> tasks = new ArrayList<>();
    private DBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        btnAddTask = (Button) findViewById(R.id.btnAddTask);
        edtTaskTitle = (EditText) findViewById(R.id.edtTaskTitle);
        spinner = (ProgressBar) findViewById(R.id.progressBar);

        mDbHelper = new DBHelper(this);
        //tasks = mDbHelper.getAllTasks();

        adapter = new TaskAdapter(this, tasks);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = (Task) listView.getItemAtPosition(position);

                Log.i(TAG, "Task: " + task.getTitle());

                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                intent.putExtra(TaskActivity.TASK_ID, task.getId());
                startActivityForResult(intent, 1);
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = new Task();
                task.setTitle(edtTaskTitle.getText().toString());
                mDbHelper.insertTask(task);

                adapter.add(task);
                adapter.notifyDataSetChanged();
            }
        });

        PollReceiver.scheduleAlarms(this);

        new GetTaskList().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_TASK_REQUEST) {
            if (resultCode == RESULT_OK) {
                tasks.clear();
                tasks.addAll(mDbHelper.getAllTasks());
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class GetTaskList extends AsyncTask<Void, Void, List<Task>> {
        @Override
        protected List<Task> doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            return mDbHelper.getAllTasks();
        }

        @Override
        protected void onPostExecute(List<Task> list) {
            spinner.setVisibility(View.GONE);
            tasks.addAll(list);
            adapter.notifyDataSetChanged();
        }
    }
}
