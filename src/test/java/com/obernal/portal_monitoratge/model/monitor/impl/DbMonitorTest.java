package com.obernal.portal_monitoratge.model.monitor.impl;

import com.obernal.portal_monitoratge.model.Execution;
import com.obernal.portal_monitoratge.model.monitor.impl.clients.DbPoolSingleton;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMetadata;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DbMonitorTest {

    private DbMonitor dbMonitor;
    private DbPoolSingleton dbPoolSingleton;

    @BeforeEach
    public void setup() {
        dbPoolSingleton = mock(DbPoolSingleton.class);
        DbMetadata metadata = new DbMetadata("id",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "name",
                "desc",
                "cron",
                "service",
                new HashSet<>(),
                "docs",
                true,
                "testDatasource",
                "testQuery",
                10L,
                20L,
                "testWord");
        dbMonitor = new DbMonitor(metadata, dbPoolSingleton);
    }

    @Test
    public void testRunWithException() throws SQLException, ClassNotFoundException {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet result = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        when(dbPoolSingleton.getConnection("testDatasource")).thenReturn(connection);
        when(connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY))
                .thenReturn(statement);
        when(statement.executeQuery("testQuery")).thenReturn(result);
        when(result.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("key");
        when(result.next()).thenThrow(new SQLException("Test Exception"));

        Execution execution = dbMonitor.run();

        assertTrue(execution.isError());
    }

    @Test
    public void testRunWithoutException() throws SQLException, ClassNotFoundException {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet result = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        when(dbPoolSingleton.getConnection("testDatasource")).thenReturn(connection);
        when(connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY))
                .thenReturn(statement);
        when(statement.executeQuery("testQuery")).thenReturn(result);
        when(result.next()).thenReturn(true).thenReturn(false);
        when(result.getMetaData()).thenReturn(metaData);

        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("key");
        when(result.getString(1)).thenReturn("15");

        Execution execution = dbMonitor.run();

        assertFalse(execution.isError());
    }

}
