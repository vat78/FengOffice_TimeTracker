package ru.vat78.fotimetracker.adapters;

import android.database.sqlite.SQLiteDatabase;
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


public class FOTT_MembersAdapter extends RecyclerView.Adapter <FOTT_MembersAdapter.MembersViewHolder> {

    final private FOTT_App app;
    final private FOTT_MembersFragment parent;

    private List<FOTT_DrawingMember> members;
    private List<FOTT_DrawingMember> visibleMembers;

    public static class MembersViewHolder extends RecyclerView.ViewHolder {

        final private TextView title ;
        final private TextView color;
        final private TextView margine;
        final private TextView tasks;
        final private ImageButton selector;

        public MembersViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textMemName);
            color = (TextView) itemView.findViewById(R.id.textMemColor);
            margine = (TextView) itemView.findViewById(R.id.textMargin);
            tasks = (TextView) itemView.findViewById(R.id.textMemTasks);
            selector = (ImageButton) itemView.findViewById(R.id.imageMemSymbol);
        }
    }

    public FOTT_MembersAdapter(FOTT_MembersFragment parent) {

        super();
        this.members = new ArrayList<>();
        this.parent = parent;
        this.app = (FOTT_App) parent.getActivity().getApplication();
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

        final FOTT_DrawingMember objectItem = visibleMembers.get(i);

        memberViewHolder.title.setText(objectItem.getName());
        memberViewHolder.tasks.setText(String.valueOf(objectItem.getTasksCnt()));
        memberViewHolder.color.setBackgroundColor(objectItem.getColor());

        memberViewHolder.margine.setWidth(36 * objectItem.getMembersWebIds().length);

        if (app.getCurMember() == objectItem.getWebId()) {
            memberViewHolder.title.setBackgroundColor(objectItem.getColor());
            memberViewHolder.tasks.setBackgroundColor(objectItem.getColor());
            memberViewHolder.selector.setBackgroundColor(objectItem.getColor());
        } else {
            memberViewHolder.title.setBackgroundColor(0);
            memberViewHolder.tasks.setBackgroundColor(0);
            memberViewHolder.selector.setBackgroundColor(0);
        }


        switch (objectItem.getNode()) {
            case FOTT_DrawingMember.NODE_CLOSE:
                memberViewHolder.selector.setImageResource(R.drawable.ic_chevron_right_24dp);
                break;
            case FOTT_DrawingMember.NODE_OPEN:
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

        int position = visibleMembers.indexOf(selection);
        switch (selection.getNode()) {
            case FOTT_DrawingMember.NODE_CLOSE:
                expandBranch(position);
                break;
            case FOTT_DrawingMember.NODE_OPEN:
                closeBranch(position);
                break;
        }
    }

    public void load(){
        try {
            members = (ArrayList<FOTT_DrawingMember>) new FOTT_DBMembers(app.getReadOnlyDB()).loadObjects();
        } catch (FOTT_Exceptions e) {
            members = new ArrayList<>();
        }
        app.closeDb();
        initialSetVisibility();
        notifyDataSetChanged();
    }

    public FOTT_DrawingMember getCurrentMember(){

        FOTT_DrawingMember result = null;
        for (FOTT_DrawingMember m : members)
            if (m.getWebId() == app.getCurMember()) {
                result = m;
                break;
            }

        return result;
    }

    public FOTT_Member getMemberById(long id){

        FOTT_DrawingMember result;
        try {
            result = (FOTT_DrawingMember) new FOTT_DBMembers(app.getReadOnlyDB()).loadObject(id);
        } catch (FOTT_Exceptions e) {
            result = null;
        }
        app.closeDb();
        return result;
    }

    private void rebuildVisibleList(){
        visibleMembers = new ArrayList<>();
        for (FOTT_DrawingMember el: members){
            if (el.isVisible()) visibleMembers.add(el);
        }
    }

    private void expandBranch(int position){
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
        members.get(curMem).setNode(FOTT_DrawingMember.NODE_OPEN);
        rebuildVisibleList();
        notifyItemChanged(position);
        notifyItemRangeInserted(position + 1, newItems);
    }

    private void closeBranch(int position){
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
            if (el.getNode() == FOTT_DrawingMember.NODE_OPEN) el.setNode(FOTT_DrawingMember.NODE_CLOSE);
        }
        members.get(curMem).setNode(FOTT_DrawingMember.NODE_CLOSE);
        rebuildVisibleList();
        notifyItemChanged(position);
        notifyItemRangeRemoved(position + 1, delItems);
    }

    private void initialSetVisibility() {

        int visibleLevel = 1;
        long currentMember = app.getCurMember();

        for (int i = 0; i<members.size() - 1; i++) {
            FOTT_DrawingMember el = members.get(i);

            visibleLevel = setVisibilityByLevel(el,visibleLevel);
            setNode(el,members.get(i+1));

            if (el.getWebId() == currentMember) {
                visibleLevel = el.getLevel();
                el.setVisible(true);
                for (int j=i-1; j>=0 && visibleLevel>1; j--) {
                    visibleLevel = setVisibilityByLevel(members.get(j),visibleLevel);
                    setNode(members.get(j), members.get(j+1));
                }
                visibleLevel = el.getLevel();
            }
        }

        rebuildVisibleList();
    }

    private int setVisibilityByLevel(FOTT_DrawingMember member, int level) {

        int newLevel = level;
        if (member.getLevel() <= level) {
            member.setVisible(true);
            if (member.getNode() == FOTT_DrawingMember.NODE_CLOSE)
                member.setNode(FOTT_DrawingMember.NODE_OPEN);
            newLevel = member.getLevel();
        }
        return newLevel;
    }

    private void setNode(FOTT_DrawingMember member, FOTT_DrawingMember nextMember){

        if (nextMember.getLevel() > member.getLevel()) {
            if (nextMember.isVisible()) {
                member.setNode(FOTT_DrawingMember.NODE_OPEN);
            } else {
                member.setNode(FOTT_DrawingMember.NODE_CLOSE);
            }
        } else {
            member.setNode(FOTT_DrawingMember.NODE_NO);
        }
    }
}
