package ru.vat78.fotimetracker.database;

import android.content.Context;
import ru.vat78.fotimetracker.IErrorsHandler;
import ru.vat78.fotimetracker.model.Member;
import ru.vat78.fotimetracker.model.Object;
import ru.vat78.fotimetracker.model.Task;
import ru.vat78.fotimetracker.model.Timeslot;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vat on 28.04.17.
 */
public class DBService {

    private final IDbConnect database;

    private final Map<Class<? extends Object>, IDao> daoMap;

    public DBService(Context context, IErrorsHandler errorsHandler) {
        database = new DB(context, errorsHandler);

        daoMap = new HashMap<>();
        daoMap.put(Member.class, new DaoMembers(database));
        daoMap.put(Task.class, new DaoTasks(database));
        daoMap.put(Timeslot.class, new DaoTimeslots(database));
    }

    public long databaseVersion() {
        return database.getCurrentDbVersion();
    }

}
