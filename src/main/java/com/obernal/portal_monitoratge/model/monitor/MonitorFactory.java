package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.app.service.AlertService;
import com.obernal.portal_monitoratge.app.clients.DbConnectionPool;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbContext;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpContext;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslContext;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMonitor;

import java.util.Properties;

public class MonitorFactory {

    private final Properties properties;
    private final AlertService alertService;
    private final DbConnectionPool connectionPool;

    public MonitorFactory(Properties properties, AlertService alertService, DbConnectionPool connectionPool) {
        this.properties = properties;
        this.alertService = alertService;
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
        return new HttpMonitor(alertService, context, properties);
    }

    private SslMonitor create(SslContext context) {
        return new SslMonitor(alertService, context, properties);
    }

    private DbMonitor create(DbContext context) {
        return new DbMonitor(alertService, context, connectionPool);
    }

}
