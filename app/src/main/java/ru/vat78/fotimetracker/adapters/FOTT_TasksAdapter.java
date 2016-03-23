package ru.vat78.fotimetracker.adapters;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.connectors.database.FOTT_DBTasks;
import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Exceptions;
import ru.vat78.fotimetracker.controllers.FOTT_Exceptions;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.views.FOTT_TasksFragment;

/**
 * Created by vat on 02.12.2015.
 */
public class FOTT_TasksAdapter extends RecyclerView.Adapter<FOTT_TasksAdapter.TasksViewHolder> {

    private List<FOTT_Task> tasks;
    private FOTT_App app;

    private FOTT_TasksFragment parent;


    public static class TasksViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView duedate;

        private FOTT_TasksFragment parent;

        public TasksViewHolder(View itemView, FOTT_TasksFragment parent) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.textTaskTitle);
            duedate = (TextView)itemView.findViewById(R.id.textDueDate);
            this.parent = parent;
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
        return new TasksViewHolder(v, parent);
    }

    @Override
    public void onBindViewHolder(TasksViewHolder taskViewHolder, int i) {

        final FOTT_Task objectItem = tasks.get(i);

        taskViewHolder.title.setText(objectItem.getName());
        Date d = objectItem.getDueDate();
        taskViewHolder.duedate.setText(app.getDateFormat().format(d));

        taskViewHolder.title.setSelected(app.getCurTask() == objectItem.getWebId());
        taskViewHolder.duedate.setSelected(app.getCurTask() == objectItem.getWebId());

        if (objectItem.isDeleted() || objectItem.getStatus() == FOTT_Task.STATUS_COMPLETED){
            taskViewHolder.title.setPaintFlags(taskViewHolder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        taskViewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTask(objectItem);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public long getTaskId(int position){
        return tasks.get(position).getWebId();
    }

    public void load(){
        try {
            this.tasks = (ArrayList<FOTT_Task>) new FOTT_DBTasks(app.getDatabase()).loadObjects();
        } catch (FOTT_Exceptions e) {}
    }

    public FOTT_Task getTaskById(long id){

        FOTT_Task result = null;
        try {
            result = (FOTT_Task) new FOTT_DBTasks(app.getDatabase()).loadObject(id);
        } catch (FOTT_Exceptions e) {}
        return result;
    }

    public void onClickTask(FOTT_Task selection){
        if (parent != null) {
            parent.onItemClicked(selection);
        }
    }
}
