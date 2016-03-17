package ru.vat78.fotimetracker.adapters;


import ru.vat78.fotimetracker.model.FOTT_Member;

public class FOTT_DrawingMember {
    private final FOTT_Member member;

    private boolean visible;
    private int node;
    private int tasksCnt;

    public FOTT_DrawingMember(FOTT_Member member){
        this.member = member;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public void setTasksCnt(int tasksCnt) {
        this.tasksCnt = tasksCnt;
    }

    public FOTT_Member getMember() {
        return member;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getNode() {
        return node;
    }

    public int getTasksCnt() {
        return tasksCnt;
    }


}
