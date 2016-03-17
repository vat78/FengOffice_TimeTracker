package ru.vat78.fotimetracker.model;


import java.util.Date;

abstract public class FOTT_ObjectBuilder {

    protected long webID;
    protected String name;
    protected String desc;
    protected String author;
    protected String[] membersWebIds;
    protected Date changed;
    protected boolean deleted;

    public FOTT_ObjectBuilder(){
        webID = 0;
        name = "";
        desc = "";
        author = "";
        changed = new Date(0);
        deleted = false;
    }

    public FOTT_ObjectBuilder(FOTT_Object template){
        webID = template.getWebId();
        name = template.getName();
        desc = template.getDesc();
        author = template.getAuthor();
        membersWebIds = template.getMembersWebIds();
        changed = template.getChanged();
        deleted = template.isDeleted();
    }

    public FOTT_ObjectBuilder setWebID(long webID) {
        this.webID = webID;
        return this;
    }

    public FOTT_ObjectBuilder setName(String name) {
        this.name = (name.length() > 250 ? name.substring(0, 250) : name);
        return this;
    }

    public FOTT_ObjectBuilder setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public FOTT_ObjectBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public FOTT_ObjectBuilder setMembersWebIds(String[] membersWebIds) {
        this.membersWebIds = membersWebIds;
        return this;
    }

    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public void setChanged(long changed) {
        this.changed = new Date(changed);
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public FOTT_Object buildObject(){
        return new FOTT_Object(this);
    }
}
