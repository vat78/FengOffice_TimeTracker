package ru.vat78.fotimetracker.model;

import java.util.Date;

public class FOTT_TimeslotBuilder extends FOTT_ObjectBuilder  {
    protected Date start;
    protected long duration;
    protected long taskWebId;

    public FOTT_TimeslotBuilder() {
        super();
        this.start = new Date(0);
    }

    public FOTT_TimeslotBuilder(FOTT_Timeslot template) {
        super(template);
        this.start = template.getStart();
        this.duration = template.getDuration();
        this.taskWebId = template.getTaskId();
    }

    public FOTT_TimeslotBuilder setStart(Date start) {
        this.start = start;
        return this;
    }

    public FOTT_TimeslotBuilder setStart(long start) {
        this.start = new Date(start);
        return this;
    }

    public FOTT_TimeslotBuilder setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public FOTT_TimeslotBuilder setTaskWebId(long taskWebId) {
        this.taskWebId = taskWebId;
        return this;
    }

    public FOTT_Timeslot buildObject(){
        return new FOTT_Timeslot(this);
    }
}
