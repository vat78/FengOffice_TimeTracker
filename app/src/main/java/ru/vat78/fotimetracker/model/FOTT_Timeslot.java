package ru.vat78.fotimetracker.model;

import java.util.Date;

/**
 * Created by vat on 27.11.2015.
 */
public class FOTT_Timeslot extends FOTT_Object {

    final private Date start;
    final private long duration;
    final private long taskWebId;

    public FOTT_Timeslot(FOTT_TimeslotBuilder builder){
        super(builder);
        this.start = builder.start;
        this.duration = builder.duration;
        this.taskWebId = builder.taskWebId;
    }

    public Date getStart() {
        return start;
    }

    public long getDuration() {
        return duration;
    }

    public String getDurationAsString() {
        return convertMiilisecToString(duration);
    }

    public long getTaskId() {
        return taskWebId;
    }

    //ToDo: does it need here?
    private String convertMiilisecToString(long time) {

        String res = "";

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

}
