package ru.vat78.fotimetracker.fengoffice.vatApi;

import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.IErrorsHandler;
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

    private ApiConnector connector;
    private IErrorsHandler errorsHandler;

    public VatApi(ApiConnector connector, IErrorsHandler errorsHandler) {
        this.connector = connector;
        this.errorsHandler = errorsHandler;

        members = new ApiMembers(connector, errorsHandler);
        tasks = new ApiTasks(connector, errorsHandler);
        timeslots = new ApiTimeslots(connector, errorsHandler);
    }

    @Override
    public String getFengOfficeVersion() {
        return null;
    }

    @Override
    public boolean checkPlugin(String plugin_name) {
        errorsHandler.resetError();
        return connector.checkPlugin(plugin_name);
    }

    @Override
    public List<Member> loadMembers() {
        errorsHandler.resetError();
        return members.load();
    }

    @Override
    public List<Task> loadTasks() {
        errorsHandler.resetError();
        return tasks.load();
    }

    @Override
    public List<Task> loadTasks(Date timestamp) {
        errorsHandler.resetError();
        return tasks.load(timestamp);
    }

    @Override
    public long saveTask(Task task) {
        errorsHandler.resetError();
        return tasks.save(task);
    }

    @Override
    public boolean deleteTask(Task task) {
        errorsHandler.resetError();
        return tasks.delete(task);
    }

    @Override
    public List<Timeslot> loadTimeslots() {
        errorsHandler.resetError();
        return timeslots.load();
    }

    @Override
    public List<Timeslot> loadTimeslots(Date timestamp) {
        errorsHandler.resetError();
        return timeslots.load(timestamp);
    }

    @Override
    public long saveTimeslot(Timeslot ts) {
        errorsHandler.resetError();
        return timeslots.save(ts);
    }

    @Override
    public boolean deleteTimeslot(Timeslot ts) {
        errorsHandler.resetError();
        return timeslots.delete(ts);
    }
}
