package ru.vat78.fotimetracker.model;

import java.util.Date;

/**
 * Created by vat on 27.11.2015.
 */
public class DbObject {

    protected static final String MEMBER_SPLITTER = "/";

    private long id;
    private long uid;
    private String name;
    private String desc;
    private String author;
    private Date changed;
    private String membersIds;
    private boolean deleted;

    public DbObject(){
        uid = 0;
        name = "";
        desc = "";
        author = "";
        changed = new Date(0);
        membersIds = "" ;
        deleted = false;
    }

    public long getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getMembersIds() { return membersIds;}

    public void setUid(long uid) {
        this.uid = uid;
    }

    public Date getChanged() {
        return changed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setMembersIDs(String members) {membersIds = members;}

    public void setChanged(long changed) {
        this.changed = new Date(changed);
    }

    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public void setDeleted(boolean value) {deleted = value;}

    public String getAuthor() {
        return author;
    }

    public String[] getMembersArray() {
        if (!membersIds.isEmpty()){
            return membersIds.split(MEMBER_SPLITTER);
        }  else {
            return new String[0];
        }
    }

    public String getMemberSplitter() { return MEMBER_SPLITTER;}

    public boolean isDeleted() { return deleted;}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return new Long(uid).intValue();
    }
}
