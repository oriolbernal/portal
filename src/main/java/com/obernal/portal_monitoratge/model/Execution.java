package com.obernal.portal_monitoratge.model;

import com.obernal.portal_monitoratge.model.monitor.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Execution<R extends Result> {
    private final String id;
    private final float elapsedTimeInSeconds;
    private final R data;
    private final List<String> alerts;
    private final Exception exception;

    public Execution(long start, R data, List<String> alerts) {
        this.id = UUID.randomUUID().toString();
        this.elapsedTimeInSeconds = (System.currentTimeMillis() - start) / 1000F;
        this.data = data;
        this.alerts = alerts;
        this.exception = null;
    }

    public Execution(long start, Exception exception) {
        this.id = UUID.randomUUID().toString();
        this.elapsedTimeInSeconds = (System.currentTimeMillis() - start) / 1000F;
        this.data = null;
        this.alerts = new ArrayList<>();
        this.exception = exception;
    }

    public float getElapsedTimeInSeconds() {
        return elapsedTimeInSeconds;
    }

    public R getData() {
        return data;
    }

    public String getId() {
        return id;
    }

    public boolean isAlert() {
        return !alerts.isEmpty();
    }

    public boolean isError() {
        return exception != null;
    }

    public String getErrorMessage() {
        return isError() ? exception.getMessage() : "";
    }

}