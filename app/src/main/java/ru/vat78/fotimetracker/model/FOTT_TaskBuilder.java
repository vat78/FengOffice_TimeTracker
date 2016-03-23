package ru.vat78.fotimetracker.model;

import java.util.Date;

public class FOTT_TaskBuilder extends FOTT_ObjectBuilder {
    protected Date startDate;
    protected Date dueDate;
    protected int priority;
    protected int status;
    protected boolean canAddTimeslots;

    public FOTT_TaskBuilder() {
        super();
        status = FOTT_Task.STATUS_ACTIVE;
        canAddTimeslots = true;
    }

    public FOTT_TaskBuilder(FOTT_Task template) {
        super(template);
        this.startDate = template.getStartDate();
        this.dueDate = template.getDueDate();
        this.priority = template.getPriority();
        this.status = template.getStatus();
        this.canAddTimeslots = template.canAddTimeslots();
    }

    public FOTT_TaskBuilder setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public FOTT_TaskBuilder setStartDate(long startdate) {
        this.startDate = new Date (startdate);
        return this;
    }

    public FOTT_TaskBuilder setDueDate(Date dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public FOTT_TaskBuilder setDueDate(long duedate) {
        this.dueDate = new Date (duedate);
        return this;
    }

    public FOTT_TaskBuilder setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public FOTT_TaskBuilder setStatus(int status) {
        this.status = status;
        return this;
    }

    public FOTT_TaskBuilder setCanAddTimeslots(boolean canAddTimeslots) {
        this.canAddTimeslots = canAddTimeslots;
        return this;
    }

    public FOTT_Task buildObject(){
        return new FOTT_Task(this);
    }
}
