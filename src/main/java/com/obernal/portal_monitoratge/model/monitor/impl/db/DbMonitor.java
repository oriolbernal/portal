package com.obernal.portal_monitoratge.model.monitor.impl.db;

import com.obernal.portal_monitoratge.model.monitor.Monitor;
import com.obernal.portal_monitoratge.model.monitor.impl.clients.DbPoolSingleton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DbMonitor extends Monitor<DbMetadata, DbResult> {

    private final DbPoolSingleton dbPoolSingleton;

    public DbMonitor(DbMetadata metadata, DbPoolSingleton dbPoolSingleton) {
        super(metadata);
        this.dbPoolSingleton = dbPoolSingleton;
    }

    @Override
    protected DbResult perform() throws SQLException, ClassNotFoundException {
        Connection connection = dbPoolSingleton.getConnection(metadata.getDatasource());
        try (Statement statement = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery(metadata.getQuery())) {
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
        if (metadata.getMinValue() != null || metadata.getMaxValue() != null) {
            return isBetweenMinAndMax(result);
        } else if (metadata.getWordToSearch() != null) {
            return searchWord(result);
        } else {
            throw new RuntimeException("Define an alert criteria: minValue, maxValue or wordToSearch can't be null");
        }
    }

    private boolean isBetweenMinAndMax(String result) {
        if (isNumber(result)) {
            long number = Long.parseLong(result);
            if (metadata.getMaxValue() == null) return number < metadata.getMinValue();
            else if (metadata.getMinValue() == null) return number > metadata.getMaxValue();
            else return number > metadata.getMaxValue() || number < metadata.getMinValue();
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
        return text.contains(metadata.getWordToSearch());
    }

}