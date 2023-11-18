package com.obernal.portal_monitoratge.model.monitor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class MonitorMetadata {

    private final String id;
    private final LocalDateTime created;
    private LocalDateTime updated;
    private String name;
    private String description;
    private String cron;
    private String service;
    private Set<String> labels;
    private String documentation;
    private boolean active;

    public MonitorMetadata(String name, String description, String cron, String service, Set<String> labels, String documentation) {
        this.id = UUID.randomUUID().toString();
        created = LocalDateTime.now();
        updated = null;
        this.name = name;
        this.description = description;
        this.cron = cron;
        this.service = service;
        this.labels = labels;
        this.documentation = documentation;
        this.active = true;
    }

    public MonitorMetadata(String id, LocalDateTime created, LocalDateTime updated, String name, String description, String cron, String service, Set<String> labels, String documentation, boolean active) {
        this.id = id;
        this.created = created;
        this.updated = updated;
        this.name = name;
        this.description = description;
        this.cron = cron;
        this.service = service;
        this.labels = labels;
        this.documentation = documentation;
        this.active = active;
    }
    public void toggle() {
        active = !active;
    }

    public void update(MonitorMetadata metadata) {
        this.name = metadata.getName();
        this.description = metadata.getDescription();
        this.cron = metadata.getCron();
        this.service = metadata.getService();
        this.labels = metadata.getLabels();
        this.documentation = metadata.getDocumentation();
        this.active = metadata.isActive();
        this.updated = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCron() {
        return cron;
    }

    public String getService() {
        return service;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public String getDocumentation() {
        return documentation;
    }

    public boolean isActive() {
        return active;
    }

}
