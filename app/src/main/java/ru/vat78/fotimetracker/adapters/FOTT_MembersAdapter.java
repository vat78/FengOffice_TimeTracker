package ru.vat78.fotimetracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.database.FOTT_DBContract;
import ru.vat78.fotimetracker.database.FOTT_DBMembers;
import ru.vat78.fotimetracker.model.FOTT_Member;
import ru.vat78.fotimetracker.views.FOTT_MembersFragment;

/**
 * Created by vat on 30.11.2015.
 */
public class FOTT_MembersAdapter extends RecyclerView.Adapter <FOTT_MembersAdapter.MembersViewHolder> {

    private List<FOTT_Member> members;
    private Context context;
    private FOTT_App app;
    private FOTT_MembersFragment parent;

    private int memColors[] = {Color.GRAY,Color.DKGRAY,Color.RED,Color.BLUE,Color.GREEN,Color.MAGENTA,Color.CYAN,Color.YELLOW,
            Color.GRAY,Color.DKGRAY,Color.RED,Color.BLUE,Color.GREEN,Color.MAGENTA,Color.CYAN,Color.YELLOW,
            Color.GRAY,Color.DKGRAY,Color.RED,Color.BLUE,Color.GREEN,Color.MAGENTA,Color.CYAN,Color.YELLOW,Color.WHITE};

    public static class MembersViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // each data item is just a string in this case
        private TextView title ;
        private TextView color;
        private TextView margine;
        private TextView tasks;

        private FOTT_MembersFragment parent;

        public MembersViewHolder(View itemView, FOTT_MembersFragment parent) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textMemName);
            color = (TextView) itemView.findViewById(R.id.textMemColor);
            margine = (TextView) itemView.findViewById(R.id.textMargin);
            tasks = (TextView) itemView.findViewById(R.id.textMemTasks);
            this.parent = parent;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (parent != null) {
                parent.onItemClicked(getPosition());
            }

        }

    }

    public FOTT_MembersAdapter(FOTT_App application, FOTT_MembersFragment parent) {
        super();
        //this.context = context;
        this.app = application;
        this.members = new ArrayList<>();
        this.parent = parent;
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public MembersViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.member_list_item, viewGroup, false);
        MembersViewHolder vh = new MembersViewHolder(v, parent);
        return vh;
    }

    @Override
    public void onBindViewHolder(MembersViewHolder memberViewHolder, int i) {

        FOTT_Member objectItem = members.get(i);

        memberViewHolder.title.setText("   " + objectItem.getName());
        memberViewHolder.tasks.setText(String.valueOf(objectItem.getTasksCnt()));
        memberViewHolder.color.setBackgroundColor(memColors[objectItem.getColor()]);

        memberViewHolder.margine.setWidth(36 * objectItem.getLevel());

        if (app.getCurMember() == objectItem.getId()) {
            memberViewHolder.title.setBackgroundColor(memColors[objectItem.getColor()]);
            memberViewHolder.tasks.setBackgroundColor(memColors[objectItem.getColor()]);
        }
        /*
        if (objectItem.isVisible()){
            memberViewHolder.setVisibility(View.VISIBLE);
        } else {
            memberViewHolder.setVisibility(View.INVISIBLE);
        } */
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



    public void load(){
        members = FOTT_DBMembers.load(app);
    }

    public long getMemberId(int position){
        return members.get(position).getId();
    }

    public FOTT_Member getMemberById(long id){
        return FOTT_DBMembers.getMemberById(app,id);
    }
}
