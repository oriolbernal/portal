package com.obernal.portal_monitoratge.app.service.impl;

import com.obernal.portal_monitoratge.app.service.MonitorFactory;
import com.obernal.portal_monitoratge.model.monitor.Monitor;
import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;
import com.obernal.portal_monitoratge.model.monitor.impl.clients.DbPoolSingleton;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMetadata;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMetadata;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorFactoryImpl implements MonitorFactory {
    private static final Logger logger = LoggerFactory.getLogger(MonitorFactoryImpl.class);

    private final DbPoolSingleton dbPoolSingleton;

    public MonitorFactoryImpl(DbPoolSingleton dbPoolSingleton) {
        this.dbPoolSingleton = dbPoolSingleton;
    }

    @Override
    public Monitor<?, ?> create(MonitorMetadata metadata) {
        return switch (metadata.getType()) {
            case SSL -> new SslMonitor((SslMetadata) metadata);
            case DB -> new DbMonitor((DbMetadata) metadata, dbPoolSingleton);
            default -> throw new RuntimeException("Type does not exist: " + metadata.getType());
        };
    }

}
