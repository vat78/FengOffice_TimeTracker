package ru.vat78.fotimetracker.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.FOTT_MainActivity;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.adapters.FOTT_TasksAdapter;

/**
 * Created by vat on 02.12.2015.
 */
public class FOTT_TasksFragment extends Fragment {

    private FOTT_App MainApp;
    private FOTT_MainActivity mainActivity;
    private ListView mList;
    private FOTT_TasksAdapter tasksAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fott__tasksfragment, container, false);

        mainActivity = (FOTT_MainActivity) getActivity();
        MainApp = (FOTT_App) mainActivity.getApplication();

        tasksAdapter = mainActivity.getTasks();
        mList = (ListView) rootView.findViewById(R.id.tasksView);
        mList.setAdapter(tasksAdapter);

        return rootView;
    }
}
