package ru.vat78.fotimetracker.fo_api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.views.FOTT_ErrorsHandler;

import static ru.vat78.fotimetracker.fo_api.FOAPI_Dictionary.*;

/**
 * Created by vat on 30.11.2015.
 */
public class FOAPI_Tasks {
    private static final String CLASS_NAME = "FOAPI_Tasks";

    public static ArrayList<FOTT_Task> load(FOTT_App app){
        JSONObject jo = app.getWeb_service().executeAPI(FO_METHOD_LISTING, FO_SERVICE_TASKS,
                new String[] {FO_API_ARG_STATUS, "0"});
        return convertResults(app,jo,true);
    }

    public static ArrayList<FOTT_Task> load(FOTT_App app, Date timestamp){
        String[] args = new String[2];
        long l = (long) timestamp.getTime() / FO_API_DATE_CONVERTOR;
        if (l != 0) {
            args[0] = FO_API_ARG_LASTUPDATE;
            args[1] = "" + l;
        } else {
            //Select only active tasks (not completed, not deleted, not archived and start date < now)
            args[0]= FO_API_ARG_STATUS;
            args[1] = "10";
        }
        JSONObject jo = app.getWeb_service().executeAPI(FO_METHOD_LISTING,FO_SERVICE_TASKS, args);
        if (!app.getWeb_service().getError().isEmpty())
            app.getError().error_handler(FOTT_ErrorsHandler.ERROR_SAVE_ERROR, CLASS_NAME, app.getWeb_service().getError());
        return convertResults(app,jo,(l==0));
    }

    private static ArrayList<FOTT_Task> convertResults(FOTT_App app, JSONObject data,boolean checkCurrentTask){

        JSONArray list = null;
        JSONObject jo;
        ArrayList<FOTT_Task> res = new ArrayList<>();
        if (data == null) {return res;}

        long current_task = app.getCurTask();
        boolean isIncludeCurrentTask = !checkCurrentTask;

        try {
            list = data.getJSONArray(FO_API_MAIN_OBJ);
        } catch (Exception e) {
            app.getError().error_handler(FOTT_ErrorsHandler.ERROR_SAVE_ERROR, CLASS_NAME, e.getMessage());
        }
        if (list == null) {return null;}

        for (int i = 0; i < list.length(); i++) {
            FOTT_Task el = null;
            try {
                jo = list.getJSONObject(i);

                el = convertToTask(app, jo);
            }
            catch (Exception e) {
                app.getError().error_handler(FOTT_ErrorsHandler.ERROR_LOG_MESSAGE, CLASS_NAME, e.getMessage());
            }
            finally {
                if (el != null) {
                    if (!res.add(el)) {
                        break;
                    }
                    isIncludeCurrentTask = isIncludeCurrentTask || (current_task == el.getId());
                }
            }
        }

        if (current_task > 0 && !isIncludeCurrentTask){
            FOTT_Task el = getTaskByID(app, current_task);
            if (el == null){
                app.setCurTask(0);
            } else {
                res.add(el);
            }
        }
        return res;
    }

    private static FOTT_Task getTaskByID(FOTT_App app, long id) {
        String[] args = new String[2];
        args[0] = FO_API_FIELD_ID;
        args[1] = "" + id;
        return convertToTask(app, app.getWeb_service().executeAPI(FO_METHOD_LISTING,FO_SERVICE_TASKS,args));
    }

    private static FOTT_Task convertToTask(FOTT_App app, JSONObject jsonObject) {
        FOTT_Task el = null;
        try {
            long id = jsonObject.getLong(FO_API_FIELD_ID);
            String s = jsonObject.getString(FO_API_FIELD_NAME);

            el = new FOTT_Task(id, s);

            if (jsonObject.isNull(FO_API_FIELD_DESC)) {
                el.setDesc("");
            } else {
                el.setDesc(jsonObject.getString(FO_API_FIELD_DESC));
            }

            s = "";
            if (!jsonObject.isNull(FO_API_FIELD_MEMPATH)) {
                JSONArray ja = jsonObject.getJSONArray(FO_API_FIELD_MEMPATH);
                for (int j = 0; j < ja.length(); j++)
                    s = s + ja.getString(j) + el.getMemberSplitter();
            }
            el.setMembersIDs(s);

            String tmp = FO_API_FALSE;
            if (!jsonObject.isNull(FO_API_FIELD_STARTDATE))
                tmp = jsonObject.getString(FO_API_FIELD_STARTDATE);
            if (tmp.equalsIgnoreCase(FO_API_FALSE)) {
                el.setStartDate(0);
            } else {
                el.setStartDate(jsonObject.getLong(FO_API_FIELD_STARTDATE) * FO_API_DATE_CONVERTOR);
            }

            tmp = FO_API_FALSE;
            if (!jsonObject.isNull(FO_API_FIELD_DUEDATE))
                tmp = jsonObject.getString(FO_API_FIELD_DUEDATE);
            if (tmp.equalsIgnoreCase(FO_API_FALSE)) {
                el.setDueDate(0);
            } else {
                el.setDueDate(jsonObject.getLong(FO_API_FIELD_DUEDATE) * FO_API_DATE_CONVERTOR);
            }

            if (!jsonObject.isNull(FO_API_FIELD_PRIORITY))
                el.setPriority(jsonObject.getInt(FO_API_FIELD_PRIORITY));

            if (!jsonObject.isNull(FO_API_FIELD_STATUS))
                el.setStatus(jsonObject.getInt(FO_API_FIELD_STATUS));

            if (!jsonObject.isNull(FOAPI_Dictionary.FO_API_FIELD_USETIMESLOTS))
                el.setCanAddTimeslots(jsonObject.getString(FOAPI_Dictionary.FO_API_FIELD_USETIMESLOTS).equalsIgnoreCase(FO_API_TRUE));

                /*
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_ASSIGNEDBY))
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_ASSIGNEDBY,jo.getString(FOAPI_Dictionary.FO_API_FIELD_ASSIGNEDBY));
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_ASSIGNEDTO))
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_ASSIGNEDTO,jo.getString(FOAPI_Dictionary.FO_API_FIELD_ASSIGNEDTO));
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_PERCENT))
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_PERCENT,jo.getInt(FOAPI_Dictionary.FO_API_FIELD_PERCENT));
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_WORKEDTIME))
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_WORKEDTIME,jo.getLong(FOAPI_Dictionary.FO_API_FIELD_WORKEDTIME));
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_PENDINGTIME))
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_PENDINGTIME,jo.getLong(FOAPI_Dictionary.FO_API_FIELD_PENDINGTIME));
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_USETIMESLOTS))
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_USETIMESLOTS,(jo.getString(FOAPI_Dictionary.FO_API_FIELD_USETIMESLOTS) == FOAPI_Dictionary.FO_API_TRUE));
                */
        }
        catch (Exception e) {
            app.getError().error_handler(FOTT_ErrorsHandler.ERROR_LOG_MESSAGE, CLASS_NAME, e.getMessage());
        }
        return el;
    }

    public static long save(FOTT_App app, FOTT_Task task) {
        long res = 0;
        JSONObject jo;
        if (task == null) return res;
            String[] args = convertTaskForAPI(task);
            jo = app.getWeb_service().executeAPI(FO_METHOD_SAVE_OBJ, FO_SERVICE_TASKS, args);
        try {
            res = jo.getLong(FO_API_FIELD_ID);
        } catch (Exception e) {}

        if (res !=0 ) {
            try {
                if (task.getStatus() == 0) {
                    jo = app.getWeb_service().executeAPI(FO_METHOD_COMPLETE_TASK, res, FO_ACTION_OPEN_TASK);
                } else {
                    jo = app.getWeb_service().executeAPI(FO_METHOD_COMPLETE_TASK, res, FO_ACTION_COMPLETE_TASK);
                }
            } catch (Exception e) {}
        }
        return res;
    }

    private static String[] convertTaskForAPI(FOTT_Task task) {
        String[] res = new String[12];
        long l;
        res[0] = FO_API_FIELD_ID;
        res[1] = "";
        if (task.getId() > 0) res[1] = "" + task.getId();
        res[2] = FO_API_FIELD_NAME;
        res[3] = task.getName();
        res[4] = FO_API_FIELD_DESC;
        res[5] = task.getDesc();
        res[6] = FO_API_FIELD_DUEDATE;
        l = (long) task.getDueDate().getTime() / FO_API_DATE_CONVERTOR;
        res[7] = "" + l;
        res[8] = FO_API_FIELD_STATUS;
        res[9] = "" + task.getStatus();
        if (task.getMembersIds().isEmpty()) {
            res[10] = "";
            res[11] = "";
        } else {
            res[10] = FO_API_FIELD_MEMBERS;
            res[11] = "[";
            String[] members = task.getMembersArray();
            for (String member : members) res[11] += "\"" + member + "\",";
            res[11] += "]";
        }
        return res;
    }

    public static boolean delete(FOTT_App app, FOTT_Task task) {
        boolean res = false;

            if (task.getId() > 0) {
                JSONObject jo = app.getWeb_service().executeAPI(FO_METHOD_DELETE_OBJ, task.getId());
                if (jo == null) {
                    res = false;
                } else {
                    try {
                        res = (jo.getString(FO_API_FIELD_RESULT).equals(FO_API_TRUE));
                    } catch (Exception e){
                    }
                }
            }

        return res;
    }
}
