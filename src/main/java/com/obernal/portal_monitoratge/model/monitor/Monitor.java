package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.app.service.AlertService;
import com.obernal.portal_monitoratge.model.alert.Alert;
import com.obernal.portal_monitoratge.model.execution.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class Monitor<C extends MonitorContext, R extends MonitorResult> {
    private static final Logger logger = LoggerFactory.getLogger(Monitor.class);

    protected final C context;
    private final AlertService alertService;

    protected Monitor(AlertService alertService, C context) {
        this.alertService = alertService;
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
            List<String> messages = getAlerts(result);
            context.changeState(!messages.isEmpty());
            Alert alert = computeAlert(result, messages);
            return new Execution<>(start, result, alert);
        } catch (Exception exception) {
            logger.error("Error executing monitor: {} --> {}", getId(), exception.getMessage(), exception);
            return new Execution<>(start, exception);
        }
    }

    protected abstract R perform() throws Exception;

    protected abstract List<String> getAlerts(R result) throws Exception;

    public MonitorState getState() {
        return context.state;
    }

    private Alert computeAlert(R result, List<String> messages) {
        return switch (context.state) {
            case FIRST_ALERT -> alertService.alert(context, result, messages);
            case INSIST -> alertService.insist(context, result, messages);
            case RECOVERY -> alertService.recover(context, result);
            case OK, ALERT -> null;
        };
    }
}
