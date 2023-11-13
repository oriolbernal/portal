package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.model.monitor.impl.clients.DbPoolSingleton;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMetadata;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpMetadata;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMetadata;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMonitor;

public class MonitorFactory {

    private final DbPoolSingleton dbPoolSingleton;

    public MonitorFactory(DbPoolSingleton dbPoolSingleton) {
        this.dbPoolSingleton = dbPoolSingleton;
    }

    public Monitor<?, ?> create(MonitorMetadata metadata) {
        if (metadata.getType() == null) {
            throw new RuntimeException("Type does not exist: " + metadata.getType());
        }
        return switch (metadata.getType()) {
            case SSL -> new SslMonitor((SslMetadata) metadata);
            case DB -> new DbMonitor((DbMetadata) metadata, dbPoolSingleton);
            case HTTP -> new HttpMonitor((HttpMetadata) metadata);
        };
    }

}
