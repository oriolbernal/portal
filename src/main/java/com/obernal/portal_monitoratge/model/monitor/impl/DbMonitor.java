package com.obernal.portal_monitoratge.model.monitor.impl;

import com.obernal.portal_monitoratge.model.Execution;
import com.obernal.portal_monitoratge.model.Task;
import com.obernal.portal_monitoratge.model.monitor.impl.clients.DbPoolSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DbMonitor implements Task<Execution> {
    private static final Logger logger = LoggerFactory.getLogger(DbMonitor.class);

    private final String id;
    private final DbPoolSingleton dbPoolSingleton;
    private final String datasource;
    private final String query;
    private final Long minValue;
    private final Long maxValue;
    private final String wordToSearch;

    public DbMonitor(String id, DbPoolSingleton dbPoolSingleton, String datasource, String query, Long minValue, Long maxValue, String wordToSearch) {
        this.id = id;
        this.dbPoolSingleton = dbPoolSingleton;
        this.datasource = datasource;
        this.query = query;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.wordToSearch = wordToSearch;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public Execution run() {
        logger.info("Executing monitor: {}", id);
        long start = System.currentTimeMillis();
        try {
            DbResult result = compute();
            boolean alert = isAlert(result);
            return new Execution(start, alert);
        } catch (Exception exception) {
            logger.error("Error executing monitor: {} --> {}", id, exception.getMessage(), exception);
            return new Execution(start, exception);
        }
    }

    private DbResult compute() throws SQLException, ClassNotFoundException {
        Connection connection = dbPoolSingleton.getConnection(datasource);
        try (Statement statement = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery(query)) {
            return new DbResult(resultSet);
        }
    }

    private boolean isAlert(DbResult result) {
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
