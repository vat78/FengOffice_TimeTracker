package ru.vat78.fotimetracker.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.FOTT_MainActivity;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.adapters.FOTT_TimeslotsAdapter;
import ru.vat78.fotimetracker.model.FOTT_Member;
import ru.vat78.fotimetracker.model.FOTT_Task;

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_fott__timeslotsfragment, container, false);

        mainActivity = (FOTT_MainActivity) getActivity();
        MainApp = (FOTT_App) mainActivity.getApplication();

        mList = (RecyclerView) rootView.findViewById(R.id.tsListView);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        mList.setLayoutManager(llm);

        tsAdapter.load();
        mList.setAdapter(tsAdapter);

        ImageButton add = (ImageButton) rootView.findViewById(R.id.tsAddBtn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showTimeslotAddWindow(inflater, rootView,15);
                mainActivity.editTimeslot(0,0);
            }
        });

        return rootView;
    }

    @Override
    public void onResume (){
        super.onResume();
        TextView top_title = (TextView) mainActivity.findViewById(R.id.tsTopTitle);
        TextView top_desc = (TextView) mainActivity.findViewById(R.id.tsTopDesc);
        if (MainApp.getCurTask() > 0) {
            FOTT_Task t = mainActivity.getTasks().getTaskById(MainApp.getCurTask());
            top_title.setText(t.getName());
            top_desc.setText(t.getDesc());
        } else {
            if (MainApp.getCurMember() > 0) {
                FOTT_Member m = mainActivity.getMembers().getMemberById(MainApp.getCurMember());
                top_title.setText(m.getName());
                top_desc.setText("");
            } else {
                top_title.setText("Please select task or category");
                top_desc.setText("");
            }
        }
    }

}
