package ru.vat78.fotimetracker;

import android.app.Application;
import android.os.StrictMode;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;
import android.test.LoaderTestCase;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Connector;
import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Members;
import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Tasks;
import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Timeslots;
import ru.vat78.fotimetracker.model.FOTT_Member;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.model.FOTT_TaskBuilder;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;
import ru.vat78.fotimetracker.model.FOTT_TimeslotBuilder;


public class FOAPITests extends TestCase {

    private static long beginOfYear;
    private static long endOfYear;

    private static FOTT_App app;
    private static FOAPI_Connector webService;

    private static FOTT_Task testTask;
    private static FOTT_Timeslot testTS;

    public FOAPITests()  {
        super();
        enableStrictMode();
    }

    @Override
    public void setUp()throws Exception {
        super.setUp();

        setupDates();

        createTestTask();
        createTestTimeslot();

        //enableStrictMode();

        webService = FOAPI_Connector.getInstance(SecretCredentials.getUrl(),
                SecretCredentials.getUser(),
                SecretCredentials.getPwd(),
                false);
    }


    public void testMemberLoad()throws Exception {

        FOAPI_Members apiMembers = FOAPI_Members.getInstance(webService);
        ArrayList<FOTT_Member> members = apiMembers.loadObjects();
        assertEquals("Members load ", true, members.size() > 0);
    }

    public void testTasksLoad() throws Exception {

        FOAPI_Tasks apiTasks = FOAPI_Tasks.getInstance(webService);
        ArrayList<FOTT_Task> tasks = apiTasks.loadChangedObjects(new Date(beginOfYear));
        assertEquals("Tasks load after 01/01/2016", true, tasks.size() > 0);

        tasks = apiTasks.loadChangedObjects(new Date(endOfYear));
        assertEquals("Tasks load after 01/01/2017", true, tasks.size() == 0);
    }

    public void testSaveLoadChangeDeleteTask() throws Exception {

        FOAPI_Tasks apiTasks = FOAPI_Tasks.getInstance(webService);
        FOTT_TaskBuilder t = new FOTT_TaskBuilder(testTask);

        long id = apiTasks.saveObject(t.buildObject());
        assertEquals("Task creation", false, id == 0);
        if (id == 0) return;

        t.setWebID(id);
        t.setDueDate(System.currentTimeMillis());
        long newId = apiTasks.saveObject(t.buildObject());
        assertEquals("Task change", true, id == newId);

        t.setStatus(FOTT_Task.TaskStatus.COMPLETED);
        newId = apiTasks.saveObject(t.buildObject());
        assertEquals("Task complete", true, id == newId);

        //FOTT_Task t = apiTasks.loadObject(id);
        //assertEquals("Read created task from web", true, t.getWebId() == id);
        //assertEquals("Read right task name", true, t.getName().equals(testTask.getName()));

        assertEquals("Delete task", true, apiTasks.deleteObject(t.buildObject()));
    }

    public void testTimeslotsLoad() throws Exception {

        FOAPI_Timeslots apiTS = FOAPI_Timeslots.getInstance(webService);
        ArrayList<FOTT_Timeslot> timeslots = apiTS.loadChangedObjects(new Date(beginOfYear));
        assertEquals("Timeslots load after 01/01/2016", true, timeslots.size() > 0);

        timeslots = apiTS.loadChangedObjects(new Date(endOfYear));
        assertEquals("Timeslots load after 01/01/2017", true, timeslots.size() == 0);
    }

    public void testSaveChangeDeleteTSinMember() throws Exception {

        FOAPI_Timeslots apiTS = FOAPI_Timeslots.getInstance(webService);

        long id = apiTS.saveObject(testTS);
        assertEquals("Timeslot for member creation", true, id != 0);
        if (id == 0) return;

        FOTT_TimeslotBuilder ts = new FOTT_TimeslotBuilder(testTS);
        ts.setWebID(id);
        ts.setDuration(30*60*1000).setStart(System.currentTimeMillis());
        long newId = apiTS.saveObject(ts.buildObject());
        assertEquals("Timeslot for member save changes", true, newId == id);

        assertEquals("Delete timeslot for member", true, apiTS.deleteObject(ts.buildObject()));
    }


    public void testSaveChangeDeleteTSinTask() throws Exception {

        FOAPI_Tasks apiTasks = FOAPI_Tasks.getInstance(webService);
        FOAPI_Timeslots apiTS = FOAPI_Timeslots.getInstance(webService);

        long taskId = apiTasks.saveObject(testTask);
        if (taskId ==0) return;

        FOTT_TimeslotBuilder ts = new FOTT_TimeslotBuilder(testTS);
        ts.setTaskWebId(taskId);
        long id = apiTS.saveObject(ts.setTaskWebId(taskId).buildObject());
        assertEquals("Timeslot for task creation", true, id != 0);

        if (id != 0) {
            ts.setWebID(id);
            ts.setDuration(30*60*1000).setStart(System.currentTimeMillis());
            long newId = apiTS.saveObject(ts.buildObject());
            assertEquals("Timeslot for task save changes", true, newId == id);

            assertEquals("Delete timeslot for task", true, apiTS.deleteObject(ts.buildObject()));
        }

        apiTasks.deleteObject(new FOTT_TaskBuilder(testTask).setWebID(taskId).buildObject());
    }

    private void enableStrictMode()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    private void setupDates(){
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        now.set(Calendar.MONTH, 1);
        now.set(Calendar.DAY_OF_MONTH,1);
        beginOfYear = now.getTimeInMillis();
        now.set(Calendar.YEAR,now.get(Calendar.YEAR)+1);
        endOfYear = now.getTimeInMillis();
    }

    private void createTestTask(){
        FOTT_TaskBuilder t = new FOTT_TaskBuilder();
        t.setName("This is test task");
        t.setDesc("Test description.");
        t.setStartDate(beginOfYear);
        t.setDueDate(endOfYear);
        t.setCanAddTimeslots(true);
        t.setStatus(FOTT_Task.TaskStatus.ACTIVE);
        t.setChanged(System.currentTimeMillis());
        t.setMembersWebIds(new String[]{"700"});

        testTask = t.buildObject();
    }

    private void createTestTimeslot(){
        FOTT_TimeslotBuilder ts = new FOTT_TimeslotBuilder();
        ts.setName("test time");
        ts.setDesc("test time description");
        ts.setDuration(15 * 60 * 1000);
        ts.setStart(System.currentTimeMillis());
        ts.setMembersWebIds(new String[]{"700"});

        testTS = ts.buildObject();
    }


}
