package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.model.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Monitor<M extends MonitorMetadata, R extends Result> implements Task<R> {
    private static final Logger logger = LoggerFactory.getLogger(Monitor.class);

    protected final M metadata;

    protected Monitor(M metadata) {
        this.metadata = metadata;
    }

    @Override
    public String getId() {
        return metadata.getId();
    }

    @Override
    public Execution<R> run() {
        logger.info("Executing monitor: {}", getId());
        long start = System.currentTimeMillis();
        try {
            R result = perform();
            boolean alert = isAlert(result);
            return new Execution<>(start, alert, result);
        } catch (Exception exception) {
            logger.error("Error executing monitor: {} --> {}", getId(), exception.getMessage(), exception);
            return new Execution<>(start, exception);
        }
    }

    protected abstract R perform() throws Exception;
    protected abstract boolean isAlert(R result) throws Exception;

}
