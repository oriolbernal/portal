package com.obernal.portal_monitoratge.app.service;

import com.obernal.portal_monitoratge.Execution;
import com.obernal.portal_monitoratge.Monitor;
import com.obernal.portal_monitoratge.app.service.exception.NotFoundException;

import java.util.stream.Stream;

public interface MonitorService {

    Stream<Monitor> findAll();

    long scheduleActiveMonitors();

    Monitor create(Monitor monitor);

    Monitor findById(String id) throws NotFoundException;

    Monitor update(String id, Object metadata) throws NotFoundException;

    Monitor toggle(String id) throws NotFoundException;

    Monitor delete(String id) throws NotFoundException;

    Execution run(String id) throws NotFoundException;

}