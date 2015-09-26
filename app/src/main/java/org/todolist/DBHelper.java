package org.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TodoList.db";

    private static final String SQL_DELETE_ENTRIES =  "DROP TABLE IF EXISTS tasks";

    private static final String[] SQL_CREATE_ENTRIES = new String[] {
        "CREATE TABLE tasks (id INTEGER PRIMARY KEY autoincrement, title TEXT NOT NULL); "
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

    public Task insertTask(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        long newRowId = db.insert("tasks", null, contentValues);
        return new Task(newRowId, title);
    }

    public boolean updateTask(long id, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        db.update("tasks", contentValues, "id = ? ", new String[]{Long.toString(id)});
        return true;
    }

    public Task getTask(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from tasks where id="+id+"", null);
        res.moveToFirst();
        return new Task(res.getLong(0), res.getString(1));
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

    public Integer deleteTask(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("tasks", "id = ? ", new String[] { Long.toString(id) });
    }
}
