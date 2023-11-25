package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.model.alert.Alert;
import com.obernal.portal_monitoratge.model.execution.Execution;
import com.obernal.portal_monitoratge.model.notification.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class Monitor<C extends MonitorContext, R extends MonitorResult> {
    private static final Logger logger = LoggerFactory.getLogger(Monitor.class);

    protected final C context;
    private final Notifier<R> notifier;
    protected MonitorState state;
    protected long counter;

    protected Monitor(C context, Notifier<R> notifier) {
        this.context = context;
        this.notifier = notifier;
        this.state = MonitorState.OK;
        this.counter = 0;
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
            changeState(!messages.isEmpty());
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
        return state;
    }

    private Alert computeAlert(R result, List<String> messages) {
        logger.info("Sending notification for monitor: " + getId());
        return switch (state) {
            case FIRST_ALERT -> notifier.alert(result, messages);
            case INSIST -> notifier.insist(result);
            case RECOVERY -> notifier.recover(result);
            case OK, ALERT -> null;
        };
    }

    public void changeState(boolean alert) {
        if (alert) {
            changeStateAfterAlert();
        } else {
            changeStateAfterOk();
        }
    }

    private void changeStateAfterAlert() {
        switch (state) {
            case OK, RECOVERY -> {
                state = MonitorState.FIRST_ALERT;
                counter = 1;
            }
            case FIRST_ALERT, ALERT, INSIST -> {
                state = mustInsist() ? MonitorState.INSIST : MonitorState.ALERT;
                counter++;
            }
        }
    }

    private void changeStateAfterOk() {
        switch (state) {
            case OK, RECOVERY -> {
                state = MonitorState.OK;
                counter++;
            }
            case FIRST_ALERT, ALERT, INSIST -> {
                state = MonitorState.RECOVERY;
                counter = 1;
            }
        }
    }

    private boolean mustInsist() {
        long insistAfter = notifier.getInsistAfter();
        return insistAfter == 0 || (counter+1) % insistAfter == 0;
    }

}
