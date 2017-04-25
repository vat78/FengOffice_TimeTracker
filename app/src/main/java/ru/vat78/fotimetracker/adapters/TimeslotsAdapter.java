package ru.vat78.fotimetracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.database.DaoTimeslots;
import ru.vat78.fotimetracker.model.Timeslot;
import ru.vat78.fotimetracker.views.TimeslotsFragment;

/**
 * Created by vat on 04.12.2015.
 */
public class TimeslotsAdapter extends RecyclerView.Adapter<TimeslotsAdapter.ViewHolder> {

    private ArrayList<Timeslot> timeslots;
    private App app;
    private TimeslotsFragment parent;

    public ArrayList<Timeslot> getAllTimeslots() {
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

    public TimeslotsAdapter(App application, TimeslotsFragment parent) {
        super();
        this.app = application;
        this.timeslots = new ArrayList<>();
        this.parent = parent;
    }

    @Override
    public int getItemCount(){
        return timeslots.size();
    }

    public Timeslot getItem(int index){
        return  timeslots.get(index);
    }

    @Override
    public TimeslotsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeslot_list_item, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Timeslot objectItem = timeslots.get(position);

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

        this.timeslots = DaoTimeslots.load(app,"");

    }

    public boolean saveTimeslot(long id, Date start, long duration, String text){
        if (start == null || duration == 0) return false;

        Timeslot ts = new Timeslot(id,text);
        ts.setStart(start);
        ts.setDuration(duration);
        ts.setTaskId(app.getCurTask());
        ts.setMembersIDs("" + app.getCurMember());
        ts.setChanged(System.currentTimeMillis());
        DaoTimeslots.save(app,ts);
        return (ts.getUid() != 0);
    }

    public void onClickTS(Timeslot selection){
        if (parent != null) {
            parent.showTSDetails(selection);
        }
    }
}
