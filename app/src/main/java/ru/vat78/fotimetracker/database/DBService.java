package ru.vat78.fotimetracker.database;

import android.content.Context;
import android.support.annotation.NonNull;
import ru.vat78.fotimetracker.IErrorsHandler;
import ru.vat78.fotimetracker.model.DbObject;
import ru.vat78.fotimetracker.model.Member;
import ru.vat78.fotimetracker.model.Task;
import ru.vat78.fotimetracker.model.Timeslot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vat on 28.04.17.
 */
public class DBService {

    private final IDbConnect database;

    private final Context context;

    private final Map<Class<? extends DbObject>, IDao> daoMap;

    public DBService(Context context, IErrorsHandler errorsHandler) {
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

    public List<? extends DbObject> getAllDeleted(Class<? extends DbObject> type) {
        return null;
    }

    public List<? extends DbObject> getAllChanged(Class<? extends DbObject> type) {
        return null;
    }
}
