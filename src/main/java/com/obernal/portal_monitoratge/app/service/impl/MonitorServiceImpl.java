package com.obernal.portal_monitoratge.app.service.impl;

import com.obernal.portal_monitoratge.Execution;
import com.obernal.portal_monitoratge.Monitor;
import com.obernal.portal_monitoratge.app.persistence.MonitorPersistence;
import com.obernal.portal_monitoratge.app.service.MonitorService;
import com.obernal.portal_monitoratge.app.service.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

public class MonitorServiceImpl implements MonitorService {
    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);

    private final MonitorPersistence persistence;
    private final SchedulerService scheduler;

    public MonitorServiceImpl(MonitorPersistence persistence, SchedulerService scheduler) {
        this.persistence = persistence;
        this.scheduler = scheduler;
    }

    @Override
    public Stream<Monitor> findAll() {
        logger.debug("Monitor Service: Finding all monitors");
        return persistence.findAll();
    }

    @Override
    public long scheduleActiveMonitors() {
        logger.debug("Monitor Service: Scheduling all active monitors");
        return this.findAll()
                .filter(Monitor::isActive)
                .peek(this::schedule)
                .count();
    }

    private void schedule(Monitor monitor) {
        logger.info("Scheduling monitor: {} and cron {}", monitor.getId(), monitor.getCron());
        Runnable runnable = () -> {
            logger.info("Executing monitor: {} at {}", monitor.getId(), LocalDateTime.now());
            Execution execution = monitor.run();
            logger.info("Execution finished for monitor: {} in {} seconds", monitor.getId(), execution.getElapsedTimeInSeconds());
        };
        if (monitor.isActive()) {
            scheduler.schedule(monitor.getId(), monitor.getCron(), runnable);
        }
    }
    @Override
    public Monitor create(Monitor monitor) {
        String id = UUID.randomUUID().toString();
        //monitor.setId(id);
        logger.debug("Monitor Service: Creating new monitor {}", monitor.getId());
        persistence.create(monitor);
        return monitor;
    }

    @Override
    public Monitor findById(String id) throws NotFoundException {
        logger.debug("Monitor Service: Find monitor {}", id);
        return this.persistence.findById(id)
                .orElseThrow(() -> new NotFoundException("Monitor id: " + id));
    }

    @Override
    public Monitor update(String id, Object data) throws NotFoundException {
        logger.debug("Monitor Service: Updating monitor {}", id);
        Monitor existingMonitor = findById(id);
        existingMonitor.update(data);
        schedule(existingMonitor);
        return persistence.update(existingMonitor);
    }

    @Override
    public Monitor toggle(String id) throws NotFoundException {
        Monitor monitor = findById(id);
        if(monitor.isActive()) {
            logger.debug("Disabling monitor: {}", id);
            scheduler.cancel(monitor.getId());
        }else {
            logger.debug("Enabling monitor {}", id);
            schedule(monitor);
        }
        monitor.toggle();
        monitor = persistence.update(monitor);
        return monitor;
    }

    @Override
    public Monitor delete(String id) throws NotFoundException {
        logger.debug("Monitor Service: Deleting monitor {}", id);
        Monitor monitor = findById(id);
        scheduler.cancel(monitor.getId());
        persistence.deleteById(id);
        return monitor;
    }

    @Override
    public Execution run(String id) throws NotFoundException {
        logger.debug("Monitor Service: Running monitor {}", id);
        Monitor monitor = findById(id);
        return monitor.run();
    }

}
