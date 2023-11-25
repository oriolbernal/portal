package com.obernal.portal_monitoratge.model.monitor.impl.db;

import com.obernal.portal_monitoratge.model.monitor.Monitor;
import com.obernal.portal_monitoratge.app.clients.DbConnectionPool;
import com.obernal.portal_monitoratge.model.notification.Notifier;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DbMonitor extends Monitor<DbContext, DbResult> {

    private final DbConnectionPool connectionPool;

    public DbMonitor(DbContext context, Notifier<DbResult> notifier, DbConnectionPool connectionPool) {
        super(context, notifier);
        this.connectionPool = connectionPool;
    }

    @Override
    protected DbResult perform() throws SQLException, ClassNotFoundException {
        Connection connection = connectionPool.getConnection(context.getDatasource());
        try (Statement statement = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery(context.getQuery())) {
            return new DbResult(resultSet);
        }
    }

    @Override
    public List<String> getAlerts(DbResult result) {
        if(context.getMinValue() == null && context.getMaxValue() == null && context.getWordToSearch() == null) {
            throw new RuntimeException("Define an alert criteria: minValue, maxValue or wordToSearch can't be null");
        }
        List<String> alerts = new ArrayList<>();
        String firstValue = getFirstValue(result);
        if(isNumber(firstValue)) {
            long number = Long.parseLong(firstValue);
            if(isNotBeweenMinMax(number)) {
                alerts.add("Number (" + number + ") not between range (" + context.getMinValue() + ", " + context.getMaxValue() + ")");
            }
        } else {
            if (firstValue.toLowerCase().contains(context.getWordToSearch().toLowerCase())) {
                alerts.add("Found word (" + context.getWordToSearch() + ") inside result (" + firstValue + ")");
            }
        }
        return alerts;
    }

    private String getFirstValue(DbResult result) {
        String firstKey = new ArrayList<>(result.getTable().get(0).keySet()).get(0);
        return result.getTable().get(0).get(firstKey);
    }

    private boolean isNumber(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isNotBeweenMinMax(long number) {
        if (context.getMaxValue() == null) return number < context.getMinValue();
        else if (context.getMinValue() == null) return number > context.getMaxValue();
        else return number > context.getMaxValue() || number < context.getMinValue();
    }


}
