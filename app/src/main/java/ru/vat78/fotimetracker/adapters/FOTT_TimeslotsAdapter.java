package ru.vat78.fotimetracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.connectors.database.FOTT_DBTimeslots;
import ru.vat78.fotimetracker.controllers.FOTT_Exceptions;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;
import ru.vat78.fotimetracker.model.FOTT_TimeslotBuilder;
import ru.vat78.fotimetracker.views.FOTT_TimeslotsFragment;

public class FOTT_TimeslotsAdapter extends RecyclerView.Adapter<FOTT_TimeslotsAdapter.ViewHolder> {

    final private FOTT_App app;
    final private FOTT_TimeslotsFragment parent;

    private ArrayList<FOTT_Timeslot> timeslots;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final public TextView tsText;
        final public TextView tsAuthor;
        final public TextView tsStart;
        final public TextView tsDuration;

        public ViewHolder(View itemView) {
            super(itemView);
            tsText = (TextView)itemView.findViewById(R.id.tsText);
            tsAuthor = (TextView)itemView.findViewById(R.id.tsAuthor);
            tsStart = (TextView)itemView.findViewById(R.id.tsStart);
            tsDuration = (TextView)itemView.findViewById(R.id.tsDuration);
        }
    }

    public FOTT_TimeslotsAdapter(FOTT_TimeslotsFragment parent) {

        super();
        this.timeslots = new ArrayList<>();
        this.parent = parent;
        this.app = (FOTT_App) parent.getActivity().getApplication();

    }

    @Override
    public int getItemCount(){
        return timeslots.size();
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
        holder.tsDuration.setText(objectItem.getDurationAsString());

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

        try {
            this.timeslots = (ArrayList<FOTT_Timeslot>) new FOTT_DBTimeslots(app.getReadOnlyDB()).loadObjects();
        } catch (FOTT_Exceptions e) {
            this.timeslots = new ArrayList<>();
        }
        app.closeDb();
        notifyDataSetChanged();
    }

    public boolean saveTimeslot(FOTT_TimeslotBuilder ts){

        if (ts == null) return false;

        ts.setTaskWebId(app.getCurTask());
        ts.setMembersWebIds(new String[] {"" + app.getCurMember()});
        ts.setChanged(System.currentTimeMillis());

        long result = new FOTT_DBTimeslots(app.getWritableDB()).saveObject(ts.buildObject());
        return (result != 0);
    }

    private void onClickTS(FOTT_Timeslot selection){
        if (parent != null) {
            parent.showTSDetails(selection);
        }
    }
}
