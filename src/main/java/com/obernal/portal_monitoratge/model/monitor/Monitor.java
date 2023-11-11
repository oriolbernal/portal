package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.model.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public abstract class Monitor<R> implements Task<R> {
    private static final Logger logger = LoggerFactory.getLogger(Monitor.class);

    private final String id;
    private MonitorType type;
    private String name;
    private String description;
    private String cron;
    private String service;
    private Set<String> labels;
    private String documentation;
    private boolean active;

    protected Monitor(String id, MonitorType type, String name, String description, String cron, String service, Set<String> labels, String documentation, boolean active) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.cron = cron;
        this.service = service;
        this.labels = labels;
        this.documentation = documentation;
        this.active = active;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Execution<R> run() {
        logger.info("Executing monitor: {}", id);
        long start = System.currentTimeMillis();
        try {
            R result = perform();
            boolean alert = isAlert(result);
            return new Execution<>(start, alert, result);
        } catch (Exception exception) {
            logger.error("Error executing monitor: {} --> {}", id, exception.getMessage(), exception);
            return new Execution<>(start, exception);
        }
    }

    protected abstract R perform() throws Exception;
    protected abstract boolean isAlert(R result) throws Exception;

    public String getCron() {
        return cron;
    }

    public void update(MonitorContext context) {
        type = context.getType();
        name = context.getName();
        description = context.getDescription();
        cron = context.getCron();
        service = context.getService();
        labels = context.getLabels();
        documentation = context.getDocumentation();
        active = context.isActive();
    }

    public void toggle() {
        active = !active;
    }

    public boolean isActive() {
        return active;
    }

}
