package ru.vat78.fotimetracker.views;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.FOTT_MainActivity;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.adapters.FOTT_TimeslotsAdapter;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;

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

        tsAdapter = new FOTT_TimeslotsAdapter(MainApp, this);
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
                if (MainApp.getCurTimeslot() != 0) mainActivity.startStopTimer();
                else mainActivity.editTimeslot(0, 0, 0, "");
            }
        });

        ImageButton start = (ImageButton) rootView.findViewById(R.id.tsTimerBtn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.startStopTimer();
            }
        });

        if (MainApp.getCurTimeslot() != 0) {
            start.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause, mainActivity.getTheme()));
            TextView mTextDuration = (TextView) rootView.findViewById(R.id.tsCurDuration);
            mTextDuration.setVisibility(View.VISIBLE);
            mainActivity.continueTimer();
        }

        return rootView;
    }

    public void showTSDetails(FOTT_Timeslot timeslot) {

        final FOTT_Timeslot ts = timeslot;
        AlertDialog.Builder ad = new AlertDialog.Builder(this.getContext());
        ad.setMessage(ts.getDesc());
        ad.setCancelable(true);
        ad.setNeutralButton(MainApp.getString(R.string.ts_button_edit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                mainActivity.editTimeslot(ts.getWebId(),ts.getStart().getTime(),ts.getDuration(), ts.getDesc());
            }
        });
        ad.show();
    }

    @Override
    public void onResume (){
        super.onResume();

    }
}
