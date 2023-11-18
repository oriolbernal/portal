package com.obernal.portal_monitoratge.app.persistence;

import com.obernal.portal_monitoratge.model.monitor.MonitorContext;
import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;

import java.util.Optional;
import java.util.stream.Stream;

public interface MonitorPersistence {

    Stream<MonitorContext> findAll();

    Optional<MonitorContext> findById(String id);

    MonitorContext create(MonitorContext context);

    MonitorContext update(MonitorContext context);

    MonitorContext deleteById(String id);

}
