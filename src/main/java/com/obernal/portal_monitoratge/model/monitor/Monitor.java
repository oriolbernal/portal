package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.model.Alert;
import com.obernal.portal_monitoratge.model.execution.Execution;
import com.obernal.portal_monitoratge.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

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
            var alert = computeAlert(result);
            if(alert.isPresent()) {
                context.changeState(true);
                return new Execution<>(start, result, alert.get());
            }
            return new Execution<>(start, result, null);
        } catch (Exception exception) {
            logger.error("Error executing monitor: {} --> {}", getId(), exception.getMessage(), exception);
            return new Execution<>(start, exception);
        }
    }

    protected abstract R perform() throws Exception;
    protected abstract List<String> getAlerts(R result) throws Exception;

    protected Optional<Alert> computeAlert(R result) throws Exception {
        List<String> messages = getAlerts(result);
        if(messages.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new Alert(messages));
    }

}
