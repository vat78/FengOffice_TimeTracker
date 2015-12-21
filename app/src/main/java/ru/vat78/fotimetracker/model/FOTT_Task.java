package ru.vat78.fotimetracker.model;

import java.util.Date;

/**
 * Created by vat on 27.11.2015.
 */
public class FOTT_Task extends FOTT_Object {

    private Date startdate;
    private Date duedate;
    private int priority;

    public FOTT_Task(long taskId, String taskTitle){
        super();
        setId(taskId);
        setName(taskTitle);
    }

    public void setStartDate(long startdate) {
        this.startdate = new Date (startdate);
    }

    public void setDueDate(long duedate) {
        this.duedate = new Date (duedate);
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getDueDate(){
        return duedate;
    }
}
