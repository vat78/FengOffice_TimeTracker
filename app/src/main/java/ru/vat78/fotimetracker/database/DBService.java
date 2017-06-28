package ru.vat78.fotimetracker.database;

import android.content.Context;
import android.support.annotation.NonNull;
import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.IErrorsHandler;
import ru.vat78.fotimetracker.model.DbObject;
import ru.vat78.fotimetracker.model.Member;
import ru.vat78.fotimetracker.model.Task;
import ru.vat78.fotimetracker.model.Timeslot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vat on 28.04.17.
 */
public class DBService {

    private final IDbConnect database;

    private final App context;

    private final Map<Class<? extends DbObject>, IDao> daoMap;

    public DBService(App context, IErrorsHandler errorsHandler) {
        this.context = context;
        database = new DbSQLite(context, errorsHandler);

        daoMap = new HashMap<>();
        daoMap.put(Member.class, new DaoMembers(database));
        daoMap.put(Task.class, new DaoTasks(database));
        daoMap.put(Timeslot.class, new DaoTimeslots(database));
    }

    public long databaseVersion() {
        return database.getCurrentDbVersion();
    }


    public boolean saveAll(@NonNull List<? extends DbObject> entries, Class<? extends DbObject> type) {
        return daoMap.get(type).save(entries) == entries.size();
    }

    public boolean saveChanges(DbObject entry) {
        entry.setChanged(new Date());
        return daoMap.get(entry.getClass()).save(entry) > 0;
    }

    /**
     * Delete entry if it wasn't synced with remote platform
     * or update flag for deleting it in next synchronisation
     * @param entry
     * @return result of operation
     */
    public boolean deleteObject(DbObject entry) {
        boolean res;
        if (entry.getUid() == 0) {
            res = daoMap.get(entry.getClass()).delete(entry);
        } else {
            entry.setDeleted(true);
            res = daoMap.get(entry.getClass()).save(entry) > 0;
        }
        return res;
    }

    /**
     * Get all entries marked for deletion
     * @param type - required type of entries
     * @return list of entries
     */
    public List<? extends DbObject> getAllDeleted(Class<? extends DbObject> type) {
        return daoMap.get(type).loadDeleted();
    }

    public List<? extends DbObject> getAllChanged(Class<? extends DbObject> type) {
        return daoMap.get(type).loadChanged(context.getLastSync().getTime());
    }
}
