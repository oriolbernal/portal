package com.obernal.portal_monitoratge.app.service.impl;

import com.obernal.portal_monitoratge.model.monitor.MonitorContext;
import com.obernal.portal_monitoratge.model.monitor.MonitorFactory;
import com.obernal.portal_monitoratge.model.Execution;
import com.obernal.portal_monitoratge.model.monitor.Monitor;
import com.obernal.portal_monitoratge.app.persistence.MonitorPersistence;
import com.obernal.portal_monitoratge.app.service.MonitorService;
import com.obernal.portal_monitoratge.app.service.exception.NotFoundException;
import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public Stream<MonitorContext> findAll() {
        logger.debug("Monitor Service: Finding all monitors");
        return persistence.findAll();
    }

    @Override
    public long scheduleActiveMonitors() {
        logger.debug("Monitor Service: Scheduling all active monitors");
        return this.findAll()
                .filter(MonitorContext::isActive)
                .peek(this::schedule)
                .count();
    }

    private void schedule(MonitorContext context) {
        MonitorMetadata metadata = context.getMetadata();
        logger.info("Scheduling monitor: {} and cron {}", metadata.getId(), metadata.getCron());
        if (metadata.isActive()) {
            Monitor<?, ?> monitor = factory.create(context);
            scheduler.schedule(metadata.getId(), metadata.getCron(), monitor::run);
        }
    }

    @Override
    public MonitorContext create(MonitorContext context) {
        logger.debug("Monitor Service: Creating new monitor {}", context.getId());
        return persistence.create(context);
    }

    @Override
    public MonitorContext findById(String id) throws NotFoundException {
        logger.debug("Monitor Service: Find monitor {}", id);
        return this.persistence.findById(id)
                .orElseThrow(() -> new NotFoundException("Monitor id: " + id));
    }

    @Override
    public MonitorContext update(String id, MonitorContext newContext) throws NotFoundException {
        logger.debug("Monitor Service: Updating monitor {}", id);
        MonitorContext existingMonitor = findById(id);
        existingMonitor.update(newContext);
        schedule(existingMonitor);
        return persistence.update(existingMonitor);
    }

    @Override
    public MonitorContext toggle(String id) throws NotFoundException {
        MonitorContext context = findById(id);
        if (context.isActive()) {
            logger.debug("Disabling monitor: {}", id);
            scheduler.cancel(context.getId());
        } else {
            logger.debug("Enabling monitor {}", id);
            schedule(context);
        }
        context.toggle();
        return persistence.update(context);
    }

    @Override
    public MonitorContext delete(String id) throws NotFoundException {
        logger.debug("Monitor Service: Deleting monitor {}", id);
        MonitorContext context = findById(id);
        scheduler.cancel(context.getId());
        return persistence.deleteById(id);
    }

    @Override
    public Execution<?> run(String id) throws NotFoundException {
        logger.debug("Monitor Service: Running monitor {}", id);
        MonitorContext context = findById(id);
        Monitor<?, ?> monitor = factory.create(context);
        return monitor.run();
    }

}
