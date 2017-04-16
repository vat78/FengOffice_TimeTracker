package ru.vat78.fotimetracker.model;

import java.util.Date;

/**
 * Created by vat on 27.11.2015.
 */
public class Task extends Object {

    private Date startdate;
    private Date duedate;
    private int priority;
    private int status;

    public Task(long taskId, String taskTitle){
        super();
        setId(taskId);
        setName(taskTitle);
        status = 0;
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

    public void setDuedate(long duedate) {
        this.duedate = new Date(duedate);
    }

    public void setStatus(int status) { this.status = status;}

    public int getStatus() {return status;}
}
