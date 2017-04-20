package ru.vat78.fotimetracker.fengoffice.vatApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.model.Task;
import ru.vat78.fotimetracker.views.ErrorsHandler;

/**
 * Created by vat on 30.11.2015.
 */
public class ApiTasks {
    private static final String CLASS_NAME = "ApiTasks";

    public ArrayList<Task> load(App app){
        JSONObject jo = app.getWebService().executeAPI(ApiDictionary.FO_METHOD_LISTING, ApiDictionary.FO_SERVICE_TASKS,
                new String[] {ApiDictionary.FO_API_ARG_STATUS, "0"});
        return convertResults(app,jo,true);
    }

    public ArrayList<Task> load(App app, Date timestamp){
        String[] args = new String[2];
        long l = (long) timestamp.getTime() / ApiDictionary.FO_API_DATE_CONVERTOR;
        if (l != 0) {
            args[0] = ApiDictionary.FO_API_ARG_LASTUPDATE;
            args[1] = "" + l;
        } else {
            //Select only active tasks (not completed, not deleted, not archived and start date < now)
            args[0]= ApiDictionary.FO_API_ARG_STATUS;
            args[1] = "10";
        }
        JSONObject jo = app.getWebService().executeAPI(ApiDictionary.FO_METHOD_LISTING,ApiDictionary.FO_SERVICE_TASKS, args);
        if (!app.getWebService().getError().isEmpty())
            app.getError().error_handler(ErrorsHandler.ERROR_SAVE_ERROR, CLASS_NAME, app.getWebService().getError());

        return convertResults(app,jo,(l==0));
    }

    private ArrayList<Task> convertResults(App app, JSONObject data, boolean checkCurrentTask){

        JSONArray list = null;
        JSONObject jo;
        ArrayList<Task> res = new ArrayList<>();
        if (data == null) {return res;}

        long current_task = app.getCurTask();
        boolean isIncludeCurrentTask = !checkCurrentTask;

        try {
            list = data.getJSONArray(ApiDictionary.FO_API_MAIN_OBJ);
        } catch (Exception e) {
            app.getError().error_handler(ErrorsHandler.ERROR_SAVE_ERROR, CLASS_NAME, e.getMessage());
        }
        if (list == null) {return null;}

        for (int i = 0; i < list.length(); i++) {
            Task el = null;
            try {
                jo = list.getJSONObject(i);

                el = convertToTask(app, jo);
            }
            catch (Exception e) {
                app.getError().error_handler(ErrorsHandler.ERROR_LOG_MESSAGE, CLASS_NAME, e.getMessage());
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
            Task el = getTaskByID(app, current_task);
            if (el == null){
                app.setCurTask(0);
            } else {
                res.add(el);
            }
        }
        return res;
    }

    private Task getTaskByID(App app, long id) {
        String[] args = new String[2];
        args[0] = ApiDictionary.FO_API_FIELD_ID;
        args[1] = "" + id;
        return convertToTask(app, app.getWebService().executeAPI(ApiDictionary.FO_METHOD_LISTING, ApiDictionary.FO_SERVICE_TASKS,args));
    }

    private Task convertToTask(App app, JSONObject jsonObject) {
        Task el = null;
        try {
            long id = jsonObject.getLong(ApiDictionary.FO_API_FIELD_ID);
            String s = jsonObject.getString(ApiDictionary.FO_API_FIELD_NAME);

            el = new Task(id, s);

            if (jsonObject.isNull(ApiDictionary.FO_API_FIELD_DESC)) {
                el.setDesc("");
            } else {
                el.setDesc(jsonObject.getString(ApiDictionary.FO_API_FIELD_DESC));
            }

            s = "";
            if (!jsonObject.isNull(ApiDictionary.FO_API_FIELD_MEMPATH)) {
                JSONArray ja = jsonObject.getJSONArray(ApiDictionary.FO_API_FIELD_MEMPATH);
                for (int j = 0; j < ja.length(); j++) {
                    String mem = ja.getString(j);
                    if (mem != null && mem.length() > 0) {
                        if (mem.getBytes()[0] == '{') {
                            JSONObject memObj = new JSONObject(mem);
                            mem = memObj.getString("member_id");
                        }
                        s = s + mem +el.getMemberSplitter();
                    }
                }
            }
            el.setMembersIDs(s);

            String tmp = ApiDictionary.FO_API_FALSE;
            if (!jsonObject.isNull(ApiDictionary.FO_API_FIELD_STARTDATE))
                tmp = jsonObject.getString(ApiDictionary.FO_API_FIELD_STARTDATE);
            if (tmp.equalsIgnoreCase(ApiDictionary.FO_API_FALSE)) {
                el.setStartDate(0);
            } else {
                el.setStartDate(jsonObject.getLong(ApiDictionary.FO_API_FIELD_STARTDATE) * ApiDictionary.FO_API_DATE_CONVERTOR);
            }

            tmp = ApiDictionary.FO_API_FALSE;
            if (!jsonObject.isNull(ApiDictionary.FO_API_FIELD_DUEDATE))
                tmp = jsonObject.getString(ApiDictionary.FO_API_FIELD_DUEDATE);
            if (tmp.equalsIgnoreCase(ApiDictionary.FO_API_FALSE)) {
                el.setDueDate(0);
            } else {
                el.setDueDate(jsonObject.getLong(ApiDictionary.FO_API_FIELD_DUEDATE) * ApiDictionary.FO_API_DATE_CONVERTOR);
            }

            if (!jsonObject.isNull(ApiDictionary.FO_API_FIELD_PRIORITY))
                el.setPriority(jsonObject.getInt(ApiDictionary.FO_API_FIELD_PRIORITY));

            if (!jsonObject.isNull(ApiDictionary.FO_API_FIELD_STATUS))
                el.setStatus(jsonObject.getInt(ApiDictionary.FO_API_FIELD_STATUS));

            if (!jsonObject.isNull(ApiDictionary.FO_API_FIELD_USETIMESLOTS))
                el.setCanAddTimeslots(jsonObject.getString(ApiDictionary.FO_API_FIELD_USETIMESLOTS).equalsIgnoreCase(ApiDictionary.FO_API_TRUE));

                /*
                if (!jo.isNull(ApiDictionary.FO_API_FIELD_ASSIGNEDBY))
                    el.put(FOTT_DBContract.DaoTasks.COLUMN_NAME_ASSIGNEDBY,jo.getString(ApiDictionary.FO_API_FIELD_ASSIGNEDBY));
                if (!jo.isNull(ApiDictionary.FO_API_FIELD_ASSIGNEDTO))
                    el.put(FOTT_DBContract.DaoTasks.COLUMN_NAME_ASSIGNEDTO,jo.getString(ApiDictionary.FO_API_FIELD_ASSIGNEDTO));
                if (!jo.isNull(ApiDictionary.FO_API_FIELD_PERCENT))
                    el.put(FOTT_DBContract.DaoTasks.COLUMN_NAME_PERCENT,jo.getInt(ApiDictionary.FO_API_FIELD_PERCENT));
                if (!jo.isNull(ApiDictionary.FO_API_FIELD_WORKEDTIME))
                    el.put(FOTT_DBContract.DaoTasks.COLUMN_NAME_WORKEDTIME,jo.getLong(ApiDictionary.FO_API_FIELD_WORKEDTIME));
                if (!jo.isNull(ApiDictionary.FO_API_FIELD_PENDINGTIME))
                    el.put(FOTT_DBContract.DaoTasks.COLUMN_NAME_PENDINGTIME,jo.getLong(ApiDictionary.FO_API_FIELD_PENDINGTIME));
                if (!jo.isNull(ApiDictionary.FO_API_FIELD_USETIMESLOTS))
                    el.put(FOTT_DBContract.DaoTasks.COLUMN_NAME_USETIMESLOTS,(jo.getString(ApiDictionary.FO_API_FIELD_USETIMESLOTS) == ApiDictionary.FO_API_TRUE));
                */
        }
        catch (Exception e) {
            app.getError().error_handler(ErrorsHandler.ERROR_LOG_MESSAGE, CLASS_NAME, e.getMessage());
        }
        return el;
    }

    public long save(App app, Task task) {
        long res = 0;
        JSONObject jo;
        if (task == null) return res;
            String[] args = convertTaskForAPI(task);
            jo = app.getWebService().executeAPI(ApiDictionary.FO_METHOD_SAVE_OBJ, ApiDictionary.FO_SERVICE_TASKS, args);
        try {
            res = jo.getLong(ApiDictionary.FO_API_FIELD_ID);
        } catch (Exception e) {}

        if (res !=0 ) {
            try {
                if (task.getStatus() == 0) {
                    jo = app.getWebService().executeAPI(ApiDictionary.FO_METHOD_COMPLETE_TASK, res, ApiDictionary.FO_ACTION_OPEN_TASK);
                } else {
                    jo = app.getWebService().executeAPI(ApiDictionary.FO_METHOD_COMPLETE_TASK, res, ApiDictionary.FO_ACTION_COMPLETE_TASK);
                }
            } catch (Exception e) {}
        }
        return res;
    }

    private String[] convertTaskForAPI(Task task) {
        String[] res = new String[12];
        long l;
        res[0] = ApiDictionary.FO_API_FIELD_ID;
        res[1] = "";
        if (task.getId() > 0) res[1] = "" + task.getId();
        res[2] = ApiDictionary.FO_API_FIELD_NAME;
        res[3] = task.getName();
        res[4] = ApiDictionary.FO_API_FIELD_DESC;
        res[5] = task.getDesc();
        res[6] = ApiDictionary.FO_API_FIELD_DUEDATE;
        l = (long) task.getDueDate().getTime() / ApiDictionary.FO_API_DATE_CONVERTOR;
        res[7] = "" + l;
        res[8] = ApiDictionary.FO_API_FIELD_STATUS;
        res[9] = "" + task.getStatus();
        if (task.getMembersIds().isEmpty()) {
            res[10] = "";
            res[11] = "";
        } else {
            res[10] = ApiDictionary.FO_API_FIELD_MEMBERS;
            res[11] = "[";
            String[] members = task.getMembersArray();
            for (String member : members) res[11] += "\"" + member + "\",";
            res[11] += "]";
        }
        return res;
    }

    public boolean delete(App app, Task task) {
        boolean res = false;

            if (task.getId() > 0) {
                JSONObject jo = app.getWebService().executeAPI(ApiDictionary.FO_METHOD_DELETE_OBJ, task.getId());
                if (jo == null) {
                    res = false;
                } else {
                    try {
                        res = (jo.getString(ApiDictionary.FO_API_FIELD_RESULT).equalsIgnoreCase(ApiDictionary.FO_API_TRUE));
                    } catch (Exception e){
                    }
                }
            }

        return res;
    }
}
