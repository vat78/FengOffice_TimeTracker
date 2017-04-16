package ru.vat78.fotimetracker.model;

import android.graphics.Color;

/**
 * Created by vat on 27.11.2015.
 */
public class Member extends Object {

    private int color;
    private int level;
    private boolean visible;
    private int node;
    private String path;
    private int tasksCnt;

    private int memColors[] = {Color.GRAY,Color.argb(255,90,105,134),Color.argb(255,32,108,225),
            Color.BLUE,Color.argb(255,82,41,163),Color.argb(255,133,79,97),Color.RED,
            Color.argb(255,236,112,0),Color.argb(255,179,109,0),Color.argb(255,171,139,0),
            Color.argb(255,99,99,48),Color.argb(255,100,153,44),Color.GREEN,Color.argb(255,177,184,200),
            Color.argb(255,202,220,249),Color.argb(255,197,198,245),Color.argb(255,237,231,251),
            Color.argb(255,253,242,248),Color.argb(255,245,198,198),Color.argb(255,255,246,237),
            Color.argb(255,239,215,179),Color.argb(255,235,224,179),Color.argb(255,227,227,200),
            Color.argb(255,224,236,210),Color.argb(255,203,223,210)};

    public Member(long memberId, String memberName){
        super();
        setId(memberId);
        setName(memberName);
        visible = false;
        setPath("");
        tasksCnt = 0;
    }


    public int getColorIndex() {
        return color;
    }

    public int getColor() {return memColors[color];}

    public int getLevel() {
        return level;
    }

    public boolean isVisible() {
        return (visible || level == 1);
    }

    public void setColorIndex(int color) {
        this.color = color;
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

    public void setPath(String path) {
        this.path = path;
        String[] s = path.split(MEMBER_SPLITTER);
        level = s.length;
    }

    public String getPath() {
        return path;
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }
}
