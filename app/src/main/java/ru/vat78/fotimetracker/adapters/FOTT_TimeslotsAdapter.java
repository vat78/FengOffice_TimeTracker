package ru.vat78.fotimetracker.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.database.FOTT_DBContract;
import ru.vat78.fotimetracker.database.FOTT_DBTimeslots;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;

/**
 * Created by vat on 04.12.2015.
 */
public class FOTT_TimeslotsAdapter extends RecyclerView.Adapter<FOTT_TimeslotsAdapter.ViewHolder> {

    private List<FOTT_Timeslot> timeslots;
    private Context context;
    private FOTT_App app;

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

    public FOTT_TimeslotsAdapter(Context context, FOTT_App application) {
        super();
        this.context = context;
        this.app = application;
        this.timeslots = new ArrayList<>();
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

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        FOTT_Timeslot objectItem = timeslots.get(position);

        holder.tsText.setText(objectItem.getName());
        holder.tsAuthor.setText(objectItem.getAuthor());
        holder.tsStart.setText(app.getDateFormat().format(objectItem.getStart()) + " " + app.getTimeFormat().format(objectItem.getStart()));
        holder.tsDuration.setText(objectItem.getDurationString());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void load() {

        this.timeslots = FOTT_DBTimeslots.load(app);

    }

    public boolean saveTimeslot(long id, Date start, long duration, String text){
        if (start == null || duration == 0) return false;

        FOTT_Timeslot ts = new FOTT_Timeslot(id,text);
        ts.setStart(start);
        ts.setDuration(duration);
        ts.setTaskId(app.getCurTask());
        if (ts.getTaskId() == 0) {
            ts.setMembersPath("" + app.getCurMember());
        }
        FOTT_DBTimeslots.save(app,ts);
        return (ts.getId() != 0);
    }

}
