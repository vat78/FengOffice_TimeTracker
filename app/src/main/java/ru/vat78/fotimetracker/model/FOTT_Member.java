package ru.vat78.fotimetracker.model;

import android.content.ContentValues;
import android.view.View;

/**
 * Created by vat on 27.11.2015.
 */
public class FOTT_Member extends FOTT_Object {

    private int color;
    private int level = 0;
    private boolean visible = true;
    private int state = 0;
    private int tasksCnt = 0;

    public FOTT_Member(long memberId, String memberName){
        setId(memberId);
        setName(memberName);
    }


    public int getColor() {
        return color;
    }

    public int getLevel() {
        return level;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getState() {
        return state;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isNode(){
        return state == 1;
    }

    public int getTasksCnt() {
        return tasksCnt;
    }

    public void setTasksCnt(int tasksCnt) {
        this.tasksCnt = tasksCnt;
    }
}
