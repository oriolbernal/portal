package com.obernal.portal_monitoratge.app.persistence;

import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;

import java.util.Optional;
import java.util.stream.Stream;

public interface MonitorPersistence {

    Stream<MonitorMetadata> findAll();

    Optional<MonitorMetadata> findById(String id);

    MonitorMetadata create(MonitorMetadata monitor);

    MonitorMetadata update(MonitorMetadata monitor);

    void deleteById(String id);

}
