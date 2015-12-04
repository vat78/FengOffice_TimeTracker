package ru.vat78.fotimetracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import ru.vat78.fotimetracker.model.FOTT_Member;

/**
 * Created by vat on 30.11.2015.
 */
public class FOTT_MembersAdapter extends ArrayAdapter<String> {

    private List<FOTT_Member> members;
    private Context context;
    private FOTT_App app;

    private int memColors[] = {Color.GRAY,Color.DKGRAY,Color.RED,Color.BLUE,Color.GREEN,Color.MAGENTA,Color.CYAN,Color.YELLOW,
            Color.GRAY,Color.DKGRAY,Color.RED,Color.BLUE,Color.GREEN,Color.MAGENTA,Color.CYAN,Color.YELLOW,
            Color.GRAY,Color.DKGRAY,Color.RED,Color.BLUE,Color.GREEN,Color.MAGENTA,Color.CYAN,Color.YELLOW,Color.WHITE};

    public FOTT_MembersAdapter(Context context, FOTT_App application) {
        super(context,R.layout.member_list_item);
        this.context = context;
        this.app = application;
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public String getItem(int position) {
        return members.get(position).getName();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.member_list_item, parent, false);

        TextView title = (TextView) view.findViewById(R.id.textMemName);
        TextView color = (TextView) view.findViewById(R.id.textMemColor);
        TextView margine = (TextView) view.findViewById(R.id.textMargin);
        TextView tasks = (TextView) view.findViewById(R.id.textMemTasks);

        FOTT_Member objectItem = members.get(position);

        title.setText("   " + objectItem.getName());
        tasks.setText(String.valueOf(objectItem.getTasksCnt()));
        color.setBackgroundColor(memColors[objectItem.getColor()]);

        margine.setWidth(36 * objectItem.getLevel());

        String tasksCnt = "" + objectItem.getTasksCnt();
        tasks.setText(tasksCnt);

        if (app.getCurMember() == objectItem.getId()) {
            title.setBackgroundColor(memColors[objectItem.getColor()]);
            tasks.setBackgroundColor(memColors[objectItem.getColor()]);
        }

        return view;
    }

    public void loadMembers(){
        SQLiteDatabase db = app.getDatabase();

        this.members = new ArrayList<>();
        Cursor memberCursor = db.query(FOTT_DBContract.FOTT_DBMembers.TABLE_NAME,
                new String[]{FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_MEMBER_ID,
                        FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_NAME,
                        FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_PATH,
                        FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_LEVEL,
                        FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_COLOR,
                        FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_TASKS},
                null, null, null, null,
                FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_PATH,null);

        memberCursor.moveToFirst();
        FOTT_Member m;
        if (!memberCursor.isAfterLast()){
            do {
                long id = memberCursor.getLong(0);
                String name = memberCursor.getString(1);
                String path = memberCursor.getString(2);
                int color = memberCursor.getInt(4);
                int level = memberCursor.getInt(3);

                m = new FOTT_Member(id, name);
                m.setPath(path);
                m.setColor(color);
                m.setLevel(level);
                m.setTasksCnt(memberCursor.getInt(5));

                members.add(m);
            } while (memberCursor.moveToNext());
        }
    }

    public long getMemberId(int position){
        return members.get(position).getId();
    }
}
