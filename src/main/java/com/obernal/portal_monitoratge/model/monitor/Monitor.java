package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.model.Execution;
import com.obernal.portal_monitoratge.model.alert.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Monitor<C extends MonitorContext, R extends Result> implements Task<R> {
    private static final Logger logger = LoggerFactory.getLogger(Monitor.class);

    protected final C context;
    protected final List<Assert<R>> asserts;

    protected Monitor(C context, Assert<R>... asserts) {
        this.context = context;
        this.asserts = Arrays.stream(asserts).toList();
    }

    @Override
    public String getId() {
        return context.getId();
    }

    @Override
    public Execution<R> run() {
        logger.info("Executing monitor: {}", getId());
        long start = System.currentTimeMillis();
        try {
            R result = perform();
            //boolean alert = isAlert(result);
            List<String> alerts = isAlert(result);
            return new Execution<>(start, alerts, result);
        } catch (Exception exception) {
            logger.error("Error executing monitor: {} --> {}", getId(), exception.getMessage(), exception);
            return new Execution<>(start, exception);
        }
    }

    protected abstract R perform() throws Exception;

    private List<String> isAlert(R result) throws Exception {
        List<String> alertMessages = new ArrayList<>();
        for (Assert<R> mAssert : asserts) {
            boolean alert = mAssert.isAlert(result);
            if(alert) {
                alertMessages.add(mAssert.getMessage(result));
            }
        }
        return alertMessages;
    }

}
