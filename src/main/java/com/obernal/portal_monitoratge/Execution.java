package com.obernal.portal_monitoratge;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Execution {
    private final String id;
    private final LocalDateTime created;
    private final float elapsedTimeInSeconds;
    public Execution(long start) {
        this.id = UUID.randomUUID().toString();
        this.created = LocalDateTime.now();
        this.elapsedTimeInSeconds = (System.currentTimeMillis() - start) / 1000F;
    }

    public abstract boolean isAlert();
    public abstract boolean isError();
    public abstract String getErrorMessage();

    public String getId() {
        return id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public float getElapsedTimeInSeconds() {
        return elapsedTimeInSeconds;
    }

}