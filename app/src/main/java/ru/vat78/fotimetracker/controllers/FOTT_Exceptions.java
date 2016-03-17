package ru.vat78.fotimetracker.controllers;

abstract public class FOTT_Exceptions extends Exception {

    public static enum ExeptionLevels{
        CRITICAL,
        WARNING,
        LOG
    }

    private final ExeptionLevels level;

    public FOTT_Exceptions(String detailMessage, ExeptionLevels level) {
        super(detailMessage);
        this.level = level;
    }
}
