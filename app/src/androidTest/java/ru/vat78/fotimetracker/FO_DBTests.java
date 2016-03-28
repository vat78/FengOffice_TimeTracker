package ru.vat78.fotimetracker;

import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Date;

import ru.vat78.fotimetracker.adapters.FOTT_DrawingMember;
import ru.vat78.fotimetracker.connectors.database.FOTT_DBHelper;
import ru.vat78.fotimetracker.connectors.database.FOTT_DBMembers;
import ru.vat78.fotimetracker.connectors.database.FOTT_DBTasks;
import ru.vat78.fotimetracker.connectors.database.FOTT_DBTimeslots;
import ru.vat78.fotimetracker.model.FOTT_MemberBuilder;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.model.FOTT_TaskBuilder;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;
import ru.vat78.fotimetracker.model.FOTT_TimeslotBuilder;


public class FO_DBTests extends AndroidTestCase {

    private static SQLiteDatabase db;


    public FO_DBTests() {
        super();
        enableStrictMode();
    }

    @Override
    public void setUp()throws Exception {
        super.setUp();

        db = new FOTT_DBHelper(getContext()).getWritableDatabase();
    }

    public void testMembersDB() throws Exception {

        Date now = new Date(System.currentTimeMillis());

        FOTT_DBMembers mDb = new FOTT_DBMembers(db);

        ArrayList<FOTT_DrawingMember> members = generateMembers();

        mDb.rebuild();

        assertEquals("Save members", true, mDb.saveObjects(members));

        ArrayList<FOTT_DrawingMember> membersAfter1 = (ArrayList<FOTT_DrawingMember>) mDb.loadObjects();
        assertEquals("Load members after first saving", true, membersAfter1.size() == members.size());

        FOTT_MemberBuilder mb = new FOTT_MemberBuilder(members.get(0));
        mb.setChanged(now.getTime() + 10000);
        mb.setWebID(0);

        long tempId = mDb.saveObject(new FOTT_DrawingMember(mb));
        assertEquals("Save one member",  tempId != 0, true);

        ArrayList<FOTT_DrawingMember> membersAfter2 = (ArrayList<FOTT_DrawingMember>) mDb.loadObjects();
        assertEquals("Load members after adding member", true, membersAfter2.size() == members.size() + 1);

        ArrayList<FOTT_DrawingMember> membersAfter3 = (ArrayList<FOTT_DrawingMember>) mDb.loadChangedObjects(now);
        assertEquals("Load changed members", true, membersAfter3.size() == 1);

        mb.setDbID(tempId);
        assertEquals("Delete member", true, mDb.deleteObject(new FOTT_DrawingMember(mb)));

        ArrayList<FOTT_DrawingMember> membersAfter4 = (ArrayList<FOTT_DrawingMember>) mDb.loadObjects();
        assertEquals("Load members after delete one member", true, membersAfter4.size() == members.size());
    }

    public void testTasksDB() throws Exception {

        Date now = new Date(System.currentTimeMillis());

        FOTT_DBTasks tDb = new FOTT_DBTasks(db);

        ArrayList<FOTT_Task> tasks = generateTasks();

        tDb.rebuild();

        assertEquals("Save tasks", true, tDb.saveObjects(tasks));

        ArrayList<FOTT_Task> tasksAfter1 = (ArrayList<FOTT_Task>) tDb.loadObjects();
        assertEquals("Load tasks after first saving", true, tasksAfter1.size() == tasks.size());

        FOTT_TaskBuilder tb = new FOTT_TaskBuilder(tasks.get(0));
        tb.setChanged(now.getTime() + 10000);
        tb.setWebID(0);

        long tempId = tDb.saveObject(new FOTT_Task(tb));
        assertEquals("Save one task", tempId != 0, true);

        ArrayList<FOTT_Task> tasksAfter2 = (ArrayList<FOTT_Task>) tDb.loadObjects();
        assertEquals("Load tasks after adding task", true, tasksAfter2.size() == tasks.size() + 1);

        tb.setDeleted(true);
        tb.setDbID(tempId);
        assertEquals("Change task", true, tDb.saveObject(new FOTT_Task(tb)) == tempId);

        ArrayList<FOTT_Task> tasksAfter3 = (ArrayList<FOTT_Task>) tDb.loadObjects();
        assertEquals("Load tasks after changing task", true, tasksAfter3.size() == tasks.size() + 1);

        ArrayList<FOTT_Task> tasksAfter4 = (ArrayList<FOTT_Task>) tDb.loadChangedObjects(now);
        assertEquals("Load changed tasks", true, tasksAfter4.size() == 1);

        ArrayList<FOTT_Task> tasksAfter5 = (ArrayList<FOTT_Task>) tDb.getObjectsMarkedAsDeleted();
        assertEquals("Load deleted tasks", true, tasksAfter5.size() == 1);

        assertEquals("Delete tasks", true, tDb.deleteObjects(tasksAfter1));
        ArrayList<FOTT_Task> tasksAfter6 = (ArrayList<FOTT_Task>) tDb.loadObjects();
        assertEquals("Load tasks after deleting tasks", true, tasksAfter6.size() == 1);

        tb.setDbID(tempId);
        assertEquals("Delete one task", true, tDb.deleteObject(new FOTT_Task(tb)));

        ArrayList<FOTT_Task> tasksAfter7 = (ArrayList<FOTT_Task>) tDb.loadObjects();
        assertEquals("Load tasks after deleting last task", true, tasksAfter7.size() == 0);
    }

    public void testTimeslotsDB() throws Exception {

        Date now = new Date(System.currentTimeMillis());

        FOTT_DBTimeslots tDb = new FOTT_DBTimeslots(db);

        ArrayList<FOTT_Timeslot> timeslots = generateTimeslots();

        tDb.rebuild();

        assertEquals("Save timeslots", true, tDb.saveObjects(timeslots));

        ArrayList<FOTT_Timeslot> tsAfter1 = (ArrayList<FOTT_Timeslot>) tDb.loadObjects();
        assertEquals("Load timeslots after first save", true, tsAfter1.size() == timeslots.size());

        FOTT_TimeslotBuilder tb = new FOTT_TimeslotBuilder(timeslots.get(0));
        tb.setChanged(now.getTime() + 10000);
        tb.setWebID(0);

        long tempId = tDb.saveObject(new FOTT_Timeslot(tb));
        assertEquals("Save one timeslot", tempId != 0, true);

        ArrayList<FOTT_Timeslot> tsAfter2 = (ArrayList<FOTT_Timeslot>) tDb.loadObjects();
        assertEquals("Load timeslots after adding timeslot", true, tsAfter2.size() == timeslots.size() + 1);

        tb.setDeleted(true);
        tb.setDbID(tempId);
        assertEquals("Change timeslot", true, tDb.saveObject(new FOTT_Timeslot(tb)) == tempId);

        ArrayList<FOTT_Timeslot> tsAfter3 = (ArrayList<FOTT_Timeslot>) tDb.loadObjects();
        assertEquals("Load timeslots after changing timeslot", true, tsAfter3.size() == timeslots.size() + 1);

        ArrayList<FOTT_Timeslot> tsAfter4 = (ArrayList<FOTT_Timeslot>) tDb.loadChangedObjects(now);
        assertEquals("Load changed timeslots", true, tsAfter4.size() == 1);

        ArrayList<FOTT_Timeslot> tsAfter5 = (ArrayList<FOTT_Timeslot>) tDb.getObjectsMarkedAsDeleted();
        assertEquals("Load deleted timeslots", true, tsAfter5.size() == 1);

        assertEquals("Delete timeslots", true, tDb.deleteObjects(tsAfter1));
        ArrayList<FOTT_Timeslot> tsAfter6 = (ArrayList<FOTT_Timeslot>) tDb.loadObjects();
        assertEquals("Load timeslots after deleting timeslots", true, tsAfter6.size() == 1);

        tb.setDbID(tempId);
        assertEquals("Delete one timeslot", true, tDb.deleteObject(new FOTT_Timeslot(tb)));
        ArrayList<FOTT_Timeslot> tsAfter7 = (ArrayList<FOTT_Timeslot>) tDb.loadObjects();
        assertEquals("Load timeslots after deleting last timeslot", true, tsAfter7.size() == 0);

    }


    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        db.close();
    }

    private void enableStrictMode()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    private FOTT_DrawingMember generateMember(int id) {

        FOTT_MemberBuilder m = new FOTT_MemberBuilder();
        m.setWebID(id);
        m.setName("ID-" + id);
        m.setColor(id * 4);
        m.setPath("ID-" + id);

        return new FOTT_DrawingMember(m);
    }

    private ArrayList<FOTT_DrawingMember> generateMembers() {

        ArrayList<FOTT_DrawingMember> result = new ArrayList<>();
        for (int i=1; i<=5; i++)
            result.add(generateMember(i));

        return result;
    }


    private FOTT_Task generateTask(int id) {

        FOTT_TaskBuilder t = new FOTT_TaskBuilder();
        t.setWebID(id);
        t.setName("ID-" + id);
        t.setMembersWebIds(new String[]{"0", "2", "3"});

        return new FOTT_Task(t);
    }

    private ArrayList<FOTT_Task> generateTasks() {

        ArrayList<FOTT_Task> result = new ArrayList<>();
        for (int i=1; i<=5; i++)
            result.add(generateTask(i));

        return result;
    }

    private FOTT_Timeslot generateTimeslot(int id) {

        FOTT_TimeslotBuilder ts = new FOTT_TimeslotBuilder();
        ts.setWebID(id);
        ts.setName("ID-" + id);
        ts.setStart(System.currentTimeMillis() - id * 30 * 60 * 1000);
        ts.setDuration(15 * 60 * 1000);
        ts.setMembersWebIds(new String[] {"0","2"});

        return new FOTT_Timeslot(ts);
    }

    private ArrayList<FOTT_Timeslot> generateTimeslots() {

        ArrayList<FOTT_Timeslot> result = new ArrayList<>();
        for (int i=1; i<=5; i++)
            result.add(generateTimeslot(i));

        return result;
    }
}
