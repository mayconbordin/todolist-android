package org.todolist;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Task {
    private static final String TAG = "Task";
    private long id;
    private String title;
    private Date date;

    public Task(){}

    public Task(long id, String title) {
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("id", id);
            json.put("title", title);
            json.put("date", date);
            return json;
        } catch (JSONException e) {
            Log.e(TAG, "Unable to convert task to JSON.", e);
            return null;
        }
    }
}
