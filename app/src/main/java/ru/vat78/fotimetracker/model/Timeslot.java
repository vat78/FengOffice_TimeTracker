package ru.vat78.fotimetracker.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vat on 27.11.2015.
 */
public class Timeslot extends Object {

    private Date start;
    private long duration;
    private long task_id;

    public Timeslot(long tsUid, String tsTitle){
        super();
        setUid(tsUid);
        setName(tsTitle.length() > 250 ? tsTitle.substring(0, 250) : tsTitle);
        setDesc(tsTitle);
        setTaskId(0);
    }

    public Date getStart() {
        return start;
    }

    public long getDuration() {
        return duration;
    }

    public String getDurationString() {
        String res = "";
        SimpleDateFormat df = new SimpleDateFormat();

        long l;
        int i;

        i = Math.round(duration / 24 / 3600 / 1000);
        if (i>0) res += "" + i + " d";
        l = duration - i * 24 * 3600 * 1000;

        i = Math.round(l / 3600 / 1000);
        if (i > 0) res += " " + i + " h";
        l = l - i * 3600 * 1000;

        i = Math.round(l / 60 / 1000);
        if (i > 9) {res += " " + i + " m";}
        else {res += " 0" + i + " m";}

        return res;
    }

    public long getTaskId() {
        return task_id;
    }


    public void setStart(long start) {
        this.start = new Date(start);
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setTaskId(long task_id) {
        this.task_id = task_id;
    }

}
