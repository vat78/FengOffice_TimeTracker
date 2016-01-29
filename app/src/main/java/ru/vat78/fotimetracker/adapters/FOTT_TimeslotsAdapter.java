package ru.vat78.fotimetracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.database.FOTT_DBTimeslots;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;
import ru.vat78.fotimetracker.views.FOTT_TimeslotsFragment;

/**
 * Created by vat on 04.12.2015.
 */
public class FOTT_TimeslotsAdapter extends RecyclerView.Adapter<FOTT_TimeslotsAdapter.ViewHolder> {

    private ArrayList<FOTT_Timeslot> timeslots;
    private FOTT_App app;
    private FOTT_TimeslotsFragment parent;

    public ArrayList<FOTT_Timeslot> getAllTimeslots() {
        return timeslots;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tsText;
        public TextView tsAuthor;
        public TextView tsStart;
        public TextView tsDuration;

        public ViewHolder(View itemView) {
            super(itemView);
            tsText = (TextView)itemView.findViewById(R.id.tsText);
            tsAuthor = (TextView)itemView.findViewById(R.id.tsAuthor);
            tsStart = (TextView)itemView.findViewById(R.id.tsStart);
            tsDuration = (TextView)itemView.findViewById(R.id.tsDuration);
        }
    }

    public FOTT_TimeslotsAdapter(FOTT_App application, FOTT_TimeslotsFragment parent) {
        super();
        this.app = application;
        this.timeslots = new ArrayList<>();
        this.parent = parent;
    }

    @Override
    public int getItemCount(){
        return timeslots.size();
    }

    public FOTT_Timeslot getItem(int index){
        return  timeslots.get(index);
    }

    @Override
    public FOTT_TimeslotsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeslot_list_item, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final FOTT_Timeslot objectItem = timeslots.get(position);

        holder.tsText.setText(objectItem.getName());
        holder.tsAuthor.setText(objectItem.getAuthor());
        String s = app.getDateFormat().format(objectItem.getStart()) + " " + app.getTimeFormat().format(objectItem.getStart());
        holder.tsStart.setText(s);
        holder.tsDuration.setText(objectItem.getDurationString());

        holder.tsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTS(objectItem);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void load() {

        this.timeslots = FOTT_DBTimeslots.load(app,"");

    }

    public boolean saveTimeslot(long id, Date start, long duration, String text){
        if (start == null || duration == 0) return false;

        FOTT_Timeslot ts = new FOTT_Timeslot(id,text);
        ts.setStart(start);
        ts.setDuration(duration);
        ts.setTaskId(app.getCurTask());
        if (ts.getTaskId() == 0) {
            ts.setMembersIDs("" + app.getCurMember());
        }
        ts.setChanged(System.currentTimeMillis());
        FOTT_DBTimeslots.save(app,ts);
        return (ts.getId() != 0);
    }

    public void onClickTS(FOTT_Timeslot selection){
        if (parent != null) {
            parent.showTSDetails(selection);
        }
    }
}
