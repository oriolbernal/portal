package com.obernal.portal_monitoratge.model.execution;

import com.obernal.portal_monitoratge.model.Alert;

import java.util.UUID;

public class Execution<R> {
    private final String id;
    private final float elapsedTimeInSeconds;
    private final R data;
    private final Alert alert;
    private final Exception exception;

    public Execution(long start, R data, Alert alert) {
        this.id = UUID.randomUUID().toString();
        this.elapsedTimeInSeconds = (System.currentTimeMillis() - start) / 1000F;
        this.data = data;
        this.alert = alert;
        this.exception = null;
    }

    public Execution(long start, Exception exception) {
        this.id = UUID.randomUUID().toString();
        this.elapsedTimeInSeconds = (System.currentTimeMillis() - start) / 1000F;
        this.data = null;
        this.alert = null;
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
        return alert != null;
    }

    public boolean isError() {
        return exception != null;
    }

    public String getErrorMessage() {
        return isError() ? exception.getMessage() : "";
    }

}