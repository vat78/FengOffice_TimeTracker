package ru.vat78.fotimetracker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.database.DaoMembers;
import ru.vat78.fotimetracker.model.Member;
import ru.vat78.fotimetracker.views.MembersFragment;

/**
 * Created by vat on 30.11.2015.
 */
public class MembersAdapter extends RecyclerView.Adapter <MembersAdapter.MembersViewHolder> {

    private List<Member> members;
    private List<Member> visibleMembers;
    private Context context;
    private App app;
    private MembersFragment parent;

    public static class MembersViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView title ;
        private TextView color;
        private TextView margine;
        private TextView tasks;
        private ImageButton selector;

        private MembersFragment parent;

        public MembersViewHolder(View itemView, MembersFragment parent) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textMemName);
            color = (TextView) itemView.findViewById(R.id.textMemColor);
            margine = (TextView) itemView.findViewById(R.id.textMargin);
            tasks = (TextView) itemView.findViewById(R.id.textMemTasks);
            selector = (ImageButton) itemView.findViewById(R.id.imageMemSymbol);
            this.parent = parent;

            //selector.setOnClickListener(this);
            //itemView.setOnClickListener(this);
        }
/*
        @Override
        public void onClick(View v) {

            if (parent != null) {
                parent.onMemberSelect(getAdapterPosition());
            }

        }*/
    }

    public MembersAdapter(App application, MembersFragment parent) {
        super();
        //this.context = context;
        this.app = application;
        this.members = new ArrayList<>();
        this.parent = parent;
    }

    @Override
    public int getItemCount() {
        return visibleMembers.size();
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
        final Member objectItem;

        objectItem = visibleMembers.get(i);

        memberViewHolder.title.setText("   " + objectItem.getName());
        memberViewHolder.tasks.setText(String.valueOf(objectItem.getTasksCnt()));
        memberViewHolder.color.setBackgroundColor(objectItem.getColor());

        memberViewHolder.margine.setWidth(36 * objectItem.getLevel());

        if (app.getCurMember() == objectItem.getId()) {
            memberViewHolder.title.setBackgroundColor(objectItem.getColor());
            memberViewHolder.tasks.setBackgroundColor(objectItem.getColor());
            memberViewHolder.selector.setBackgroundColor(objectItem.getColor());
            //memberViewHolder.setIsRecyclable(false);
        } else {
            memberViewHolder.title.setBackgroundColor(0);
            memberViewHolder.tasks.setBackgroundColor(0);
            memberViewHolder.selector.setBackgroundColor(0);
        }


        switch (objectItem.getNode()) {
            case 1:
                memberViewHolder.selector.setImageResource(R.drawable.ic_chevron_right_24dp);
                break;
            case 2:
                memberViewHolder.selector.setImageResource(R.drawable.ic_expand_more_24dp);
                break;
            default:
                memberViewHolder.selector.setImageResource(R.drawable.ic_check_box_outline_blank_24dp);
        }

        memberViewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMember(objectItem);
            }
        });

        memberViewHolder.selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSelector(objectItem);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void onClickMember(Member selection){
        if (parent != null) {
            parent.onMemberSelect(selection);
        }
    }

    public void onClickSelector(Member selection){
        //???
        int position = visibleMembers.indexOf(selection);
        switch (selection.getNode()) {
            case 1:
                expandBranch(position);
                break;
            case 2:
                closeBranch(position);
                break;
        }
    }

    public void load(){
        members = DaoMembers.load(app);
        rebuildFilteredList();
        notifyDataSetChanged();
    }

    public long getMemberId(int position){
        return visibleMembers.get(position).getId();
    }

    public Member getMemberById(long id){
        return DaoMembers.getMemberById(app, id);
    }

    private void rebuildFilteredList(){
        visibleMembers = new ArrayList<>();
        for (Member el: members){
            if (el.isVisible()) visibleMembers.add(el);
        }
    }

    public void expandBranch(int position){
        int curLevel = visibleMembers.get(position).getLevel();
        int curMem = members.indexOf(visibleMembers.get(position));
        int newItems = 0;
        for (int i = curMem+1; i < members.size(); i++) {
            Member el = members.get(i);
            if (el.getLevel() <= curLevel) break;
            if (el.getLevel() == curLevel+1) {
                el.setVisible(true);
                newItems++;
            }
        }
        members.get(curMem).setNode(2);
        rebuildFilteredList();
        notifyItemChanged(position);
        notifyItemRangeInserted(position+1,newItems);
    }
    public void closeBranch(int position){
        int curLevel = visibleMembers.get(position).getLevel();
        int curMem = members.indexOf(visibleMembers.get(position));
        int delItems = 0;
        for (int i = curMem+1; i < members.size(); i++) {
            Member el = members.get(i);
            if (el.getLevel() <= curLevel) break;
            if (el.isVisible()){
                el.setVisible(false);
                delItems++;
            }
            if (el.getNode() == 2)el.setNode(1);
            //if (el.getId() == app.getCurMember()) app.setCurMember(0);
        }
        members.get(curMem).setNode(1);
        rebuildFilteredList();
        notifyItemChanged(position);
        notifyItemRangeRemoved(position+1, delItems);
    }
}
