package org.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TodoList.db";

    private static final String SQL_DELETE_ENTRIES =  "DROP TABLE IF EXISTS tasks";

    private static final String[] SQL_CREATE_ENTRIES = new String[] {
        "CREATE TABLE tasks (" +
                "id INTEGER PRIMARY KEY autoincrement, " +
                "title TEXT NOT NULL, " +
                "date INTEGER" +
        "); "
    };

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String sqlCreate : SQL_CREATE_ENTRIES) {
            db.execSQL(sqlCreate);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public Task insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("title", task.getTitle());

        if (task.getDate() != null) {
            contentValues.put("date", task.getDate().getTime());
        } else {
            contentValues.putNull("date");
        }

        long newRowId = db.insert("tasks", null, contentValues);
        task.setId(newRowId);

        return task;
    }

    public boolean updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("title", task.getTitle());

        if (task.getDate() != null) {
            contentValues.put("date", task.getDate().getTime());
        } else {
            contentValues.putNull("date");
        }

        db.update("tasks", contentValues, "id = ? ", new String[]{Long.toString(task.getId())});

        return true;
    }

    public Task getTask(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from tasks where id=" + id + "", null);
        res.moveToFirst();

        Task task = new Task();
        task.setId(res.getLong(0));
        task.setTitle(res.getString(1));
        task.setDate(res.isNull(2) ? null : new Date(res.getLong(2)));

        return task;
    }

    public List<Task> getAllTasks() {
        ArrayList<Task> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from tasks", null);
        res.moveToFirst();

        while(!res.isAfterLast()) {
            list.add(new Task(res.getLong(0), res.getString(1)));
            res.moveToNext();
        }

        res.close();

        return list;
    }

    public List<Task> getTasksByDate(long date) {
        ArrayList<Task> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from tasks where date > " + date + "", null);
        res.moveToFirst();

        while(!res.isAfterLast()) {
            list.add(new Task(res.getLong(0), res.getString(1)));
            res.moveToNext();
        }

        res.close();

        return list;
    }

    public Integer deleteTask(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("tasks", "id = ? ", new String[] { Long.toString(id) });
    }
}
