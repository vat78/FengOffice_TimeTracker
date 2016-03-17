package ru.vat78.fotimetracker.model;

import java.util.Date;

public class FOTT_TaskBuilder extends FOTT_ObjectBuilder {
    protected Date startDate;
    protected Date dueDate;
    protected int priority;
    protected FOTT_Task.TaskStatus status;
    protected boolean canAddTimeslots;

    public FOTT_TaskBuilder() {
        super();
        status = FOTT_Task.TaskStatus.ACTIVE;
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

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(long startdate) {
        this.startDate = new Date (startdate);
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setDueDate(long duedate) {
        this.dueDate = new Date (duedate);
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setStatus(FOTT_Task.TaskStatus status) {
        this.status = status;
    }

    public void setCanAddTimeslots(boolean canAddTimeslots) {
        this.canAddTimeslots = canAddTimeslots;
    }

    public FOTT_Task buildObject(){
        return new FOTT_Task(this);
    }
}
