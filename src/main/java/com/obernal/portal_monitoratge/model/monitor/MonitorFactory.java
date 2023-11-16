package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.clients.DbConnectionPool;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMetadata;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpMetadata;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMetadata;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMonitor;

import java.util.Properties;

public class MonitorFactory {

    private final Properties properties;
    private final DbConnectionPool connectionPool;

    public MonitorFactory(Properties properties, DbConnectionPool connectionPool) {
        this.properties = properties;
        this.connectionPool = connectionPool;
    }

    public Monitor<?, ?> create(MonitorMetadata metadata) {
        if (metadata.getType() == null) {
            throw new RuntimeException("Type does not exist: " + metadata.getType());
        }
        return switch (metadata.getType()) {
            case HTTP -> new HttpMonitor((HttpMetadata) metadata, properties);
            case SSL -> new SslMonitor((SslMetadata) metadata, properties);
            case DB -> new DbMonitor((DbMetadata) metadata, connectionPool);
        };
    }

}
