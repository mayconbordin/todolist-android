package org.todolist;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TaskHolder {
    View base;
    TextView title = null;
    Button delete  = null;

    public TaskHolder(View base) {
        this.base = base;
    }

    public TextView getTitle() {
        if (title == null) {
            title = (TextView) base.findViewById(R.id.rowTextView);
        }
        return title;
    }

    public Button getDelete() {
        if (delete == null) {
            delete = (Button) base.findViewById(R.id.btnDelete);
        }
        return delete;
    }
}
