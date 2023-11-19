package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.model.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class Monitor<C extends MonitorContext, R extends Result> {
    private static final Logger logger = LoggerFactory.getLogger(Monitor.class);

    protected final C context;

    protected Monitor(C context) {
        this.context = context;
    }

    public String getId() {
        return context.getId();
    }

    public Execution<R> run() {
        logger.info("Executing monitor: {}", getId());
        long start = System.currentTimeMillis();
        try {
            R result = perform();
            List<String> alerts = getAlerts(result);
            return new Execution<>(start, result, alerts);
        } catch (Exception exception) {
            logger.error("Error executing monitor: {} --> {}", getId(), exception.getMessage(), exception);
            return new Execution<>(start, exception);
        }
    }

    protected abstract R perform() throws Exception;
    protected abstract List<String> getAlerts(R result) throws Exception;

}
