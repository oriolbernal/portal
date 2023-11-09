package com.obernal.portal_monitoratge.model;

import java.util.UUID;

public class Execution implements Report {
    private final String id;
    private final float elapsedTimeInSeconds;
    private final boolean alert;
    private final Exception exception;

    public Execution(long start, boolean alert) {
        this.id = UUID.randomUUID().toString();
        this.elapsedTimeInSeconds = (System.currentTimeMillis() - start) / 1000F;
        this.alert = alert;
        this.exception = null;
    }

    public Execution(long start, Exception exception) {
        this.id = UUID.randomUUID().toString();
        this.elapsedTimeInSeconds = (System.currentTimeMillis() - start) / 1000F;
        this.alert = false;
        this.exception = exception;
    }

    @Override
    public float getElapsedTimeInSeconds() {
        return elapsedTimeInSeconds;
    }

    public String getId() {
        return id;
    }

    public boolean isAlert() {
        return alert;
    }

    public boolean isError() {
        return exception != null;
    }

    public String getErrorMessage() {
        return isError() ? exception.getMessage() : "";
    }

}