package com.obernal.portal_monitoratge.model.monitor.impl;

import com.obernal.portal_monitoratge.model.monitor.Monitor;
import com.obernal.portal_monitoratge.model.monitor.MonitorType;
import com.obernal.portal_monitoratge.model.monitor.impl.clients.DbPoolSingleton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;

public class DbMonitor extends Monitor<DbResult> {

    private final DbPoolSingleton dbPoolSingleton;
    private final String datasource;
    private final String query;
    private final Long minValue;
    private final Long maxValue;
    private final String wordToSearch;

    public DbMonitor(String id, String name, String description, String cron, String service, Set<String> labels, String documentation, DbPoolSingleton dbPoolSingleton, String datasource, String query, Long minValue, Long maxValue, String wordToSearch) {
        super(id, MonitorType.SSL, name, description, cron, service, labels, documentation, true);
        this.dbPoolSingleton = dbPoolSingleton;
        this.datasource = datasource;
        this.query = query;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.wordToSearch = wordToSearch;
    }

    @Override
    protected DbResult perform() throws SQLException, ClassNotFoundException {
        Connection connection = dbPoolSingleton.getConnection(datasource);
        try (Statement statement = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery(query)) {
            return new DbResult(resultSet);
        }
    }

    @Override
    protected boolean isAlert(DbResult result) {
        String firstKey = new ArrayList<>(result.getData().get(0).keySet()).get(0);
        String firstValue = result.getData().get(0).get(firstKey);
        return isAlert(firstValue);
    }

    private boolean isAlert(String result) throws RuntimeException {
        if (minValue != null || maxValue != null) {
            return isBetweenMinAndMax(result);
        } else if (wordToSearch != null) {
            return searchWord(result);
        } else {
            throw new RuntimeException("Define an alert criteria: minValue, maxValue or wordToSearch can't be null");
        }
    }

    private boolean isBetweenMinAndMax(String result) {
        if (isNumber(result)) {
            long number = Long.parseLong(result);
            if (maxValue == null) return number < minValue;
            else if (minValue == null) return number > maxValue;
            else return number > maxValue || number < minValue;
        } else {
            throw new RuntimeException("Query result is not a number: " + result);
        }
    }

    private boolean isNumber(String strNumber) {
        try {
            Long.parseLong(strNumber);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean searchWord(String text) {
        return text.contains(wordToSearch);
    }

}
