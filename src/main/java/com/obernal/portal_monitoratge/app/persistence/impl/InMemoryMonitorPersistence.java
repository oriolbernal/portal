package com.obernal.portal_monitoratge.app.persistence.impl;

import com.obernal.portal_monitoratge.Monitor;
import com.obernal.portal_monitoratge.app.persistence.MonitorPersistence;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class InMemoryMonitorPersistence implements MonitorPersistence {
    private final Map<String, Monitor> monitorStore;

    public InMemoryMonitorPersistence(Map<String, Monitor> monitorStore) {
        this.monitorStore = monitorStore;
    }

    @Override
    public Stream<Monitor> findAll() {
        return monitorStore.values().stream();
    }

    @Override
    public Optional<Monitor> findById(String id) {
        return Optional.ofNullable(monitorStore.get(id));
    }

    @Override
    public Monitor create(Monitor monitor) {
        monitorStore.put(monitor.getId(), monitor);
        return monitor;
    }

    @Override
    public Monitor update(Monitor monitor) {
        if (monitorStore.containsKey(monitor.getId())) {
            monitorStore.put(monitor.getId(), monitor);
            return monitor;
        }
        return null; // Return null to indicate that the monitor doesn't exist
    }

    @Override
    public void deleteById(String id) {
        monitorStore.remove(id);
    }

}
