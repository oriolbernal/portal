package com.obernal.portal_monitoratge.model.monitor.impl.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DbResultTest {

    ResultSet resultSet;
    ResultSetMetaData metaData;


    @BeforeEach
    public void setup() {
        resultSet = mock(ResultSet.class);
        metaData = mock(ResultSetMetaData.class);
    }

    @Test
    public void create() throws SQLException {
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("key");
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("value");
        DbResult result = new DbResult(resultSet);
        assertEquals(1, result.getColumns());
        assertEquals(1, result.getRows());
        assertEquals(1, result.getTable().size());
        assertEquals(1, result.getTable().get(0).size());
        assertEquals("value", result.getTable().get(0).get("key"));
    }

}