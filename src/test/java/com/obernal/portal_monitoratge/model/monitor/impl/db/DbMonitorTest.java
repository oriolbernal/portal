package com.obernal.portal_monitoratge.model.monitor.impl.db;

import com.obernal.portal_monitoratge.model.monitor.impl.clients.DbConnectionPool;
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
    private DbConnectionPool connectionPool;

    @BeforeEach
    public void setup() {
        connectionPool = mock(DbConnectionPool.class);
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
        dbMonitor = new DbMonitor(metadata, connectionPool);
    }

    @Test
    public void testRunWithException() throws SQLException, ClassNotFoundException {
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

        var execution = dbMonitor.run();

        assertTrue(execution.isError());
    }

    @Test
    public void testRunWithoutException() throws SQLException, ClassNotFoundException {
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
        when(result.getString(1)).thenReturn("15");

        var execution = dbMonitor.run();

        assertFalse(execution.isError());
    }

    @Test
    public void test_min() throws SQLException, ClassNotFoundException {
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
                2L,
                null,
                "testWord");
        var monitor = new DbMonitor(metadata, connectionPool);

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
                null,
                2L,
                "testWord");
        var monitor = new DbMonitor(metadata, connectionPool);

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
                2L,
                4L,
                "testWord");
        var monitor = new DbMonitor(metadata, connectionPool);

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
                null,
                null,
                "testWord");
        var monitor = new DbMonitor(metadata, connectionPool);

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
                1L,
                3L,
                "testWord");
        var monitor = new DbMonitor(metadata, connectionPool);

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
                null,
                null,
                null);
        var monitor = new DbMonitor(metadata, connectionPool);

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

}
