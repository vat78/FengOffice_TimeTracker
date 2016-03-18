package ru.vat78.fotimetracker.connectors;

import java.util.ArrayList;
import java.util.Date;

import ru.vat78.fotimetracker.controllers.FOTT_Exceptions;
import ru.vat78.fotimetracker.model.FOTT_Object;

public interface FOTT_ObjectsConnector {

    public ArrayList<? extends FOTT_Object > loadObjects() throws FOTT_Exceptions;

    public ArrayList<? extends FOTT_Object > loadFilteredObjects(String filter) throws FOTT_Exceptions;

    public ArrayList<? extends FOTT_Object> loadChangedObjects(Date milestone) throws FOTT_Exceptions;

    public FOTT_Object loadObject(long objectId) throws FOTT_Exceptions;

    public boolean saveObjects(ArrayList<? extends FOTT_Object> savingObjects) throws FOTT_Exceptions;

    public long saveObject(FOTT_Object savingObject) throws FOTT_Exceptions;

    public boolean saveChangedObjects(ArrayList<? extends FOTT_Object> savingObjects, Date milestone) throws FOTT_Exceptions;

    public boolean deleteObjects(ArrayList<? extends FOTT_Object> deletingObjects) throws FOTT_Exceptions;

    public boolean deleteObject(FOTT_Object deletingObject) throws FOTT_Exceptions;
}
