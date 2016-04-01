package ru.vat78.fotimetracker.connectors.fo_api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import ru.vat78.fotimetracker.controllers.FOTT_Exceptions;
import ru.vat78.fotimetracker.connectors.FOTT_ObjectsConnector;
import ru.vat78.fotimetracker.model.FOTT_Object;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.model.FOTT_TaskBuilder;

public class FOAPI_Tasks implements FOTT_ObjectsConnector {

    private static final String CLASS_NAME = "FOAPI_Tasks";

    private final FOAPI_Connector webService;

    public FOAPI_Tasks(FOAPI_Connector webService) {
        this.webService = webService;
    }

    @Override
    public ArrayList<FOTT_Task> loadObjects() throws FOAPI_Exceptions{
        JSONObject jo = webService.executeAPI(FOAPI_Dictionary.FO_METHOD_LISTING, FOAPI_Dictionary.FO_SERVICE_TASKS,
                createRequestArgsFromDate(0));
        return convertJSONResults(jo, true);
    }

    @Override
    public ArrayList<FOTT_Task> loadFilteredObjects(String filter) throws FOAPI_Exceptions {
        return loadObjects();
    }

    @Override
    public ArrayList<FOTT_Task> loadChangedObjects(Date milestone) throws FOAPI_Exceptions {

        JSONObject jo = webService.executeAPI(FOAPI_Dictionary.FO_METHOD_LISTING,
                FOAPI_Dictionary.FO_SERVICE_TASKS, createRequestArgsFromDate(milestone.getTime()));
        return convertJSONResults(jo, (milestone.getTime() == 0));
    }

    @Override
    public FOTT_Task loadObject(long objectId) throws FOAPI_Exceptions {

                /*
                ToDo: load task by ID
        HashMap<String,String> args = new HashMap<>();
        args.put(FOAPI_Dictionary.FO_API_FIELD_ID, "" + objectId);

        return readElement(webService.executeAPI(FOAPI_Dictionary.FO_METHOD_LISTING,
                FOAPI_Dictionary.FO_SERVICE_TASKS, args)).buildObject();
                */

        return null;
    }

    @Override
    public long saveObject(FOTT_Object savingObject) throws FOAPI_Exceptions {

        long result = 0;
        if (savingObject == null) return result;

        FOTT_Task task = (FOTT_Task) savingObject;
        HashMap<String,String> args = convertTaskForAPI(task);
        JSONObject jo = webService.executeAPI(FOAPI_Dictionary.FO_METHOD_SAVE_OBJ,
                FOAPI_Dictionary.FO_SERVICE_TASKS, args);

        try {
            result = jo.getLong(FOAPI_Dictionary.FO_API_FIELD_ID);
            saveTaskStatus(result, task);
        } catch (JSONException ignored) {}

        return result;
    }

    @Override
    public boolean saveObjects(ArrayList<? extends FOTT_Object> savingObjects) throws FOAPI_Exceptions { return false; }

    @Override
    public boolean saveChangedObjects(ArrayList<? extends FOTT_Object> savingObjects, Date milestone) throws FOAPI_Exceptions { return false; }

    @Override
    public boolean deleteObjects(ArrayList<? extends FOTT_Object> deletingObjects) throws FOAPI_Exceptions { return false; }

    @Override
    public boolean deleteObject(FOTT_Object deletingObject) throws FOAPI_Exceptions {

        boolean success = false;
        FOTT_Task task = (FOTT_Task) deletingObject;

        if (task.getWebId() > 0) {
            JSONObject jo = webService.executeAPI(FOAPI_Dictionary.FO_METHOD_DELETE_OBJ, task.getWebId());

            try {
                success = (jo.getString(FOAPI_Dictionary.FO_API_FIELD_RESULT).startsWith(FOAPI_Dictionary.FO_API_TRUE));
            } catch (Exception e){
                success = false;
            }
        }
        return success;
    }

    private ArrayList<FOTT_Task> convertJSONResults(JSONObject data, boolean checkCurrentTask) throws FOAPI_Exceptions{

        JSONArray list = null;
        ArrayList<FOTT_Task> result = new ArrayList<>();
        if (data == null) {return result;}

        //ToDo: check current task
        //long current_task = app.getCurTask();
        //boolean isIncludeCurrentTask = !checkCurrentTask;

        try {
            list = data.getJSONArray(FOAPI_Dictionary.FO_API_MAIN_OBJ);
        } catch (JSONException e) {
            throw new FOAPI_Exceptions(CLASS_NAME + "\n" + data, FOAPI_Exceptions.ECodes.JSON_ARRAY_MISMATCH, FOTT_Exceptions.ExeptionLevels.WARNING);
        }
        if (list != null) {

            for (int i = 0; i < list.length(); i++) {

                FOTT_TaskBuilder t = null;
                try {
                    t = readElement(list.getJSONObject(i));
                } catch (JSONException ignored) { }

                if (t != null)
                    result.add(t.buildObject());
            }
        }

        /*
        if (current_task > 0 && !isIncludeCurrentTask){
            FOTT_Task el = getTaskByID(app, current_task);
            if (el == null){
                app.setCurTask(0);
            } else {
                res.add(el);
            }
        } */
        return result;
    }

    private FOTT_TaskBuilder readElement(JSONObject jsonObject) {
        FOTT_TaskBuilder element = null;
        FOAPI_JSONHandler h = new FOAPI_JSONHandler(jsonObject);

        long id = h.getLong(FOAPI_Dictionary.FO_API_FIELD_ID, 0);
        String s = h.getString(FOAPI_Dictionary.FO_API_FIELD_NAME, "");

        if (id != 0 && !s.isEmpty()) {
            element = new FOTT_TaskBuilder();
            element.setWebID(id);
            element.setName(s);

            element.setDesc(h.getString(FOAPI_Dictionary.FO_API_FIELD_DESC, ""));
            element.setMembersWebIds(h.getArray(FOAPI_Dictionary.FO_API_FIELD_MEMPATH));

            element.setStartDate(h.getDateTime(FOAPI_Dictionary.FO_API_FIELD_STARTDATE, 0));
            element.setDueDate(h.getDateTime(FOAPI_Dictionary.FO_API_FIELD_DUEDATE, 0));

            element.setPriority(h.getInt(FOAPI_Dictionary.FO_API_FIELD_PRIORITY, 0));
            element.setStatus(getStatusFromJSON(h));
            element.setCanAddTimeslots(h.getBoolean(FOAPI_Dictionary.FO_API_FIELD_USETIMESLOTS, true));
        }

        return element;
    }

    private HashMap<String, String> convertTaskForAPI(FOTT_Task task) {
        HashMap<String, String> result = new HashMap<>();
        long l;

        if (task.getWebId() != 0) result.put(FOAPI_Dictionary.FO_API_FIELD_ID,"" + task.getWebId());
        result.put(FOAPI_Dictionary.FO_API_FIELD_NAME, task.getName());
        result.put(FOAPI_Dictionary.FO_API_FIELD_DESC, task.getDesc());

        l = (long) task.getDueDate().getTime() / FOAPI_Dictionary.FO_API_DATE_CONVERTOR;
        result.put(FOAPI_Dictionary.FO_API_FIELD_DUEDATE, "" + l);

        if (task.getMembersWebIds().length != 0) {
            String s = "[";
            for (String member : task.getMembersWebIds()) s += "\"" + member + "\",";
            s = s.substring(0,s.length()-1) + "]";
            result.put(FOAPI_Dictionary.FO_API_FIELD_MEMBERS, s);
        }
        return result;
    }

    private HashMap<String,String> createRequestArgsFromDate(long milestone) {

        HashMap<String,String> args = new HashMap<>();

        milestone = (long) milestone / FOAPI_Dictionary.FO_API_DATE_CONVERTOR;
        if (milestone != 0) {
            args.put(FOAPI_Dictionary.FO_API_ARG_LASTUPDATE,  "" + milestone);
        } else {
            //Select only active tasks (not completed, not deleted, not archived and start date < now)
            args.put(FOAPI_Dictionary.FO_API_ARG_STATUS,  FOAPI_Dictionary.FO_API_TASK_STATUS_ACTIVE);
        }
        return args;
    }

    private void saveTaskStatus(long taskWebId, FOTT_Task task) throws FOAPI_Exceptions {

        if (taskWebId !=0 ) {

            switch (task.getStatus()) {
                case FOTT_Task.STATUS_ACTIVE:
                    webService.executeAPI(FOAPI_Dictionary.FO_METHOD_COMPLETE_TASK,
                                taskWebId, FOAPI_Dictionary.FO_ACTION_OPEN_TASK);
                    break;

                case FOTT_Task.STATUS_COMPLETED:
                    webService.executeAPI(FOAPI_Dictionary.FO_METHOD_COMPLETE_TASK,
                                taskWebId, FOAPI_Dictionary.FO_ACTION_COMPLETE_TASK);
                    break;
            }
        }
    }

    private int getStatusFromJSON(FOAPI_JSONHandler element) {

        int result;
        int status = element.getInt(FOAPI_Dictionary.FO_API_FIELD_STATUS,0);
        switch (status) {
            case 0:
                result = FOTT_Task.STATUS_ACTIVE;
                break;
            default:
                result = FOTT_Task.STATUS_COMPLETED;
                break;
        }
        return  result;
    }
}
