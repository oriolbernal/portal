package com.obernal.portal_monitoratge.model.monitor.impl.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbResult {
    private final List<Map<String, String>> data;
    private final int columns;
    private int rows;

    public DbResult(ResultSet result) throws SQLException {
        data = new ArrayList<>();
        ResultSetMetaData md = result.getMetaData();
        columns = md.getColumnCount();
        while (result.next()) {
            Map<String, String> row = new HashMap<>();
            for (int i = 1; i <= columns; i++) {
                row.put(md.getColumnLabel(i), result.getString(i));
            }
            data.add(row);
            rows++;
        }
    }

    public List<Map<String, String>> getData() {
        return data;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }
}
