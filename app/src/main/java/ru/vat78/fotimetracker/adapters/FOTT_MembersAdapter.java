package ru.vat78.fotimetracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.connectors.database.FOTT_DBMembers;
import ru.vat78.fotimetracker.controllers.FOTT_Exceptions;
import ru.vat78.fotimetracker.model.FOTT_Member;
import ru.vat78.fotimetracker.views.FOTT_MembersFragment;

/**
 * Created by vat on 30.11.2015.
 */
public class FOTT_MembersAdapter extends RecyclerView.Adapter <FOTT_MembersAdapter.MembersViewHolder> {

    private List<FOTT_DrawingMember> members;
    private List<FOTT_DrawingMember> visibleMembers;
    private FOTT_App app;
    private FOTT_MembersFragment parent;

    public static class MembersViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView title ;
        private TextView color;
        private TextView margine;
        private TextView tasks;
        private ImageButton selector;

        public MembersViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textMemName);
            color = (TextView) itemView.findViewById(R.id.textMemColor);
            margine = (TextView) itemView.findViewById(R.id.textMargin);
            tasks = (TextView) itemView.findViewById(R.id.textMemTasks);
            selector = (ImageButton) itemView.findViewById(R.id.imageMemSymbol);
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
        return visibleMembers.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public MembersViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.member_list_item, viewGroup, false);
        return new MembersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MembersViewHolder memberViewHolder, int i) {
        final FOTT_DrawingMember objectItem;

        objectItem = visibleMembers.get(i);

        String s = "   " + objectItem.getName();
        memberViewHolder.title.setText(s);
        //memberViewHolder.tasks.setText(String.valueOf(objectItem.getTasksCnt()));
        memberViewHolder.color.setBackgroundColor(objectItem.getColor());

        memberViewHolder.margine.setWidth(36 * objectItem.getMembersWebIds().length);

        if (app.getCurMember() == objectItem.getWebId()) {
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

    public void onClickMember(FOTT_DrawingMember selection){
        if (parent != null) {
            parent.onMemberSelect(selection);
        }
    }

    public void onClickSelector(FOTT_DrawingMember selection){
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
        try {
            members = (ArrayList<FOTT_DrawingMember>) new FOTT_DBMembers(app.getDatabase()).loadObjects();
        } catch (FOTT_Exceptions e) {
            members = new ArrayList<>();
        }
        initialBuildVisibleList();
        notifyDataSetChanged();
    }

    public long getMemberId(int position){
        return visibleMembers.get(position).getDbID();
    }

    public FOTT_Member getMemberById(long id){

        FOTT_DrawingMember result;
        try {
            result = (FOTT_DrawingMember) new FOTT_DBMembers(app.getDatabase()).loadObject(id);
        } catch (FOTT_Exceptions e) {
            result = null;
        }
        return result;
    }

    private void rebuildFilteredList(){
        visibleMembers = new ArrayList<>();
        for (FOTT_DrawingMember el: members){
            if (el.isVisible()) visibleMembers.add(el);
        }
    }

    public void expandBranch(int position){
        int curLevel = visibleMembers.get(position).getLevel();
        int curMem = members.indexOf(visibleMembers.get(position));
        int newItems = 0;
        for (int i = curMem+1; i < members.size(); i++) {
            FOTT_DrawingMember el = members.get(i);
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
            FOTT_DrawingMember el = members.get(i);
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

    private void initialBuildVisibleList() {
        //ToDo: make visible current member
        rebuildFilteredList();
    }
}
