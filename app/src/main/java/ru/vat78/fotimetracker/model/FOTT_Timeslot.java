package ru.vat78.fotimetracker.model;

import java.util.Date;

/**
 * Created by vat on 27.11.2015.
 */
public class FOTT_Timeslot extends FOTT_Object {

    private Date start;
    private long duration;


    public FOTT_Timeslot(long tsId, String tsTitle){
        setId(tsId);
        setName(tsTitle);
    }

    public Date getStart() {
        return start;
    }

    public long getDuration() {
        return duration;
    }

    public String getDurationString() {
        String res = "";
        Date dur = new Date(duration * 1000);
        if (duration >= 24 * 3600) res += "" + Math.round(duration / 24 / 3600) + " days";
        if (dur.getHours() > 0) res += " " + dur.getHours() + " hours";
        if (dur.getMinutes() > 0) res += " " + dur.getMinutes() + " minutes";

        return res;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setStart(long start) {
        this.start = new Date(start * 1000);;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
