package com.obernal.portal_monitoratge.app.service;

import com.obernal.portal_monitoratge.model.Execution;
import com.obernal.portal_monitoratge.app.service.exception.NotFoundException;
import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;

import java.util.stream.Stream;

public interface MonitorService {

    Stream<MonitorMetadata> findAll();

    long scheduleActiveMonitors();

    MonitorMetadata create(MonitorMetadata metadata);

    MonitorMetadata findById(String id) throws NotFoundException;

    MonitorMetadata update(String id, MonitorMetadata metadata) throws NotFoundException;

    MonitorMetadata toggle(String id) throws NotFoundException;

    MonitorMetadata delete(String id) throws NotFoundException;

    Execution<?> run(String id) throws NotFoundException;

}
