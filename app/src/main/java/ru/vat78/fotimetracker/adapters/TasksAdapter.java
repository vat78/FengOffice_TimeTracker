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

import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.database.DaoTasks;
import ru.vat78.fotimetracker.model.Task;
import ru.vat78.fotimetracker.views.TasksFragment;

/**
 * Created by vat on 02.12.2015.
 */
public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {

    private List<Task> tasks;
    private App app;

    private TasksFragment parent;


    public static class TasksViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView duedate;

        private TasksFragment parent;

        public TasksViewHolder(View itemView, TasksFragment parent) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.textTaskTitle);
            duedate = (TextView)itemView.findViewById(R.id.textDueDate);
            this.parent = parent;
        }
    }

    public TasksAdapter(App application, TasksFragment parent) {
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

        final Task objectItem = tasks.get(i);

        taskViewHolder.title.setText(objectItem.getName());
        Date d = objectItem.getDueDate();
        taskViewHolder.duedate.setText(app.getDateFormat().format(d));

        taskViewHolder.title.setSelected(app.getCurTask() == objectItem.getUid());
        taskViewHolder.duedate.setSelected(app.getCurTask() == objectItem.getUid());
        
        if (objectItem.isDeleted() || objectItem.getStatus() == 1){
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
        return tasks.get(position).getUid();
    }

    public void load(){
        //this.tasks = DaoTasks.load(app,"");
    }

    public Task getTaskById(long id){
        return null; // DaoTasks.getTaskById(app, id);
    }
    
    public void onClickTask(Task selection){
        if (parent != null) {
            parent.onItemClicked(selection);
        }
    }
}
