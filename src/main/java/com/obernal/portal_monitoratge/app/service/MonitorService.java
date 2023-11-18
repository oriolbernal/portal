package com.obernal.portal_monitoratge.app.service;

import com.obernal.portal_monitoratge.model.Execution;
import com.obernal.portal_monitoratge.app.service.exception.NotFoundException;
import com.obernal.portal_monitoratge.model.monitor.MonitorContext;

import java.util.stream.Stream;

public interface MonitorService {

    Stream<MonitorContext> findAll();

    long scheduleActiveMonitors();

    MonitorContext create(MonitorContext metadata);

    MonitorContext findById(String id) throws NotFoundException;

    MonitorContext update(String id, MonitorContext metadata) throws NotFoundException;

    MonitorContext toggle(String id) throws NotFoundException;

    MonitorContext delete(String id) throws NotFoundException;

    Execution<?> run(String id) throws NotFoundException;

}
