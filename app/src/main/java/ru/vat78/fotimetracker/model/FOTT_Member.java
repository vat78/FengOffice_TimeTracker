package ru.vat78.fotimetracker.model;

import android.graphics.Color;

import java.util.Date;

/**
 * Category object
 * Created by vat on 27.11.2015.
 */
public class FOTT_Member extends FOTT_Object {

    final private int color;
    final private String path;
    final private int level;

    public FOTT_Member(FOTT_MemberBuilder builder) {
        super(builder);
        this.color = builder.color;
        this.path = builder.path;
        this.level = builder.level;
    }

    public int getColor() {return color;}

    public int getLevel() {
        return this.level;
    }

    public String getPath() {
        return path;
    }

}
