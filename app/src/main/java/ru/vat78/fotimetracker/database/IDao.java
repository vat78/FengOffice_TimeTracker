package ru.vat78.fotimetracker.database;

import ru.vat78.fotimetracker.model.Object;

import java.util.List;

/**
 * Created by vat on 20.04.17.
 */
public interface IDao<T extends Object> {

    long save(T entity);
    long save(List<T> entities);
    T getByUid(long uid);
    boolean isExistInDB(long memberID);
    List<T> load();

}
