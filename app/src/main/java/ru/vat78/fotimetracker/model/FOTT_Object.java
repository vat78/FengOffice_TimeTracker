package ru.vat78.fotimetracker.model;

import java.util.Date;

/**
 * Base Object
 * Created by vat on 27.11.2015.
 */
public class FOTT_Object {

    final private long dbID;
    final private long webID;
    final private String name;
    final private String desc;
    final private String author;
    final private String[] membersWebIds;

    final private Date changed;
    final private boolean deleted;

    /*
    public FOTT_Object(long webID, String name, String desc, String author, String[] membersWebIds, Date changed, boolean deleted){
        this.webID = webID;
        this.name = name;
        this.desc = desc;
        this.author = author;
        this.membersWebIds = membersWebIds;
        this.changed = changed;
        this.deleted = deleted;
    }
    */

    public FOTT_Object(FOTT_ObjectBuilder builder){
        this.dbID = 0;
        this.webID = builder.webID;
        this.name = builder.name;
        this.desc = builder.desc;
        this.author = builder.author;
        this.membersWebIds = builder.membersWebIds;
        this.changed = builder.changed;
        this.deleted = builder.deleted;
    }

    public long getDbID() {
        return dbID;
    }

    public long getWebId() {
        return webID;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String[] getMembersWebIds() { return membersWebIds;}

    public Date getChanged() {
        return changed;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isDeleted() { return deleted;}

}
