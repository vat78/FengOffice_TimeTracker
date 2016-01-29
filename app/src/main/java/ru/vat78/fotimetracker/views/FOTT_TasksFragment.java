package ru.vat78.fotimetracker.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.FOTT_MainActivity;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.adapters.FOTT_TasksAdapter;
import ru.vat78.fotimetracker.model.FOTT_Task;

/**
 * Created by vat on 02.12.2015.
 */
public class FOTT_TasksFragment extends Fragment {

    private FOTT_App MainApp;
    private FOTT_MainActivity mainActivity;
    private RecyclerView mList;
    private FOTT_TasksAdapter tasksAdapter;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mainActivity = (FOTT_MainActivity) context;
        MainApp = (FOTT_App) mainActivity.getApplication();

        tasksAdapter = new FOTT_TasksAdapter(MainApp, this);
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


    public void onItemClicked(FOTT_Task task) {
        MainApp.setCurTask(task.getId());
        mainActivity.setCurrentFragment(2);
    }

}
