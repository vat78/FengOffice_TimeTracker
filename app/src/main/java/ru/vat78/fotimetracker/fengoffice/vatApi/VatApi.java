package ru.vat78.fotimetracker.fengoffice.vatApi;

import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.fengoffice.FengOfficeApi;
import ru.vat78.fotimetracker.model.Member;
import ru.vat78.fotimetracker.model.Task;
import ru.vat78.fotimetracker.model.Timeslot;

import java.util.Date;
import java.util.List;

/**
 * Created by vat on 16.04.17.
 */
public class VatApi implements FengOfficeApi {

    private final ApiMembers members;
    private final ApiTasks tasks;
    private final ApiTimeslots timeslots;

    private final App application;

    public VatApi(App app) {
        this.application = app;

        members = new ApiMembers();
        tasks = new ApiTasks();
        timeslots = new ApiTimeslots();
    }

    @Override
    public String getFengOfficeVersion() {
        return null;
    }

    @Override
    public boolean checkPlugin(String plugin_name) {
        return application.getWebService().checkPlugin(plugin_name);
    }

    @Override
    public List<Member> loadMembers() {
        return members.load(application);
    }

    @Override
    public List<Task> loadTasks() {
        return tasks.load(application);
    }

    @Override
    public List<Task> loadTasks(Date timestamp) {
        return tasks.load(application, timestamp);
    }

    @Override
    public long saveTask(Task task) {
        return tasks.save(application, task);
    }

    @Override
    public boolean deleteTask(Task task) {
        return tasks.delete(application, task);
    }

    @Override
    public List<Timeslot> loadTimeslots() {
        return timeslots.load(application);
    }

    @Override
    public List<Timeslot> loadTimeslots(Date timestamp) {
        return timeslots.load(application, timestamp);
    }

    @Override
    public long saveTimeslot(Timeslot ts) {
        return timeslots.save(application, ts);
    }

    @Override
    public boolean deleteTimeslot(Timeslot ts) {
        return timeslots.delete(application, ts);
    }
}
