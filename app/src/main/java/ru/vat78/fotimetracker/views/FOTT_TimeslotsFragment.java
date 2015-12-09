package ru.vat78.fotimetracker.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.FOTT_MainActivity;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.adapters.FOTT_TimeslotsAdapter;

/**
 * Created by vat on 04.12.2015.
 */
public class FOTT_TimeslotsFragment extends Fragment {
    private FOTT_App MainApp;
    private FOTT_MainActivity mainActivity;
    private RecyclerView mList;
    private FOTT_TimeslotsAdapter tsAdapter;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mainActivity = (FOTT_MainActivity) context;
        MainApp = (FOTT_App) mainActivity.getApplication();

        tsAdapter = new FOTT_TimeslotsAdapter(mainActivity,MainApp);
        mainActivity.setTimeslots(tsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fott__timeslotsfragment, container, false);

        mainActivity = (FOTT_MainActivity) getActivity();
        MainApp = (FOTT_App) mainActivity.getApplication();

        mList = (RecyclerView) rootView.findViewById(R.id.tsListView);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        mList.setLayoutManager(llm);

        tsAdapter.load();
        mList.setAdapter(tsAdapter);

        return rootView;
    }
}
