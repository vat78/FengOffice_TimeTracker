package ru.vat78.fotimetracker.adapters;

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
import java.util.List;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.database.FOTT_DBContract;
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
        holder.tsAuthor.setText("");
        holder.tsStart.setText("");
        holder.tsDuration.setText("");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void load() {
        SQLiteDatabase db = app.getDatabase();

        this.timeslots = new ArrayList<>();

        String filter = "";
        if (app.getCurTask() > 0){
            filter = " " + FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_TASK_ID +
                    " = " + String.valueOf(app.getCurTask());
        } else {
            filter = " " + FOTT_DBContract.FOTT_DBTimeslots.COLUMN_TIMESLOT_ID + " IN ( SELECT " +
                    FOTT_DBContract.FOTT_DBObject_Members.COLUMN_OBJECT_ID + " FROM " +
                    FOTT_DBContract.FOTT_DBObject_Members.TABLE_NAME + " WHERE " +
                    FOTT_DBContract.FOTT_DBObject_Members.COLUMN_MEMBER_ID + " = " +
                    String.valueOf(app.getCurMember()) + " AND " +
                    FOTT_DBContract.FOTT_DBObject_Members.COLUMN_OBJECT_TYPE + " = 2)";
        }

        Cursor tsCursor = db.query(FOTT_DBContract.FOTT_DBTimeslots.TABLE_NAME,
                new String[]{FOTT_DBContract.FOTT_DBTimeslots.COLUMN_TIMESLOT_ID,
                        FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_TITLE,
                        FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_START,
                        FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_DURATION,},
                filter, null, null, null,
                FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_START, null);

        tsCursor.moveToFirst();
        FOTT_Timeslot el;
        if (!tsCursor.isAfterLast()){
            do {
                long id = tsCursor.getLong(0);
                String name = tsCursor.getString(1);
                long start = tsCursor.getLong(2);
                int dur = tsCursor.getInt(3);

                el = new FOTT_Timeslot(id, name);
                el.setStart(start);
                el.setDuration(dur);

                timeslots.add(el);
            } while (tsCursor.moveToNext());
        }
    }
}
