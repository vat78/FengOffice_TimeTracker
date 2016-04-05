package ru.vat78.fotimetracker.controllers;

abstract public class FOTT_Exceptions extends Exception {

    public static enum ExceptionLevels {
        CRITICAL,
        WARNING,
        LOG
    }

    private final ExceptionLevels level;

    public FOTT_Exceptions(String detailMessage, ExceptionLevels level) {
        super(detailMessage);
        this.level = level;
    }

    public ExceptionLevels getLevel() {return level;}
}
