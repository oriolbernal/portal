package com.obernal.portal_monitoratge.model.monitor.impl.db;

import com.obernal.portal_monitoratge.clients.DbConnectionPool;
import com.obernal.portal_monitoratge.model.alert.impl.MinMaxAssert;
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

    @BeforeEach
    public void setup() {
        connectionPool = mock(DbConnectionPool.class);
    }

    @Test
    public void testRunWithException() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(10L, 20L, "testWord");
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet result = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        when(connectionPool.getConnection("testDatasource")).thenReturn(connection);
        when(connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY))
                .thenReturn(statement);
        when(statement.executeQuery("testQuery")).thenReturn(result);
        when(result.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("key");
        when(result.next()).thenThrow(new SQLException("Test Exception"));

        var execution = monitor.run();

        assertTrue(execution.isError());
    }

    @Test
    public void test_min() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(2L, null, null);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet result = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        when(connectionPool.getConnection("testDatasource")).thenReturn(connection);
        when(connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY))
                .thenReturn(statement);
        when(statement.executeQuery("testQuery")).thenReturn(result);
        when(result.next()).thenReturn(true).thenReturn(false);
        when(result.getMetaData()).thenReturn(metaData);

        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("key");
        when(result.getString(1)).thenReturn("1");

        var execution = monitor.run();

        assertFalse(execution.isError());
        assertTrue(execution.isAlert());
    }

    @Test
    public void test_max() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(null, 2L, null);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet result = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        when(connectionPool.getConnection("testDatasource")).thenReturn(connection);
        when(connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY))
                .thenReturn(statement);
        when(statement.executeQuery("testQuery")).thenReturn(result);
        when(result.next()).thenReturn(true).thenReturn(false);
        when(result.getMetaData()).thenReturn(metaData);

        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("key");
        when(result.getString(1)).thenReturn("3");

        var execution = monitor.run();

        assertFalse(execution.isError());
        assertTrue(execution.isAlert());
    }

    @Test
    public void test_min_max() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(2L, 4L, null);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet result = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        when(connectionPool.getConnection("testDatasource")).thenReturn(connection);
        when(connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY))
                .thenReturn(statement);
        when(statement.executeQuery("testQuery")).thenReturn(result);
        when(result.next()).thenReturn(true).thenReturn(false);
        when(result.getMetaData()).thenReturn(metaData);

        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("key");
        when(result.getString(1)).thenReturn("3");

        var execution = monitor.run();

        assertFalse(execution.isError());
        assertFalse(execution.isAlert());
    }

    @Test
    public void test_SearchWord() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(null, null, null);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet result = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        when(connectionPool.getConnection("testDatasource")).thenReturn(connection);
        when(connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY))
                .thenReturn(statement);
        when(statement.executeQuery("testQuery")).thenReturn(result);
        when(result.next()).thenReturn(true).thenReturn(false);
        when(result.getMetaData()).thenReturn(metaData);

        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("key");
        when(result.getString(1)).thenReturn("testWord");

        var execution = monitor.run();

        assertFalse(execution.isError());
        assertTrue(execution.isAlert());
    }

    @Test
    public void test_SearchWord_with_minMax() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(1L, 3L, null);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet result = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        when(connectionPool.getConnection("testDatasource")).thenReturn(connection);
        when(connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY))
                .thenReturn(statement);
        when(statement.executeQuery("testQuery")).thenReturn(result);
        when(result.next()).thenReturn(true).thenReturn(false);
        when(result.getMetaData()).thenReturn(metaData);

        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("key");
        when(result.getString(1)).thenReturn("testWord");
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertTrue(execution.isAlert());
    }

    @Test
    public void test_error() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(null, null, null);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet result = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        when(connectionPool.getConnection("testDatasource")).thenReturn(connection);
        when(connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY))
                .thenReturn(statement);
        when(statement.executeQuery("testQuery")).thenReturn(result);
        when(result.next()).thenReturn(true).thenReturn(false);
        when(result.getMetaData()).thenReturn(metaData);

        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("key");
        when(result.getString(1)).thenReturn("testWord");

        var execution = monitor.run();
        assertEquals("Define an alert criteria: minValue, maxValue or wordToSearch can't be null", execution.getErrorMessage());
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
                                "docs"),
                        "testDatasource",
                        "testQuery",
                        min,
                        max,
                        word
                ),
                connectionPool, new MinMaxAssert(min, max));
    }

}
