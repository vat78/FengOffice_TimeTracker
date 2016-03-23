package ru.vat78.fotimetracker.adapters;


import ru.vat78.fotimetracker.model.FOTT_Member;
import ru.vat78.fotimetracker.model.FOTT_MemberBuilder;

public class FOTT_DrawingMember extends FOTT_Member {

    private boolean visible;
    private int node;
    private int tasksCnt;

    public FOTT_DrawingMember(FOTT_MemberBuilder builder){
        super(builder);
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
