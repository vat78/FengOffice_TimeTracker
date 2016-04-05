package ru.vat78.fotimetracker.views;

import android.text.TextUtils;

import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.model.FOTT_TaskBuilder;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;
import ru.vat78.fotimetracker.model.FOTT_TimeslotBuilder;

/**
 * Parcel some information for transmit in Activities
 */
public class FOTT_Parcel {

    public static String[] parcelTimeslot(FOTT_Timeslot timeslot) {

        String[] res = new String[6];
        res[0] = "" + timeslot.getDbID();
        res[1] = "" + timeslot.getWebId();
        res[2] = timeslot.getDesc();
        res[3] = "" + timeslot.getStart().getTime();
        res[4] = "" + timeslot.getDuration();
        res[5] = "" + timeslot.getTaskId();
        return res;
    }

    public static FOTT_TimeslotBuilder unpackTimeslot(String[] ts) {
        return unpackTimeslot(ts, new FOTT_TimeslotBuilder());
    }


    public static FOTT_TimeslotBuilder unpackTimeslot(String[] ts, FOTT_TimeslotBuilder template) {

        if (!TextUtils.isEmpty(ts[0])) template.setDbID(Long.getLong(ts[0]));
        if (!TextUtils.isEmpty(ts[1])) template.setWebID(Long.getLong(ts[1]));
        if (!TextUtils.isEmpty(ts[2])) template.setDesc(ts[2]);

        long l = 15 * 60 * 1000;
        if (!TextUtils.isEmpty(ts[4])) l = Long.getLong(ts[4]);

        if (!TextUtils.isEmpty(ts[3])) {
            template.setStart(Long.getLong(ts[3]));
        } else {
            template.setStart(System.currentTimeMillis() - l);
        }
        template.setDuration(l);

        if (!TextUtils.isEmpty(ts[5])) template.setTaskWebId(Long.getLong(ts[5]));
        return template;
    }

    public static String[] parcelTask(FOTT_Task task) {

        String[] res = new String[6];
        res[0] = "" + task.getDbID();
        res[1] = "" + task.getWebId();
        res[2] = task.getName();
        res[3] = "" + task.getStartDate().getTime();
        res[4] = "" + task.getDueDate().getTime();
        res[5] = "" + task.getStatus();
        return res;
    }

    public static FOTT_TaskBuilder unpackTask(String[] taskData) {
        return unpackTask(taskData, new FOTT_TaskBuilder());
    }


    public static FOTT_TaskBuilder unpackTask(String[] taskData, FOTT_TaskBuilder template) {

        if (!TextUtils.isEmpty(taskData[0])) template.setDbID(Long.getLong(taskData[0]));
        if (!TextUtils.isEmpty(taskData[1])) template.setWebID(Long.getLong(taskData[1]));
        if (!TextUtils.isEmpty(taskData[2])) template.setName(taskData[2]);
        if (!TextUtils.isEmpty(taskData[3])) template.setStartDate(Long.getLong(taskData[3]));
        if (!TextUtils.isEmpty(taskData[4])) template.setDueDate(Long.getLong(taskData[41]));
        if (!TextUtils.isEmpty(taskData[5])) template.setStatus(Integer.getInteger(taskData[5]));

        return template;
    }
}
