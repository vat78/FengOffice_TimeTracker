package ru.vat78.fotimetracker.connectors.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import ru.vat78.fotimetracker.connectors.FOTT_ObjectsConnector;
import ru.vat78.fotimetracker.controllers.FOTT_Exceptions;
import ru.vat78.fotimetracker.model.FOTT_Object;

import static ru.vat78.fotimetracker.connectors.database.FOTT_DBContract.COMMON_COLUMN_DELETED;
import static ru.vat78.fotimetracker.connectors.database.FOTT_DBContract.COMMON_COLUMN_FO_ID;

abstract class FOTT_DBCommon implements FOTT_ObjectsConnector {

    protected final SQLiteDatabase db;

    protected FOTT_DBCommon(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public ArrayList<? extends FOTT_Object> loadObjects() throws FOTT_Exceptions{
        return loadFilteredObjects("");
    }


    @Override
    public ArrayList<? extends FOTT_Object> loadChangedObjects(Date milestone) throws FOTT_Exceptions {
        String filter = FOTT_DBContract.COMMON_COLUMN_CHANGED + " >= " + milestone.getTime();
        return loadFilteredObjects(filter);
    }

    @Override
    public FOTT_Object loadObject(long objectId) throws FOTT_Exceptions  {

        if (objectId != 0) {

            String filter = FOTT_DBContract.COMMON_COLUMN_ID + " = " + objectId;

            ArrayList<? extends FOTT_Object> result = loadFilteredObjects(filter);
            if (result.size() > 0) return result.get(0);
        }

        return null;
    }

    @Override
    public boolean saveObjects(ArrayList<? extends FOTT_Object> savingObjects) throws FOTT_Exceptions {

        boolean result = true;

        for (FOTT_Object obj : savingObjects) {
            result = result && (saveObject(obj) != 0);
        }
        return result;
    }

    @Override
    public boolean saveChangedObjects(ArrayList<? extends FOTT_Object> savingObjects, Date milestone) throws FOTT_Exceptions {

        boolean result = true;

        for (FOTT_Object obj : savingObjects) {
            if (obj.getChanged().after(milestone))
                result = result && (saveObject(obj) != 0);
        }
        return result;
    }

    @Override
    public boolean deleteObjects(ArrayList<? extends FOTT_Object> deletingObjects) throws FOTT_Exceptions {

        boolean result = true;

        for (FOTT_Object obj : deletingObjects) {
            if (obj.getDbID() !=0) result = result && deleteObject(obj);
        }
        return result;
    }

    public ArrayList<? extends FOTT_Object> getObjectsMarkedAsDeleted() throws FOTT_Exceptions{
        return loadFilteredObjects(COMMON_COLUMN_DELETED + " > 0 ");
    }

    public long isExistInDB(long objectWebID) {

        long result = 0;
        try {
            FOTT_Object obj = findObjectByWebId(objectWebID);
            if (obj != null) result = obj.getDbID();
        } catch (FOTT_Exceptions ignore) {    }

        return result;
    }

    private FOTT_Object findObjectByWebId(long objectWebId) throws FOTT_Exceptions  {

        if (objectWebId != 0) {

            String filter = FOTT_DBContract.COMMON_COLUMN_FO_ID + " = " + objectWebId;

            ArrayList<? extends FOTT_Object> result = loadFilteredObjects(filter);
            if (result.size() > 0) return result.get(0);
        }

        return null;
    }

}
