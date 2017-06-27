package ru.vat78.fotimetracker.model;

/**
 * Created by vat on 27.11.2015.
 */
public class Member extends DbObject {

    private int color;
    private int level;
    private boolean visible;
    private int node;
    private String path;
    private int tasksCnt;

    private int memColors[] = {0xFF888888, 0xFF5A6986, 0xFF206CE1, 0xFF0000FF, 0xFF5229a3, 0xFF854f61, 0xFFFF0000,
            0xFFec7000, 0xFFb36d00, 0xFFab8b00, 0xFF636330, 0xFF64992c, 0xFF00FF00, 0xFFb1b8c8, 0xFFcadcf9, 0xFFc5c6f5,
            0xFFede7fb, 0xfffdf2f8, 0xfff5c6c6, 0xfffff6ed, 0xffefd7b3, 0xffebe0b3, 0xffe3e3c8, 0xffe0ecd2, 0xffcbdfd2};

    public Member(long memberUid, String memberName){
        super();
        setUid(memberUid);
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
        this.path = path == null ? "" : path;
        String[] s = this.path.split(MEMBER_SPLITTER);
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
