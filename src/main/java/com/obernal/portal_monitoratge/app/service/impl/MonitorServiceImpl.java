package com.obernal.portal_monitoratge.app.service.impl;

import com.obernal.portal_monitoratge.app.service.MonitorFactory;
import com.obernal.portal_monitoratge.model.Execution;
import com.obernal.portal_monitoratge.model.monitor.Monitor;
import com.obernal.portal_monitoratge.app.persistence.MonitorPersistence;
import com.obernal.portal_monitoratge.app.service.MonitorService;
import com.obernal.portal_monitoratge.app.service.exception.NotFoundException;
import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class MonitorServiceImpl implements MonitorService {
    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);

    private final MonitorPersistence persistence;
    private final SchedulerService scheduler;
    private final MonitorFactory factory;

    public MonitorServiceImpl(MonitorPersistence persistence, SchedulerService scheduler, MonitorFactory factory) {
        this.persistence = persistence;
        this.scheduler = scheduler;
        this.factory = factory;
    }


    @Override
    public Stream<MonitorMetadata> findAll() {
        logger.debug("Monitor Service: Finding all monitors");
        return persistence.findAll();
    }

    @Override
    public long scheduleActiveMonitors() {
        logger.debug("Monitor Service: Scheduling all active monitors");
        return this.findAll()
                .filter(MonitorMetadata::isActive)
                .peek(this::schedule)
                .count();
    }

    private void schedule(MonitorMetadata metadata) {
        logger.info("Scheduling monitor: {} and cron {}", metadata.getId(), metadata.getCron());
        if (metadata.isActive()) {
            Monitor<?, ?> monitor = factory.create(metadata);
            scheduler.schedule(metadata.getId(), metadata.getCron(), runnable(monitor));
        }
    }

    private Runnable runnable(Monitor<?, ?> monitor) {
        return () -> {
            logger.info("Executing monitor: {} at {}", monitor.getId(), LocalDateTime.now());
            Execution<?> execution = monitor.run();
            logger.info("Execution finished for monitor: {} in {} seconds", monitor.getId(), execution.getElapsedTimeInSeconds());
        };
    }

    @Override
    public MonitorMetadata create(MonitorMetadata metadata) {
        logger.debug("Monitor Service: Creating new monitor {}", metadata.getId());
        persistence.create(metadata);
        return metadata;
    }

    @Override
    public MonitorMetadata findById(String id) throws NotFoundException {
        logger.debug("Monitor Service: Find monitor {}", id);
        return this.persistence.findById(id)
                .orElseThrow(() -> new NotFoundException("Monitor id: " + id));
    }

    @Override
    public MonitorMetadata update(String id, MonitorMetadata metadata) throws NotFoundException {
        logger.debug("Monitor Service: Updating monitor {}", id);
        MonitorMetadata existingMonitor = findById(id);
        existingMonitor.update(metadata);
        schedule(existingMonitor);
        return persistence.update(existingMonitor);
    }

    @Override
    public MonitorMetadata toggle(String id) throws NotFoundException {
        MonitorMetadata metadata = findById(id);
        if (metadata.isActive()) {
            logger.debug("Disabling monitor: {}", id);
            scheduler.cancel(metadata.getId());
        } else {
            logger.debug("Enabling monitor {}", id);
            schedule(metadata);
        }
        metadata.toggle();
        return persistence.update(metadata);
    }

    @Override
    public MonitorMetadata delete(String id) throws NotFoundException {
        logger.debug("Monitor Service: Deleting monitor {}", id);
        MonitorMetadata metadata = findById(id);
        scheduler.cancel(metadata.getId());
        persistence.deleteById(id);
        return metadata;
    }

    @Override
    public Execution<?> run(String id) throws NotFoundException {
        logger.debug("Monitor Service: Running monitor {}", id);
        MonitorMetadata metadata = findById(id);
        Monitor<?, ?> monitor = factory.create(metadata);
        return monitor.run();
    }

}
