package com.obernal.portal_monitoratge.model.monitor.impl;

import com.obernal.portal_monitoratge.model.Execution;
import com.obernal.portal_monitoratge.model.monitor.impl.clients.DbPoolSingleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DbMonitorTest {

    private DbMonitor dbMonitor;
    private DbPoolSingleton dbPoolSingleton;

    @BeforeEach
    public void setup() {
        // Mocking the dependencies
        dbPoolSingleton = mock(DbPoolSingleton.class);
        dbMonitor = new DbMonitor(
                "id",
                "name",
                "desc",
                "cron",
                "service",
                new HashSet<>(),
                "docs",
                dbPoolSingleton,
                "testDatasource",
                "testQuery",
                10L,
                20L,
                "testWord"
        );
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
