package ru.vat78.fotimetracker.fengoffice;

import ru.vat78.fotimetracker.model.Member;
import ru.vat78.fotimetracker.model.Task;
import ru.vat78.fotimetracker.model.Timeslot;

import java.util.Date;
import java.util.List;

/**
 * Created by vat on 16.04.17.
 */
public interface IFengOfficeService {

    boolean checkAndSetUrl(String url, boolean useUntrustCert);
    boolean checkAndSetLogin(String login);
    boolean checkAndSetPassword(String password);

    boolean testConnection();

    String getFengOfficeVersion();
    boolean checkPlugin(String plugin_name);

    List<Member> loadMembers();

    List<Task> loadTasks();
    List<Task> loadTasks(Date timestamp);
    long saveTask(Task task);
    boolean deleteTask(Task task);

    List<Timeslot> loadTimeslots();
    List<Timeslot> loadTimeslots(Date timestamp);
    long saveTimeslot(Timeslot ts);
    boolean deleteTimeslot(Timeslot ts);
}
