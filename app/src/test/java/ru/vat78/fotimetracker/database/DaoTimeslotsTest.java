package ru.vat78.fotimetracker.database;

import android.database.Cursor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.vat78.fotimetracker.model.Timeslot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by vat on 27.04.17.
 */
public class DaoTimeslotsTest {
    private static final long UID = 7;
    private static final long ID = 12;
    private static final String TITLE = "TimeSlot";

    @Mock
    private IDbConnect database;

    @Mock
    private Cursor oneCursor;
    @Mock
    private Cursor emptyCursor;
    @Mock
    private Cursor manyCursor;

    private DaoTimeslots dao;

    @Before
    public void prepareMocks(){
        MockitoAnnotations.initMocks(this);
        dao = new DaoTimeslots(database);

        when(database.insertOrUpdate(eq(DBContract.TimeslotsTable.TABLE_NAME), any(Map.class))).thenReturn(ID).thenReturn(ID + 1);
        when(database.query(eq(DBContract.TimeslotsTable.TABLE_NAME), any(String[].class),eq(DBContract.COLUMN_NAME_FO_ID + " = " + UID), any(String.class))).thenReturn(oneCursor);
        when(database.query(eq(DBContract.TimeslotsTable.TABLE_NAME), any(String[].class),eq(DBContract.COLUMN_NAME_FO_ID + " = " + (UID+1)), any(String.class))).thenReturn(emptyCursor);
        when(emptyCursor.isAfterLast()).thenReturn(true);
        prepeareOneCursor();
        prepeareManyCursor();
    }

    @Test
    public void save() throws Exception {
        Timeslot test = new Timeslot(UID, TITLE);
        long id = dao.save(test);
        assertEquals(ID, id);
        assertEquals(test.getId(), id);
        verify(database, times(1)).beginTransaction();
        verify(database, times(1)).insertOrUpdate(eq(DBContract.TimeslotsTable.TABLE_NAME), any(Map.class));
        verify(database, times(1)).query(eq(DBContract.TimeslotsTable.TABLE_NAME), any(String[].class),eq(DBContract.COLUMN_NAME_FO_ID + " = " + UID), any(String.class));
        verify(database, times(1)).endTransaction();
    }

    @Test
    public void testSaveLinksWithMembers() throws Exception {
        Timeslot test = new Timeslot(UID, TITLE);
        test.setMembersIDs("1/2/3");
        long id = dao.save(test);
        assertEquals(ID, id);
        assertEquals(test.getId(), id);
        verify(database, times(3)).insertOrUpdate(eq(DBContract.MemberObjectsTable.TABLE_NAME), any(Map.class));
    }

    @Test
    public void testSaveNewTS() throws Exception {
        Timeslot test = new Timeslot(UID+1, TITLE);
        long id = dao.save(test);
        assertEquals(ID, id);
        assertEquals(test.getId(), id);
        verify(database, times(1)).beginTransaction();
        verify(database, times(1)).insertOrUpdate(eq(DBContract.TimeslotsTable.TABLE_NAME), any(Map.class));
        verify(database, times(1)).query(eq(DBContract.TimeslotsTable.TABLE_NAME), any(String[].class),eq(DBContract.COLUMN_NAME_FO_ID + " = " + (UID+1)), any(String.class));
        verify(database, times(1)).endTransaction();
    }

    @Test
    public void saveList() throws Exception {
        List<Timeslot> test = new ArrayList<>();
        test.add(new Timeslot(UID, TITLE));
        test.add(new Timeslot(UID + 1, TITLE + "1"));
        long res = dao.save(test);
        assertEquals(test.size(), res);
        assertEquals(ID, test.get(0).getId());
        verify(database, times(2)).beginTransaction();
        verify(database, times(2)).insertOrUpdate(eq(DBContract.TimeslotsTable.TABLE_NAME), any(Map.class));
        verify(database, times(1)).query(eq(DBContract.TimeslotsTable.TABLE_NAME), any(String[].class),eq(DBContract.COLUMN_NAME_FO_ID + " = " + UID), any(String.class));
        verify(database, times(1)).query(eq(DBContract.TimeslotsTable.TABLE_NAME), any(String[].class),eq(DBContract.COLUMN_NAME_FO_ID + " = " + (UID+1)), any(String.class));
        verify(database, times(2)).endTransaction();
    }

    @Test
    public void getByUid() throws Exception {
        Timeslot test = dao.getByUid(UID);
        assertEquals(ID, test.getId());
        assertEquals(UID, test.getUid());
        assertEquals(TITLE, test.getName());
        assertEquals("", test.getDesc());
        assertEquals(11111L, test.getStart().getTime());
        assertEquals(360L, test.getDuration());
        assertEquals(222222L, test.getChanged().getTime());
        assertEquals("Me", test.getAuthor());
        assertEquals(1L, test.getTaskId());
        assertEquals("", test.getMembersIds());

        verify(database, times(1)).beginTransaction();
        verify(database, times(1)).query(eq(DBContract.TimeslotsTable.TABLE_NAME), any(String[].class),eq(DBContract.COLUMN_NAME_FO_ID + " = " + UID), any(String.class));
        verify(database, times(1)).endTransaction();
    }

    @Test
    public void isExistInDB() throws Exception {
        assertTrue(dao.isExistInDB(UID));
        assertFalse(dao.isExistInDB(UID+1));

        verify(database, times(2)).beginTransaction();
        verify(database, times(2)).endTransaction();
    }

    @Test
    public void load() throws Exception {
        when(database.query(eq(DBContract.TimeslotsTable.TABLE_NAME), any(String[].class), eq(""), any(String.class))).thenReturn(manyCursor);
        List<Timeslot> test = dao.load();
        assertEquals(ID+2, test.get(2).getId());
        assertEquals(UID+1, test.get(2).getUid());
        assertEquals(TITLE + "1", test.get(2).getName());
        assertEquals("", test.get(2).getDesc());
        assertEquals(0L, test.get(2).getStart().getTime());
        assertEquals(600L, test.get(2).getDuration());
        assertEquals(33333L, test.get(2).getChanged().getTime());
        assertEquals("Somebody", test.get(2).getAuthor());
        assertEquals(10L, test.get(2).getTaskId());
        assertEquals("1/2/3", test.get(2).getMembersIds());

        assertEquals(ID+1, test.get(1).getId());
        assertEquals(0L, test.get(1).getUid());
        assertEquals(ID, test.get(0).getId());
        assertEquals(UID, test.get(0).getUid());

        verify(database, times(1)).beginTransaction();
        verify(database, times(1)).query(eq(DBContract.TimeslotsTable.TABLE_NAME), any(String[].class), eq(""), any(String.class));
        verify(database, times(1)).endTransaction();
    }

    private void prepeareOneCursor() {
        when(oneCursor.moveToFirst()).thenReturn(true);
        when(oneCursor.getLong(0)).thenReturn(ID);
        when(oneCursor.getLong(1)).thenReturn(UID);
        when(oneCursor.getString(2)).thenReturn(TITLE);
        when(oneCursor.getLong(3)).thenReturn(11111L);
        when(oneCursor.getLong(4)).thenReturn(360L);
        when(oneCursor.getLong(5)).thenReturn(222222L);
        when(oneCursor.getString(6)).thenReturn("Me");
        when(oneCursor.getLong(7)).thenReturn(1L);
        when(oneCursor.getString(8)).thenReturn("");
        when(oneCursor.getString(9)).thenReturn("");
    }

    private void prepeareManyCursor() {
        when(manyCursor.moveToFirst()).thenReturn(true);
        when(manyCursor.getLong(0)).thenReturn(ID).thenReturn(ID+1).thenReturn(ID+2);
        when(manyCursor.getLong(1)).thenReturn(UID).thenReturn(0L).thenReturn(UID+1);
        when(manyCursor.getString(2)).thenReturn(TITLE).thenReturn("NO UID").thenReturn(TITLE + "1");
        when(manyCursor.getLong(3)).thenReturn(1111L).thenReturn(0L).thenReturn(0L);
        when(manyCursor.getLong(4)).thenReturn(360L).thenReturn(60L).thenReturn(600L);
        when(manyCursor.getLong(5)).thenReturn(222222L).thenReturn(0L).thenReturn(33333L);
        when(manyCursor.getString(6)).thenReturn("Me").thenReturn("").thenReturn("Somebody");
        when(manyCursor.getLong(7)).thenReturn(1L).thenReturn(0L).thenReturn(10L);
        when(manyCursor.getString(8)).thenReturn("");
        when(manyCursor.getString(9)).thenReturn("1/2/3");
        when(manyCursor.moveToNext()).thenReturn(true).thenReturn(true).thenReturn(false);
    }
}