package ru.vat78.fotimetracker.model;

/**
 * Created by vat on 27.11.2015.
 */
public class FOTT_Object {

    private long foid;
    private String name;
    private String desc;

    public long getId() {
        return foid;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public void setId(long foid) {
        this.foid = foid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
