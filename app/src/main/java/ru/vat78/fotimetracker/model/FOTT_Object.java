package ru.vat78.fotimetracker.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by vat on 27.11.2015.
 */
public class FOTT_Object {

    private long foid;
    private String name;
    private String desc;
    private String author;
    private Date changed;
    private String membersIds;

    public FOTT_Object(){
        foid = 0;
        name = "";
        desc = "";
        author = "";
        changed = new Date(0);
        membersIds = "" ;
    }

    public long getId() {
        return foid;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getMembersIds() { return membersIds;}

    public void setId(long foid) {
        this.foid = foid;
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

    public void setMembersPath(String members) {membersIds = members;}

    public void setChanged(long changed) {
        this.changed = new Date(changed);
    }

    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public String getAuthor() {
        return author;
    }

    public String[] getMembersArray() {
        if (!membersIds.isEmpty()){
            return membersIds.split("/");
        }  else {
            return null;
        }
    }

}
