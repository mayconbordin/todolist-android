package org.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {
    public TaskAdapter(Context context, List<Task> objects) {
        super(context, R.layout.list_row, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        TaskHolder holder = null;

        if (view == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.list_row, parent, false);
            holder = new TaskHolder(view);
            view.setTag(holder);
        } else {
            holder = (TaskHolder) view.getTag();
        }

        final Task item = getItem(position);

        if (item != null) {
            holder.getTitle().setText(item.getTitle());
            holder.getDelete().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(item);
                    notifyDataSetChanged();
                }
            });
        }

        return view;
    }
}
