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
                        FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_DURATION,
                        FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_CHANGED,
                        FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_CHANGED_BY,
                        FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_TASK_ID,
                        FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_DESC},
                filter, null, null, null,
                FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_START + " DESC", null);

        tsCursor.moveToFirst();
        FOTT_Timeslot el;
        if (!tsCursor.isAfterLast()){
            do {
                long id = tsCursor.getLong(0);
                String name = tsCursor.getString(1);
                long start = tsCursor.getLong(2);
                int dur = tsCursor.getInt(3);
                long changed = tsCursor.getLong(4);
                String author = tsCursor.getString(5);
                long tid = tsCursor.getLong(6);

                el = new FOTT_Timeslot(id, name);
                el.setStart(start);
                el.setDuration(dur);
                el.setChanged(changed);
                el.setAuthor(author);
                el.setTaskId(tid);
                el.setDesc(tsCursor.getString(7));

                timeslots.add(el);
            } while (tsCursor.moveToNext());
        }
    }

    public boolean saveTimeslot(long id, Date start, long duration, String text){
        if (start == null || duration == 0) return false;

        ContentValues ts = new ContentValues();
        ts.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_START,start.getTime());
        ts.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_DURATION,duration);
        ts.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_DESC,text);
        if (text.length()>250) text = text.substring(0,249);
        ts.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_TITLE,text);
        long now = System.currentTimeMillis();
        ts.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_CHANGED,now);
        if (app.getCurTask() != 0) {
            ts.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_TASK_ID,app.getCurTask());
        }

        if (id ==0) {
            return saveNewTimeslot(ts);
        } else {
            return saveChangedTimeslot(id, ts);
        }
    }

    private boolean saveNewTimeslot(ContentValues ts_values){

        SQLiteDatabase db = app.getDatabase();
        long id = db.insert(FOTT_DBContract.FOTT_DBTimeslots.TABLE_NAME,null,ts_values);

        if (app.getCurTask() == 0){
            //TODO Add link to members path
        }
        return (id != 0);
    }

    private boolean saveChangedTimeslot(long id, ContentValues ts_values){
        if (id == 0) return false;

        String filter = FOTT_DBContract.FOTT_DBTimeslots.COLUMN_TIMESLOT_ID + " = " + String.valueOf(id);

        SQLiteDatabase db = app.getDatabase();
        int res = db.update(FOTT_DBContract.FOTT_DBTimeslots.TABLE_NAME, ts_values,filter,null);

        if (app.getCurTask() == 0){
            //TODO Add link to members path
        }
        return (res != 0);
    }
}
