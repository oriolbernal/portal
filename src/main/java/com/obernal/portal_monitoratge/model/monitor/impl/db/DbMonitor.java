package com.obernal.portal_monitoratge.model.monitor.impl.db;

import com.obernal.portal_monitoratge.model.alert.Assert;
import com.obernal.portal_monitoratge.model.monitor.Monitor;
import com.obernal.portal_monitoratge.clients.DbConnectionPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbMonitor extends Monitor<DbContext, DbResult> {

    private final DbConnectionPool connectionPool;

    public DbMonitor(DbContext context, DbConnectionPool connectionPool, Assert<DbResult>... asserts) {
        super(context, asserts);
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

}
