package com.obernal.portal_monitoratge.model.monitor;

import java.util.UUID;

public abstract class MonitorContext {

    protected final String id;
    protected final MonitorMetadata metadata;

    protected MonitorContext(MonitorMetadata metadata) {
        this.id = UUID.randomUUID().toString();
        this.metadata = metadata;
    }

    public abstract MonitorType getType();

    public String getId() {
        return id;
    }

    public MonitorMetadata getMetadata() {
        return metadata;
    }

    public void update(MonitorContext newContext) {
        metadata.update(newContext.getMetadata());
    }

    public void toggle() {
        metadata.toggle();
    }

    public boolean isActive() {
        return metadata.isActive();
    }


}
