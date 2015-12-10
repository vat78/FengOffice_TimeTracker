package ru.vat78.fotimetracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.database.FOTT_DBContract;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.views.FOTT_TasksFragment;

/**
 * Created by vat on 02.12.2015.
 */
public class FOTT_TasksAdapter extends RecyclerView.Adapter<FOTT_TasksAdapter.TasksViewHolder> {

    private List<FOTT_Task> tasks;
    private Context context;
    private FOTT_App app;

    private FOTT_TasksFragment parent;


    public static class TasksViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView title;
        public TextView duedate;

        private FOTT_TasksFragment parent;

        public TasksViewHolder(View itemView, FOTT_TasksFragment parent) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.textTaskTitle);
            duedate = (TextView)itemView.findViewById(R.id.textDueDate);
            this.parent = parent;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (parent != null) {
                parent.onItemClicked(getPosition());
            }

        }

    }

    public FOTT_TasksAdapter(FOTT_App application, FOTT_TasksFragment parent) {
        //this.context = context;
        this.app = application;
        this.tasks = new ArrayList<>();

        this.parent = parent;
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public TasksViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_list_item, viewGroup, false);
        TasksViewHolder vh = new TasksViewHolder(v, parent);
        return vh;
    }

    @Override
    public void onBindViewHolder(TasksViewHolder taskViewHolder, int i) {

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(app.getApplicationContext());

        FOTT_Task objectItem = tasks.get(i);

        taskViewHolder.title.setText(objectItem.getName());
        Date d = objectItem.getDueDate();
        taskViewHolder.duedate.setText(dateFormat.format(d));

        taskViewHolder.title.setSelected(app.getCurTask() == objectItem.getId());
        taskViewHolder.duedate.setSelected(app.getCurTask() == objectItem.getId());

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public long getTaskId(int position){
        return tasks.get(position).getId();
    }

    public void load(){
        SQLiteDatabase db = app.getDatabase();

        this.tasks = new ArrayList<>();
        String memFilter = null;
        if (app.getCurMember() > 0) {
            //memFilter = " " + FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_MEMBERS + " LIKE '%\"" + app.getCurMember() + "\"%' OR " +
            //    FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_TASK_ID + " = 0";
            memFilter = " " + FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_TASK_ID + " IN ( SELECT " +
                    FOTT_DBContract.FOTT_DBObject_Members.COLUMN_OBJECT_ID + " FROM " +
                    FOTT_DBContract.FOTT_DBObject_Members.TABLE_NAME + " WHERE " +
                    FOTT_DBContract.FOTT_DBObject_Members.COLUMN_MEMBER_ID + " = " +
                    String.valueOf(app.getCurMember()) + " AND " +
                    FOTT_DBContract.FOTT_DBObject_Members.COLUMN_OBJECT_TYPE + " = 1)";
        }
        Cursor taskCursor = db.query(FOTT_DBContract.FOTT_DBTasks.TABLE_NAME,
                new String[]{FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_TASK_ID,
                        FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_TITLE,
                        FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_DUEDATE},
                        memFilter, null, null, null,
                FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_DUEDATE, null);

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

    public FOTT_Task getTaskById(long id){
        SQLiteDatabase db = app.getDatabase();
        FOTT_Task res = new FOTT_Task(0,"");

        if (id>0){
            String filter = " " + FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_TASK_ID + " = " + id;
            Cursor taskCursor = db.query(FOTT_DBContract.FOTT_DBTasks.TABLE_NAME,
                    new String[]{FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_TASK_ID,
                            FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_TITLE,
                            FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_DUEDATE,
                            FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_DESC},
                    filter, null, null, null,
                    FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_DUEDATE, null);
            taskCursor.moveToFirst();
            if (!taskCursor.isAfterLast()){
                res.setId(taskCursor.getLong(0));
                res.setName(taskCursor.getString(1));
                res.setDuedate(taskCursor.getLong(2));
                res.setDesc(taskCursor.getString(3));
            }
        }
        return res;
    }
}
