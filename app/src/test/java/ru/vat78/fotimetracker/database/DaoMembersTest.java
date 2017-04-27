package ru.vat78.fotimetracker.database;

import android.content.ContentValues;
import android.database.Cursor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.model.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by vat on 25.04.17.
 */
public class DaoMembersTest {
    private static final long UID = 15;
    private static final long ID = 10;
    private static final String TITLE = "Test";
    private static final String PATH = "Test/Path";

    @Mock
    private IDbConnect database;

    @Mock
    private Cursor oneCursor;
    @Mock
    private Cursor emptyCursor;
    @Mock
    private Cursor manyCursor;

    private DaoMembers dao;

    @Before
    public void prepareMocks(){
        MockitoAnnotations.initMocks(this);
        dao = new DaoMembers(database);

        when(database.insertOrUpdate(eq(DBContract.MembersTable.TABLE_NAME), any(Map.class))).thenReturn(ID);
        when(emptyCursor.isAfterLast()).thenReturn(true);
        prepeareOneCursor();
        prepeareManyCursor();
    }

    @Test
    public void testSaveOne() throws Exception {
        Member test = new Member(UID, TITLE);
        long id = dao.save(test);
        assertEquals(ID, id);
        assertEquals(test.getId(), id);
        verify(database, times(1)).beginTransaction();
        verify(database, times(1)).insertOrUpdate(eq(DBContract.MembersTable.TABLE_NAME), any(Map.class));
        verify(database, times(1)).endTransaction();
    }

    @Test
    public void testSaveList() throws Exception {
        List<Member> test = new ArrayList<>();
        test.add(new Member(UID, TITLE));
        test.add(new Member(UID + 1, TITLE + "1"));
        long res = dao.save(test);
        assertEquals(test.size(), res);
        assertEquals(ID, test.get(0).getId());
        verify(database, times(2)).beginTransaction();
        verify(database, times(2)).insertOrUpdate(eq(DBContract.MembersTable.TABLE_NAME), any(Map.class));
        verify(database, times(2)).endTransaction();
    }

    @Test
    public void testGetById() throws Exception {
        when(database.query(any(String.class), any(String[].class), eq(" " + DBContract.COLUMN_NAME_FO_ID + " = " + UID), any(String.class))).thenReturn(oneCursor);
        Member test = dao.getByUid(UID);
        assertEquals(ID, test.getId());
        assertEquals(UID, test.getUid());
        assertEquals(TITLE, test.getName());
        assertEquals(PATH, test.getPath());

        verify(database, times(1)).beginTransaction();
        verify(database, times(1)).query(any(String.class), any(String[].class), eq(" " + DBContract.COLUMN_NAME_FO_ID + " = " + UID), any(String.class));
        verify(database, times(1)).endTransaction();
    }

    @Test
    public void testIsExistInDb() throws Exception {
        when(database.query(any(String.class), any(String[].class), eq(" " + DBContract.COLUMN_NAME_FO_ID + " = " + UID), any(String.class))).thenReturn(oneCursor);
        when(database.query(any(String.class), any(String[].class), eq(" " + DBContract.COLUMN_NAME_FO_ID + " = " + (UID+1)), any(String.class))).thenReturn(emptyCursor);
        assertTrue(dao.isExistInDB(UID));
        assertFalse(dao.isExistInDB(UID+1));

        verify(database, times(2)).beginTransaction();
        verify(database, times(2)).endTransaction();
    }

    @Test
    public void testLoad() throws Exception {
        when(database.query(any(String.class), any(String[].class), isNull(String.class), any(String.class))).thenReturn(manyCursor);
        List<Member> test = dao.load();
        assertEquals(3, test.size());

        verify(database, times(1)).beginTransaction();
        verify(database, times(1)).query(any(String.class), any(String[].class), isNull(String.class), any(String.class));
        verify(database, times(1)).endTransaction();
    }

    @Test
    public void testMemberAnyMustBeFirst() throws Exception {
        when(database.query(any(String.class), any(String[].class), isNull(String.class), any(String.class))).thenReturn(manyCursor);
        List<Member> test = dao.load();

        assertEquals(ID,test.get(1).getId());
        assertEquals(ID+1,test.get(0).getId());
        assertEquals(ID+2,test.get(2).getId());

        assertEquals(UID,test.get(1).getUid());
        assertEquals(-1,test.get(0).getUid());
        assertEquals(UID+1,test.get(2).getUid());

        assertEquals(TITLE,test.get(1).getName());
        assertEquals(TITLE +"1",test.get(2).getName());
        assertEquals(PATH,test.get(1).getPath());
    }

    @Test
    public void testCalcTasksForMemberAny() throws Exception {
        when(database.query(any(String.class), any(String[].class), isNull(String.class), any(String.class))).thenReturn(manyCursor);
        List<Member> test = dao.load();

        assertEquals(2,test.get(0).getTasksCnt());
        assertEquals(1,test.get(1).getTasksCnt());
        assertEquals(2,test.get(2).getTasksCnt());
    }

    @Test
    public void testCalcLevelsForMembers() throws Exception {
        when(database.query(any(String.class), any(String[].class), isNull(String.class), any(String.class))).thenReturn(manyCursor);
        List<Member> test = dao.load();

        assertEquals(1,test.get(0).getLevel());
        assertEquals(2,test.get(1).getLevel());
        assertEquals(1,test.get(2).getLevel());
    }

    @Test
    public void testLoadEmptyPath() throws Exception {
        when(database.query(any(String.class), any(String[].class), isNull(String.class), any(String.class))).thenReturn(manyCursor);
        List<Member> test = dao.load();
        assertEquals("",test.get(0).getPath());
    }

    private void prepeareOneCursor() {
        when(oneCursor.moveToFirst()).thenReturn(true);
        when(oneCursor.getLong(anyInt())).thenReturn(ID).thenReturn(UID);
        when(oneCursor.getInt(anyInt())).thenReturn(0);
        when(oneCursor.getString(anyInt())).thenReturn(TITLE).thenReturn(PATH);
    }

    private void prepeareManyCursor() {
        when(manyCursor.moveToFirst()).thenReturn(true);
        when(manyCursor.moveToNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(manyCursor.getLong(0)).thenReturn(ID).thenReturn(ID + 1).thenReturn(ID + 2);
        when(manyCursor.getLong(1)).thenReturn(UID).thenReturn(-1L).thenReturn(UID + 1);
        when(manyCursor.getInt(5)).thenReturn(1);
        when(manyCursor.getInt(6)).thenReturn(1).thenReturn(2);
        when(manyCursor.getString(2)).thenReturn(TITLE).thenReturn("Any").thenReturn(TITLE + "1");
        when(manyCursor.getString(3)).thenReturn(PATH).thenReturn(null).thenReturn("");
    }
}