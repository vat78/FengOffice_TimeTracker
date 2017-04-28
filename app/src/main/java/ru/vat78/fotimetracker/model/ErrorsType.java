package ru.vat78.fotimetracker.model;

import ru.vat78.fotimetracker.R;

/**
 * Created by vat on 28.04.17.
 */
public enum ErrorsType {
    CANT_SAVE_TO_DB(R.string.db_insert_empty_row),
    CANT_UPDATE_IN_DB(R.string.db_insert_empty_row),

    TEST_CONNECTION_ERROR(R.string.error_no_internet),

    WRONG_URL(0),
    CANT_CONNECT_TO_SERVER(0),
    WRONG_WEB_ANSWER(0),
    JSON_PARSING_ERROR(0),

    SYSTEM_ERROR(0),
    ;

    private final int description;

    ErrorsType(int desc) {
        this.description = desc;
    }

    public int getDescription() {
        return description;
    }
}
