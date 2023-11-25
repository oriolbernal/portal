package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.app.clients.DbConnectionPool;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbContext;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbResult;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpContext;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpResult;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslContext;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslResult;
import com.obernal.portal_monitoratge.model.notification.Notifier;
import com.obernal.portal_monitoratge.model.notification.NotifierFactory;

import java.util.Properties;

public class MonitorFactory {

    private final NotifierFactory notifierFactory;
    private final Properties properties;
    private final DbConnectionPool connectionPool;

    public MonitorFactory(NotifierFactory notifierFactory, Properties properties, DbConnectionPool connectionPool) {
        this.notifierFactory = notifierFactory;
        this.properties = properties;
        this.connectionPool = connectionPool;
    }

    public Monitor<?, ?> create(MonitorContext context) {
        if (context.getType() == null) {
            throw new RuntimeException("Type does not exist: " + context.getType());
        }
        return switch (context.getType()) {
            case HTTP -> new HttpMonitor((HttpContext) context, (Notifier<HttpResult>) notifierFactory.create(context), properties);
            case SSL -> new SslMonitor((SslContext) context, (Notifier<SslResult>) notifierFactory.create(context), properties);
            case DB -> new DbMonitor((DbContext) context, (Notifier<DbResult>) notifierFactory.create(context), connectionPool);
        };
    }

}
