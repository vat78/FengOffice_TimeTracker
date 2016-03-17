package ru.vat78.fotimetracker.model;

import java.util.Date;

public class FOTT_TimeslotBuilder extends FOTT_ObjectBuilder  {
    protected Date start;
    protected long duration;
    protected long taskWebId;

    public void setStart(Date start) {
        this.start = start;
    }

    public void setStart(long start) {
        this.start = new Date(start);
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setTaskWebId(long taskWebId) {
        this.taskWebId = taskWebId;
    }

    public FOTT_Timeslot buildObject(){
        return new FOTT_Timeslot(this);
    }
}
