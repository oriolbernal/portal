package com.obernal.portal_monitoratge.model.monitor.impl.db;

import com.obernal.portal_monitoratge.model.monitor.Result;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbResult extends Result {

    public DbResult(ResultSet result) throws SQLException {
        super();
        List<Map<String, String>> table = new ArrayList<>();
        ResultSetMetaData md = result.getMetaData();
        int columns = md.getColumnCount();
        int rows = 0;
        while (result.next()) {
            Map<String, String> row = new HashMap<>();
            for (int i = 1; i <= columns; i++) {
                row.put(md.getColumnLabel(i), result.getString(i));
            }
            table.add(row);
            rows++;
        }
        data.put("columns", columns);
        data.put("rows", rows);
        data.put("table", table);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> getTable() {
        return (List<Map<String, String>>) data.get("table");
    }

    public int getColumns() {
        return (int) data.get("columns");
    }

    public int getRows() {
        return (int) data.get("rows");
    }

}
