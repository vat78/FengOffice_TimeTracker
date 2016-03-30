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
import ru.vat78.fotimetracker.adapters.FOTT_MembersAdapter;
import ru.vat78.fotimetracker.model.FOTT_Member;

public class FOTT_MembersFragment extends Fragment {

    private FOTT_App MainApp;
    private FOTT_MainActivity mainActivity;
    private RecyclerView mList;
    private FOTT_MembersAdapter membersAdapter;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mainActivity = (FOTT_MainActivity) context;
        MainApp = (FOTT_App) mainActivity.getApplication();

        membersAdapter = new FOTT_MembersAdapter(this);
        mainActivity.setMembers(membersAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fott__membersfragment, container, false);

        mList = (RecyclerView) rootView.findViewById(R.id.membersView);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        mList.setLayoutManager(llm);

        mList.setAdapter(membersAdapter);
        if (MainApp.getCurTimeslot()>0) mainActivity.setCurrentFragment(1);
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        membersAdapter.load();
    }

    public void onMemberSelect(FOTT_Member selection) {
        MainApp.setCurMember(selection.getWebId());
        MainApp.setCurTask(0);
        mainActivity.setCurrentFragment(1);
    }
}
