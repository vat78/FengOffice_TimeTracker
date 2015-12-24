package ru.vat78.fotimetracker.model;

/**
 * Created by vat on 27.11.2015.
 */
public class FOTT_Member extends FOTT_Object {

    private int color;
    private int level;
    private boolean visible;
    private int node;
    private String path;
    private int tasksCnt;

    public FOTT_Member(long memberId, String memberName){
        super();
        setId(memberId);
        setName(memberName);
        visible = false;
        setPath("");
        tasksCnt = 0;
    }


    public int getColor() {
        return color;
    }

    public int getLevel() {
        return level;
    }

    public boolean isVisible() {
        return (visible || level == 1);
    }

    public void setColor(int color) {
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
