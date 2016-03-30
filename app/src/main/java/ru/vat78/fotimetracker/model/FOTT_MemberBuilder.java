package ru.vat78.fotimetracker.model;


public class FOTT_MemberBuilder extends FOTT_ObjectBuilder {
    protected int color;
    protected String path;
    protected int level;

    public FOTT_MemberBuilder() {
        super();

        color = 0;
        path = "";
    }

    public FOTT_MemberBuilder(FOTT_Member template){
        super(template);
        color = template.getColor();
        path = template.getPath();
    }

    public FOTT_MemberBuilder setColor(int color) {
        this.color = color;
        return this;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public FOTT_Member buildObject() {
        return new FOTT_Member(this);
    }
}
