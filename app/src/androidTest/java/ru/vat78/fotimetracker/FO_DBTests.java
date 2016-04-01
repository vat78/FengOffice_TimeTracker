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

        assertTrue("Save members", mDb.saveObjects(members));

        ArrayList<FOTT_DrawingMember> membersAfter1 = (ArrayList<FOTT_DrawingMember>) mDb.loadObjects();
        assertEquals("Load members after first saving", members.size(), membersAfter1.size());

        FOTT_MemberBuilder mb = new FOTT_MemberBuilder(members.get(0));
        mb.setChanged(now.getTime() + 10000);
        mb.setWebID(0);

        long tempId = mDb.saveObject(new FOTT_DrawingMember(mb));
        assertFalse("Save one member", tempId == 0);

        ArrayList<FOTT_DrawingMember> membersAfter2 = (ArrayList<FOTT_DrawingMember>) mDb.loadObjects();
        assertEquals("Load members after adding member", members.size() + 1, membersAfter2.size());

        ArrayList<FOTT_DrawingMember> membersAfter3 = (ArrayList<FOTT_DrawingMember>) mDb.loadChangedObjects(now);
        assertEquals("Load changed members", 1, membersAfter3.size());

        mb.setDbID(tempId);
        assertTrue("Delete member", mDb.deleteObject(new FOTT_DrawingMember(mb)));

        ArrayList<FOTT_DrawingMember> membersAfter4 = (ArrayList<FOTT_DrawingMember>) mDb.loadObjects();
        assertEquals("Load members after delete one member", members.size(), membersAfter4.size());
    }

    public void testTasksDB() throws Exception {

        Date now = new Date(System.currentTimeMillis());

        FOTT_DBTasks tDb = new FOTT_DBTasks(db);

        ArrayList<FOTT_Task> tasks = generateTasks();

        tDb.rebuild();

        assertTrue("Save tasks", tDb.saveObjects(tasks));

        ArrayList<FOTT_Task> tasksAfter1 = (ArrayList<FOTT_Task>) tDb.loadObjects();
        assertEquals("Load tasks after first saving", tasks.size(), tasksAfter1.size());

        FOTT_TaskBuilder tb = new FOTT_TaskBuilder(tasks.get(0));
        tb.setChanged(now.getTime() + 10000);
        tb.setWebID(0);

        long tempId = tDb.saveObject(new FOTT_Task(tb));
        assertFalse("Save one task", tempId == 0);

        ArrayList<FOTT_Task> tasksAfter2 = (ArrayList<FOTT_Task>) tDb.loadObjects();
        assertEquals("Load tasks after adding task", tasks.size() + 1, tasksAfter2.size());

        tb.setDeleted(true);
        tb.setDbID(tempId);
        assertEquals("Change task", tempId, tDb.saveObject(new FOTT_Task(tb)));

        ArrayList<FOTT_Task> tasksAfter3 = (ArrayList<FOTT_Task>) tDb.loadObjects();
        assertEquals("Load tasks after changing task", tasks.size() + 1, tasksAfter3.size());

        ArrayList<FOTT_Task> tasksAfter4 = (ArrayList<FOTT_Task>) tDb.loadChangedObjects(now);
        assertEquals("Load changed tasks", 1, tasksAfter4.size());

        ArrayList<FOTT_Task> tasksAfter5 = (ArrayList<FOTT_Task>) tDb.getObjectsMarkedAsDeleted();
        assertEquals("Load deleted tasks", 1, tasksAfter5.size());

        assertTrue("Delete tasks", tDb.deleteObjects(tasksAfter1));
        ArrayList<FOTT_Task> tasksAfter6 = (ArrayList<FOTT_Task>) tDb.loadObjects();
        assertEquals("Load tasks after deleting tasks", 1, tasksAfter6.size());

        tb.setDbID(tempId);
        assertTrue("Delete one task", tDb.deleteObject(new FOTT_Task(tb)));

        ArrayList<FOTT_Task> tasksAfter7 = (ArrayList<FOTT_Task>) tDb.loadObjects();
        assertEquals("Load tasks after deleting last task", 0, tasksAfter7.size());
    }

    public void testTimeslotsDB() throws Exception {

        Date now = new Date(System.currentTimeMillis());

        FOTT_DBTimeslots tDb = new FOTT_DBTimeslots(db);

        ArrayList<FOTT_Timeslot> timeslots = generateTimeslots();

        tDb.rebuild();

        assertTrue("Save timeslots", tDb.saveObjects(timeslots));

        ArrayList<FOTT_Timeslot> tsAfter1 = (ArrayList<FOTT_Timeslot>) tDb.loadObjects();
        assertEquals("Load timeslots after first save", timeslots.size(), tsAfter1.size());

        FOTT_TimeslotBuilder tb = new FOTT_TimeslotBuilder(timeslots.get(0));
        tb.setChanged(now.getTime() + 10000);
        tb.setWebID(0);

        long tempId = tDb.saveObject(new FOTT_Timeslot(tb));
        assertFalse("Save one timeslot", tempId == 0);

        ArrayList<FOTT_Timeslot> tsAfter2 = (ArrayList<FOTT_Timeslot>) tDb.loadObjects();
        assertEquals("Load timeslots after adding timeslot", timeslots.size() + 1, tsAfter2.size());

        tb.setDeleted(true);
        tb.setDbID(tempId);
        assertEquals("Change timeslot", tempId, tDb.saveObject(new FOTT_Timeslot(tb)));

        ArrayList<FOTT_Timeslot> tsAfter3 = (ArrayList<FOTT_Timeslot>) tDb.loadObjects();
        assertEquals("Load timeslots after changing timeslot", timeslots.size() + 1, tsAfter3.size());

        ArrayList<FOTT_Timeslot> tsAfter4 = (ArrayList<FOTT_Timeslot>) tDb.loadChangedObjects(now);
        assertEquals("Load changed timeslots", 1, tsAfter4.size());

        ArrayList<FOTT_Timeslot> tsAfter5 = (ArrayList<FOTT_Timeslot>) tDb.getObjectsMarkedAsDeleted();
        assertEquals("Load deleted timeslots", 1, tsAfter5.size());

        assertTrue("Delete timeslots", tDb.deleteObjects(tsAfter1));
        ArrayList<FOTT_Timeslot> tsAfter6 = (ArrayList<FOTT_Timeslot>) tDb.loadObjects();
        assertEquals("Load timeslots after deleting timeslots", 1, tsAfter6.size());

        tb.setDbID(tempId);
        assertTrue("Delete one timeslot", tDb.deleteObject(new FOTT_Timeslot(tb)));
        ArrayList<FOTT_Timeslot> tsAfter7 = (ArrayList<FOTT_Timeslot>) tDb.loadObjects();
        assertEquals("Load timeslots after deleting last timeslot", 0, tsAfter7.size());

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
