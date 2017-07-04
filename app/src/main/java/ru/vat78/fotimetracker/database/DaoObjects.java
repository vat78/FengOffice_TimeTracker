package ru.vat78.fotimetracker.database;

import android.support.annotation.NonNull;
import ru.vat78.fotimetracker.model.DbObject;

import java.util.List;
import java.util.Map;

import static ru.vat78.fotimetracker.database.DBContract.COLUMN_NAME_DELETED;
import static ru.vat78.fotimetracker.database.DBContract.COLUMN_NAME_CHANGED;

/**
 * Created by vat on 28.06.17.
 */
public abstract class DaoObjects<T extends DbObject> implements IDao<T> {

    abstract protected IDbConnect getDatabase();

    abstract protected String getTableName();

    abstract protected Map<String, Object> convertForDB(T entry);

    abstract protected List<T> loadWithFilter(String filterConditions);

    /**
     * Delete record in db
     * @param entry
     * @return
     */
    public boolean delete(T entry) {
        getDatabase().beginTransaction();
        boolean res = getDatabase().delete(getTableName(), entry.getId());
        getDatabase().endTransaction();
        return res;
    }

    public long save(@NonNull T entry) {
        getDatabase().beginTransaction();
        Map ts = convertForDB((T) entry);
        long res = getDatabase().insertOrUpdate(getTableName(), ts);
        if (res != 0 ) entry.setId(res);
        getDatabase().endTransaction();
        return res;
    }

    public long save(@NonNull List<T> entries) {
        long cntr = 0;
        for (T o : entries) {
            if (o.getUid() != 0) {
                if (save(o) != -1) cntr++;
            }
        }
        return cntr;
    }

    /**
     * Load all records from table
     * @return list of all records in database
     */
    @Override
    @NonNull
    public List<T> load() {
        return loadWithFilter("");
    }

    /**
     * Load records marked for deletion
     * @return list of all records in database
     */
    @Override
    @NonNull
    public List<T> loadDeleted() {
        return loadWithFilter(COLUMN_NAME_DELETED + " = true");
    }

    /**
     * Load records changed after timestamp
     * @param timestamp
     * @return list of all records in database
     */
    @Override
    @NonNull
    public List<T> loadChanged(long timestamp) {
        return loadWithFilter(COLUMN_NAME_CHANGED + " > " + timestamp);
    }

    @Override
    public boolean isExistInDB(long uid) {
        T res = getByUid(uid);
        return (res.getUid() == uid);
    }


}
