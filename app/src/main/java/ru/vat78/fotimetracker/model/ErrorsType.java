package ru.vat78.fotimetracker.model;

import ru.vat78.fotimetracker.R;

/**
 * Created by vat on 28.04.17.
 */
public enum ErrorsType {
    CANT_SAVE_TO_DB(R.string.db_insert_empty_row),
    CANT_UPDATE_IN_DB(R.string.db_insert_empty_row);

    private final int description;

    ErrorsType(int desc) {
        this.description = desc;
    }

    public int getDescription() {
        return description;
    }
}
