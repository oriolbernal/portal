package com.obernal.portal_monitoratge.app.persistence;

import com.obernal.portal_monitoratge.Monitor;

import java.util.Optional;
import java.util.stream.Stream;

public interface MonitorPersistence {

    Stream<Monitor> findAll();

    Optional<Monitor> findById(String id);

    Monitor create(Monitor monitor);

    Monitor update(Monitor monitor);

    void deleteById(String id);

}
