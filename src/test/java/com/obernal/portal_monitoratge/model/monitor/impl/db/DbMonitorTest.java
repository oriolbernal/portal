package com.obernal.portal_monitoratge.model.monitor.impl.db;

import com.obernal.portal_monitoratge.app.clients.DbConnectionPool;
import com.obernal.portal_monitoratge.app.service.AlertService;
import com.obernal.portal_monitoratge.model.alert.Alert;
import com.obernal.portal_monitoratge.model.alert.AlertType;
import com.obernal.portal_monitoratge.model.monitor.MonitorContext;
import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;
import com.obernal.portal_monitoratge.model.monitor.MonitorResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DbMonitorTest {

    private AlertService alertService;
    private DbConnectionPool connectionPool;
    private Connection connection;
    private Statement statement;
    private ResultSet result;
    private ResultSetMetaData metaData;

    @BeforeEach
    public void setup() {
        alertService = mock(AlertService.class);
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
        mockAlert();
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertTrue(execution.isAlert());
    }

    private void mockAlert() {
        when(alertService.alert(any(MonitorContext.class), any(MonitorResult.class), any(List.class))).thenReturn(new Alert(new ArrayList<>()) {
            @Override
            public AlertType getType() {
                return null;
            }
        });
    }

    @Test
    public void alert_max() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(null, 2L, null);
        mockData("key", "3");
        mockAlert();
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
        mockAlert();
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertTrue(execution.isAlert());
    }

    @Test
    public void alert_minMax_with_searchWord() throws SQLException, ClassNotFoundException {
        var monitor = createMonitor(1L, 3L, "test");
        mockData("key", "testWord");
        mockAlert();
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertTrue(execution.isAlert());
    }


    private DbMonitor createMonitor(Long min, Long max, String word) {
        return new DbMonitor(
                alertService,
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
