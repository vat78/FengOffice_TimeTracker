package ru.vat78.fotimetracker.model;

import java.util.Date;

/**
 * Created by vat on 27.11.2015.
 */
public class FOTT_Task extends FOTT_Object {

    private long duedate;

    public FOTT_Task(long taskId, String taskTitle){
        setId(taskId);
        setName(taskTitle);
    }

    public void setDuedate(long duedate) {
        this.duedate = duedate;
    }

    public Date getDueDate(){
        return new Date(duedate);
    }
}
