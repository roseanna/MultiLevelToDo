package com.example.roseanna.todomultitable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by roseanna on 3/19/16.
 */
public class TaskAdapter extends ArrayAdapter implements Serializable {
    private final Context context;
    private final ArrayList<ToDoList> tasks;

    public TaskAdapter(Context context, ArrayList<ToDoList> tasks) {
        super(context, R.layout.checkboxrow, tasks);
        this.context = context;
        this.tasks = tasks;
    }
    public ArrayList<ToDoList> getTasks(){
        return this.tasks;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater       = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView                  = inflater.inflate(R.layout.checkboxrow, parent, false);
        final CheckBox cb             = (CheckBox) rowView.findViewById(R.id.checkbox);

        cb.setText(tasks.get(position).getTitle());

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb.isChecked())
                    tasks.get(position).set();
                else if (!cb.isChecked())
                    tasks.get(position).unset();
            }
        });
        return rowView;
    }

}
