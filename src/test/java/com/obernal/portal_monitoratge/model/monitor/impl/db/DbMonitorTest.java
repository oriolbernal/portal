package com.obernal.portal_monitoratge.model.monitor.impl.db;

import com.obernal.portal_monitoratge.clients.DbConnectionPool;
import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DbMonitorTest {

    private DbConnectionPool connectionPool;
    private Connection connection;
    private Statement statement;
    private ResultSet result;
    private ResultSetMetaData metaData;

    @BeforeEach
    public void setup() {
        connectionPool = mock(DbConnectionPool.class);
        connection = mock(Connection.class);
        statement = mock(Statement.class);
        result = mock(ResultSet.class);
        metaData = mock(ResultSetMetaData.class);
    }

    @Test
    public void error_if_sqlException() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(10L, 20L, "testWord");
        mockData(null, null);
        when(result.next()).thenThrow(new SQLException("Test Exception"));
        var execution = monitor.run();
        assertTrue(execution.isError());
    }

    private void mockData(String key, String value) throws SQLException, ClassNotFoundException {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet result = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        when(connectionPool.getConnection("testDatasource")).thenReturn(connection);
        when(connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)).thenReturn(statement);
        when(statement.executeQuery("testQuery")).thenReturn(result);
        when(result.next()).thenReturn(true).thenReturn(false);
        when(result.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn(key);
        when(result.getString(1)).thenReturn(value);
    }
    @Test
    public void error_if_criteria_notConfigured() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(null, null, null);
        mockData("key", "testWord");
        var execution = monitor.run();
        assertEquals("Define an alert criteria: minValue, maxValue or wordToSearch can't be null", execution.getErrorMessage());
    }

    @Test
    public void alert_min() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(2L, null, null);
        mockData("key", "1");
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertTrue(execution.isAlert());
    }

    @Test
    public void alert_max() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(null, 2L, null);
        mockData("key", "3");
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertTrue(execution.isAlert());
    }

    @Test
    public void alert_min_max() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(2L, 4L, null);
        mockData("key", "3");
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertFalse(execution.isAlert());
    }

    @Test
    public void alert_searchWord() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(null, null, "word");
        mockData("key", "testWord");
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertTrue(execution.isAlert());
    }

    @Test
    public void alert_minMax_with_searchWord() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(1L, 3L, "test");
        mockData("key", "testWord");
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertTrue(execution.isAlert());
    }


    private DbMonitor createMonitor(Long min, Long max, String word) {
        return new DbMonitor(
                new DbContext(
                        new MonitorMetadata(
                                "name",
                                "desc",
                                "cron",
                                "service",
                                new HashSet<>(),
                                "docs",
                                0),
                        "testDatasource",
                        "testQuery",
                        min,
                        max,
                        word
                ),
                connectionPool);
    }

}
