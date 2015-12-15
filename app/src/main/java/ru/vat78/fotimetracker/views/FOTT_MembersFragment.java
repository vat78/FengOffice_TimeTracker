package ru.vat78.fotimetracker.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.FOTT_MainActivity;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.adapters.FOTT_MembersAdapter;

/**
 * Created by vat on 30.11.2015.
 */
public class FOTT_MembersFragment extends Fragment {

    private FOTT_App MainApp;
    private FOTT_MainActivity mainActivity;
    private ListView mList;
    private FOTT_MembersAdapter membersAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fott__membersfragment, container, false);

        mainActivity = (FOTT_MainActivity) getActivity();
        MainApp = (FOTT_App) mainActivity.getApplication();

        membersAdapter = mainActivity.getMembers();
        membersAdapter.load();
        mList = (ListView) rootView.findViewById(R.id.membersView);
        mList.setAdapter(membersAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id){
                MainApp.setCurMember(membersAdapter.getMemberId(position));
                MainApp.setCurTask(0);
                membersAdapter.notifyDataSetChanged();
                mainActivity.setCurrentFragment(1);
            }
        });
        return rootView;
    }

}
