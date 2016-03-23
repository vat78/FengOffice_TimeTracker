package ru.vat78.fotimetracker.model;

import java.util.Date;

/**
 * Task object
 * Created by vat on 27.11.2015.
 */
public class FOTT_Task extends FOTT_Object {

    public static final int STATUS_ACTIVE = 0;
    public static final int STATUS_COMPLETED = 1;


    final private Date startDate;
    final private Date dueDate;
    final private int priority;
    final private int status;
    final private boolean canAddTimeslots;

    public FOTT_Task(FOTT_TaskBuilder builder){
        super(builder);
        this.startDate = builder.startDate;
        this.dueDate = builder.dueDate;
        this.priority = builder.priority;
        this.status = builder.status;
        this.canAddTimeslots = builder.canAddTimeslots;
    }


    public Date getDueDate(){
        return dueDate;
    }

    public int getStatus() {return status;}

    public Date getStartDate() {
        return startDate;
    }

    public int getPriority() {
        return priority;
    }

    public boolean canAddTimeslots() {return canAddTimeslots;}
}
