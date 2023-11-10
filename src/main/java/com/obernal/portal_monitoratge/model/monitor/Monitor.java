package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.model.Execution;
import com.obernal.portal_monitoratge.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Monitor<C extends MonitorContext, R> implements Task<Execution> {
    private static final Logger logger = LoggerFactory.getLogger(Monitor.class);

    private final String id;
    protected C context;

    protected Monitor(String id, C context) {
        this.id = id;
        this.context = context;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Execution run() {
        logger.info("Executing monitor: {}", id);
        long start = System.currentTimeMillis();
        try {
            R result = perform();
            boolean alert = isAlert();
            return new Execution(start, alert);
        } catch (Exception exception) {
            logger.error("Error executing monitor: {} --> {}", id, exception.getMessage(), exception);
            return new Execution(start, exception);
        }
    }

    public abstract R perform();
    public abstract boolean isAlert();

    public String getCron() {
        return context.getCron();
    }

    public void update(C context) {
        this.context = context;
    }

    public void toggle() {
        this.context.toggle();
    }

    public boolean isActive() {
        return context.isActive();
    }

}
