package org.todolist;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int EDIT_TASK_REQUEST = 1;

    private ListView listView;
    private Button btnAddTask;
    private EditText edtTaskTitle;
    private TaskAdapter adapter;
    private List<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        btnAddTask = (Button) findViewById(R.id.btnAddTask);
        edtTaskTitle = (EditText) findViewById(R.id.edtTaskTitle);

        tasks = new ArrayList<>();
        tasks.add(new Task("Tarefa 1"));
        tasks.add(new Task("Tarefa 2"));
        tasks.add(new Task("Tarefa 3"));
        tasks.add(new Task("Tarefa 4"));

        adapter = new TaskAdapter(this, tasks);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = (Task) listView.getItemAtPosition(position);

                Log.i(TAG, "Task: " + task.getTitle());

                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                intent.putExtra(TaskActivity.TASK_TITLE, task.getTitle());
                intent.putExtra(TaskActivity.TASK_POS, position);
                startActivityForResult(intent, 1);
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = edtTaskTitle.getText().toString();
                adapter.add(new Task(title));
                adapter.notifyDataSetChanged();
            }
        });
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
            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            return true;
        } else if (id == R.id.action_send_message) {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:"));
            sendIntent.putExtra("sms_body", "Just a test");
            startActivity(sendIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_TASK_REQUEST) {
            if (resultCode == RESULT_OK) {
                String title = data.getStringExtra(TaskActivity.TASK_TITLE);
                int pos = data.getIntExtra(TaskActivity.TASK_POS, -1);

                if (pos != -1) {
                    tasks.get(pos).setTitle(title);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
