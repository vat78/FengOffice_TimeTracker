package ru.vat78.fotimetracker.fengoffice.vatApi;

import ru.vat78.fotimetracker.IErrorsHandler;
import ru.vat78.fotimetracker.fengoffice.IFengOfficeService;
import ru.vat78.fotimetracker.model.Member;
import ru.vat78.fotimetracker.model.Task;
import ru.vat78.fotimetracker.model.Timeslot;

import java.util.Date;
import java.util.List;

/**
 * Created by vat on 16.04.17.
 */
public class VatApi implements IFengOfficeService {

    private IErrorsHandler errorsHandler;

    private final ApiMembers members;
    private final ApiTasks tasks;
    private final ApiTimeslots timeslots;

    private ApiConnector connector;

    public VatApi(IErrorsHandler errorsHandler) {
        this.connector = new ApiConnector(errorsHandler);
        this.errorsHandler = errorsHandler;

        members = new ApiMembers(connector, errorsHandler);
        tasks = new ApiTasks(connector, errorsHandler);
        timeslots = new ApiTimeslots(connector, errorsHandler);
    }

    @Override
    public boolean checkAndSetUrl(String url, boolean useUntrustCert) {
        boolean res = false;
        if (url != null) {
            url = url.trim();
        }
        else {
            url = "";
        }

        if (url.length() > 3) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            if (!url.endsWith("/")) {
                url += "/";
            }

            connector.setUrl(url);
            connector.canUseUntrustCert(useUntrustCert);
            res = true;
        }

        return res;
    }

    @Override
    public boolean checkAndSetLogin(String login) {
        boolean res = false;

        if (login != null) {
            login = login.trim();
            if (login.length() > 2) {
                connector.setLogin(login);
                res = true;
            }
        }
        return res;
    }

    @Override
    public boolean checkAndSetPassword(String password) {
        boolean res = false;

        if (password != null) {
            connector.setPassword(password);
            res = true;
        }
        return res;
    }

    @Override
    public boolean testConnection() {
        return connector.testConnection();
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
