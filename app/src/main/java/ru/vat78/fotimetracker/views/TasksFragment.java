package ru.vat78.fotimetracker.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.MainActivity;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.adapters.TasksAdapter;
import ru.vat78.fotimetracker.model.Task;

/**
 * Created by vat on 02.12.2015.
 */
public class TasksFragment extends Fragment {

    private App MainApp;
    private MainActivity mainActivity;
    private RecyclerView mList;
    private TasksAdapter tasksAdapter;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        MainApp = (App) mainActivity.getApplication();

        tasksAdapter = new TasksAdapter(MainApp, this);
        mainActivity.setTasks(tasksAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fott__tasksfragment, container, false);

        mList = (RecyclerView) rootView.findViewById(R.id.tasksView);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        mList.setLayoutManager(llm);

        tasksAdapter.load();
        mList.setAdapter(tasksAdapter);
        if (MainApp.getCurTimeslot()>0) mainActivity.setCurrentFragment(2);
        return rootView;
    }


    public void onItemClicked(Task task) {
        MainApp.setCurTask(task.getId());
        mainActivity.setCurrentFragment(2);
    }

}
