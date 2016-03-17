package ru.vat78.fotimetracker.model;

import android.graphics.Color;

import java.util.Date;

/**
 * Category object
 * Created by vat on 27.11.2015.
 */
public class FOTT_Member extends FOTT_Object {

    final private int color;
    final private String path;

    //private boolean visible;
    //private int node;
    //private int tasksCnt;

    public FOTT_Member(FOTT_MemberBuilder builder) {
        super(builder);
        this.color = builder.color;
        this.path = builder.path;
    }

    /*
    public FOTT_Member(long webID, String name, String desc, String author, String[] membersWebIds, Date changed, boolean deleted, int color){
        super(webID, name, desc, author, membersWebIds, changed, deleted);
        this.color = color;

        visible = false;
        tasksCnt = 0;
    }
    */

    public int getColor() {return color;}

    public int getLevel() {
        return this.getMembersWebIds().length;
    }

    public String getPath() {
        return path;
    }

    /*

    public boolean isVisible() {
        return (visible || this.getMembersArray().length == 1);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getTasksCnt() {
        return tasksCnt;
    }

    public void setTasksCnt(int tasksCnt) {
        this.tasksCnt = tasksCnt;
    }

    public int getNode() {
        return node;
    }

    */
}
