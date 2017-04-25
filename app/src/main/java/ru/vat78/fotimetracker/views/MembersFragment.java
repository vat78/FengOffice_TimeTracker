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
import ru.vat78.fotimetracker.adapters.MembersAdapter;
import ru.vat78.fotimetracker.model.Member;

/**
 * Created by vat on 30.11.2015.
 */
public class MembersFragment extends Fragment {

    private App MainApp;
    private MainActivity mainActivity;
    private RecyclerView mList;
    private MembersAdapter membersAdapter;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        MainApp = (App) mainActivity.getApplication();

        membersAdapter = new MembersAdapter(MainApp, this);
        mainActivity.setMembers(membersAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fott__membersfragment, container, false);

        mList = (RecyclerView) rootView.findViewById(R.id.membersView);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        mList.setLayoutManager(llm);

        membersAdapter.load();
        mList.setAdapter(membersAdapter);
        if (MainApp.getCurTimeslot()>0) mainActivity.setCurrentFragment(1);
        return rootView;
    }

    public void onMemberSelect(Member selection) {
        MainApp.setCurMember(selection.getUid());
        MainApp.setCurTask(0);
        membersAdapter.notifyDataSetChanged();
        mainActivity.setCurrentFragment(1);
    }
}
