package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.clients.DbConnectionPool;
import com.obernal.portal_monitoratge.model.alert.impl.JsonAssert;
import com.obernal.portal_monitoratge.model.alert.impl.MinMaxAssert;
import com.obernal.portal_monitoratge.model.alert.impl.SslExpirationDateAssert;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbContext;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpContext;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslContext;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMonitor;

import java.util.Properties;

public class MonitorFactory {

    private final Properties properties;
    private final DbConnectionPool connectionPool;

    public MonitorFactory(Properties properties, DbConnectionPool connectionPool) {
        this.properties = properties;
        this.connectionPool = connectionPool;
    }

    public Monitor<?, ?> create(MonitorContext context) {
        if (context.getType() == null) {
            throw new RuntimeException("Type does not exist: " + context.getType());
        }
        return switch (context.getType()) {
            case HTTP -> create((HttpContext) context);
            case SSL -> create((SslContext) context);
            case DB -> create((DbContext) context);
        };
    }

    private HttpMonitor create(HttpContext context) {
        var cAssert = new JsonAssert(context.getExpectedBody(), false);
        return new HttpMonitor(context, properties, cAssert);
    }

    private SslMonitor create(SslContext context) {
        var cAssert = new SslExpirationDateAssert(context.getDaysInAdvance());
        return new SslMonitor(context, properties, cAssert);
    }

    private DbMonitor create(DbContext context) {
        var cAssert = new MinMaxAssert(context.getMinValue(), context.getMaxValue());
        return new DbMonitor(context, connectionPool, cAssert);
    }

}
