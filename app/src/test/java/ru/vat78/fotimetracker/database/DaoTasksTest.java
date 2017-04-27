package ru.vat78.fotimetracker.database;

import android.database.Cursor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.vat78.fotimetracker.model.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by vat on 26.04.17.
 */
public class DaoTasksTest {
    private static final long UID = 7;
    private static final long ID = 12;
    private static final String TITLE = "Task";

    @Mock
    private IDbConnect database;

    @Mock
    private Cursor oneCursor;
    @Mock
    private Cursor emptyCursor;
    @Mock
    private Cursor manyCursor;

    private DaoTasks dao;

    @Before
    public void prepareMocks(){
        MockitoAnnotations.initMocks(this);
        dao = new DaoTasks(database);

        when(database.insertOrUpdate(eq(DBContract.TasksTable.TABLE_NAME), any(Map.class))).thenReturn(ID).thenReturn(ID + 1);
        when(database.query(eq(DBContract.TasksTable.TABLE_NAME), any(String[].class),eq(DBContract.COLUMN_NAME_FO_ID + " = " + UID), any(String.class))).thenReturn(oneCursor);
        when(database.query(eq(DBContract.TasksTable.TABLE_NAME), any(String[].class),eq(DBContract.COLUMN_NAME_FO_ID + " = " + (UID+1)), any(String.class))).thenReturn(emptyCursor);
        when(emptyCursor.isAfterLast()).thenReturn(true);
        prepeareOneCursor();
        prepeareManyCursor();
    }

    @Test
    public void saveOne() throws Exception {
        Task test = new Task(UID, TITLE);
        long id = dao.save(test);
        assertEquals(ID, id);
        assertEquals(test.getId(), id);
        verify(database, times(1)).beginTransaction();
        verify(database, times(1)).insertOrUpdate(eq(DBContract.TasksTable.TABLE_NAME), any(Map.class));
        verify(database, times(1)).query(eq(DBContract.TasksTable.TABLE_NAME), any(String[].class),eq(DBContract.COLUMN_NAME_FO_ID + " = " + UID), any(String.class));
        verify(database, times(1)).endTransaction();
    }

    @Test
    public void testSaveLinksWithMembers() throws Exception {
        Task test = new Task(UID, TITLE);
        test.setMembersIDs("1/2/3");
        long id = dao.save(test);
        assertEquals(ID, id);
        assertEquals(test.getId(), id);
        verify(database, times(4)).insertOrUpdate(eq(DBContract.MemberObjectsTable.TABLE_NAME), any(Map.class));
    }

    @Test
    public void testSaveNewTask() throws Exception {
        Task test = new Task(UID+1, TITLE);
        long id = dao.save(test);
        assertEquals(ID, id);
        assertEquals(test.getId(), id);
        verify(database, times(1)).beginTransaction();
        verify(database, times(1)).insertOrUpdate(eq(DBContract.TasksTable.TABLE_NAME), any(Map.class));
        verify(database, times(1)).query(eq(DBContract.TasksTable.TABLE_NAME), any(String[].class),eq(DBContract.COLUMN_NAME_FO_ID + " = " + (UID+1)), any(String.class));
        verify(database, times(1)).endTransaction();
    }

    @Test
    public void saveList() throws Exception {
        List<Task> test = new ArrayList<>();
        test.add(new Task(UID, TITLE));
        test.add(new Task(UID + 1, TITLE + "1"));
        long res = dao.save(test);
        assertEquals(test.size(), res);
        assertEquals(ID, test.get(0).getId());
        verify(database, times(2)).beginTransaction();
        verify(database, times(2)).insertOrUpdate(eq(DBContract.TasksTable.TABLE_NAME), any(Map.class));
        verify(database, times(1)).query(eq(DBContract.TasksTable.TABLE_NAME), any(String[].class),eq(DBContract.COLUMN_NAME_FO_ID + " = " + UID), any(String.class));
        verify(database, times(1)).query(eq(DBContract.TasksTable.TABLE_NAME), any(String[].class),eq(DBContract.COLUMN_NAME_FO_ID + " = " + (UID+1)), any(String.class));
        verify(database, times(2)).endTransaction();
    }

    @Test
    public void getByUid() throws Exception {
        Task test = dao.getByUid(UID);
        assertEquals(ID, test.getId());
        assertEquals(UID, test.getUid());
        assertEquals(TITLE, test.getName());
        assertEquals("", test.getDesc());
        assertEquals(1111L, test.getDueDate().getTime());
        assertEquals(0, test.getStatus());

        verify(database, times(1)).beginTransaction();
        verify(database, times(1)).query(eq(DBContract.TasksTable.TABLE_NAME), any(String[].class),eq(DBContract.COLUMN_NAME_FO_ID + " = " + UID), any(String.class));
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
        when(database.query(eq(DBContract.TasksTable.TABLE_NAME), any(String[].class), eq(""), any(String.class))).thenReturn(manyCursor);
        List<Task> test = dao.load();
        assertEquals(3, test.size());
        assertEquals(ID, test.get(0).getId());
        assertEquals(UID, test.get(0).getUid());
        assertEquals(TITLE, test.get(0).getName());
        assertEquals(1111L, test.get(0).getDueDate().getTime());
        assertEquals(1, test.get(1).getStatus());

        assertEquals(ID+1, test.get(1).getId());
        assertEquals(0L, test.get(1).getUid());
        assertEquals(ID+2, test.get(2).getId());
        assertEquals(UID+1, test.get(2).getUid());

        verify(database, times(1)).beginTransaction();
        verify(database, times(1)).query(eq(DBContract.TasksTable.TABLE_NAME), any(String[].class), eq(""), any(String.class));
        verify(database, times(1)).endTransaction();
    }

    private void prepeareOneCursor() {
        when(oneCursor.moveToFirst()).thenReturn(true);
        when(oneCursor.getLong(0)).thenReturn(ID);
        when(oneCursor.getLong(1)).thenReturn(UID);
        when(oneCursor.getString(2)).thenReturn(TITLE);
        when(oneCursor.getLong(3)).thenReturn(1111L);
        when(oneCursor.getString(4)).thenReturn("");
        when(oneCursor.getInt(5)).thenReturn(0);
    }

    private void prepeareManyCursor() {
        when(manyCursor.moveToFirst()).thenReturn(true);
        when(manyCursor.getLong(0)).thenReturn(ID).thenReturn(ID+1).thenReturn(ID+2);
        when(manyCursor.getLong(1)).thenReturn(UID).thenReturn(0L).thenReturn(UID+1);
        when(manyCursor.getString(2)).thenReturn(TITLE).thenReturn("NO UID").thenReturn(TITLE + "1");
        when(manyCursor.getLong(3)).thenReturn(1111L).thenReturn(0L).thenReturn(0L);
        when(manyCursor.getInt(4)).thenReturn(0).thenReturn(1).thenReturn(0);
        when(manyCursor.moveToNext()).thenReturn(true).thenReturn(true).thenReturn(false);
    }
}