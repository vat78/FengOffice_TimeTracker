package ru.vat78.fotimetracker.model;

import java.util.Date;

/**
 * Created by vat on 27.11.2015.
 */
public class FOTT_Timeslot extends FOTT_Object {

    private Date start;
    private int duration;


    public FOTT_Timeslot(long tsId, String tsTitle){
        setId(tsId);
        setName(tsTitle);
    }

    public Date getStart() {
        return start;
    }

    public int getDuration() {
        return duration;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setStart(long start) {
        this.start = new Date(start * 1000);;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
