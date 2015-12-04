package ru.vat78.fotimetracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.database.FOTT_Contract;
import ru.vat78.fotimetracker.model.FOTT_Task;

/**
 * Created by vat on 02.12.2015.
 */
public class FOTT_TasksAdapter extends ArrayAdapter<String> {

    private List<FOTT_Task> tasks;
    private Context context;
    private FOTT_App app;

    public FOTT_TasksAdapter(Context context, FOTT_App application) {
        super(context, R.layout.task_list_item);
        this.context = context;
        this.app = application;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public String getItem(int position) {
        return tasks.get(position).getName();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(app.getApplicationContext());

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.task_list_item, parent, false);

        TextView title = (TextView) view.findViewById(R.id.textTaskTitle);
        TextView duedate = (TextView) view.findViewById(R.id.textDueDate);

        FOTT_Task objectItem = tasks.get(position);

        title.setText(objectItem.getName());
        duedate.setText(dateFormat.format(objectItem.getDueDate()));

        if (app.getCurTask() == objectItem.getId()) {
            title.setBackgroundColor(Color.GRAY);
            duedate.setBackgroundColor(Color.GRAY);
        }

        return view;
    }

    public long getTaskId(int position){
        return tasks.get(position).getId();
    }

    public void loadTasks(){
        SQLiteDatabase db = app.getDatabase();

        this.tasks = new ArrayList<>();
        String memFilter = null;
        if (app.getCurMember() > 0) {
            memFilter = " " + FOTT_Contract.FOTT_Tasks.COLUMN_NAME_MEMBERS + " LIKE '%\"" + app.getCurMember() + "\"%' OR " +
                FOTT_Contract.FOTT_Tasks.COLUMN_NAME_TASK_ID + " = 0";
        }
        Cursor taskCursor = db.query(FOTT_Contract.FOTT_Tasks.TABLE_NAME,
                new String[]{FOTT_Contract.FOTT_Tasks.COLUMN_NAME_TASK_ID,
                        FOTT_Contract.FOTT_Tasks.COLUMN_NAME_TITLE,
                        FOTT_Contract.FOTT_Tasks.COLUMN_NAME_DUEDATE},
                        memFilter, null, null, null,
                FOTT_Contract.FOTT_Tasks.COLUMN_NAME_DUEDATE, null);

        taskCursor.moveToFirst();
        FOTT_Task m;
        if (!taskCursor.isAfterLast()){
            do {
                long id = taskCursor.getLong(0);
                String name = taskCursor.getString(1);
                long duedate = taskCursor.getLong(2);

                m = new FOTT_Task(id, name);
                m.setDuedate(duedate);

                tasks.add(m);
            } while (taskCursor.moveToNext());
        }
    }
}
