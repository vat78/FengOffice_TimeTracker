package ru.vat78.fotimetracker.database;

import ru.vat78.fotimetracker.model.DbObject;

import java.util.List;

/**
 * Created by vat on 20.04.17.
 */
public interface IDao<T extends DbObject> {

    long save(T entry);
    long save(List<T> entries);

    T getByUid(long uid);
    boolean isExistInDB(long id);

    List<T> load();
    List<T> loadDeleted();
    List<T> loadChanged(long timestamp);

    boolean delete(T entry);
}
