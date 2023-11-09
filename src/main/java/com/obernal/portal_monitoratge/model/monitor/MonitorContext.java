package com.obernal.portal_monitoratge.model.monitor;

import java.util.Set;

public class MonitorContext {
    private final MonitorType type;
    private String name;
    private String description;
    private String cron;
    private String service;
    private Set<String> labels;
    private String documentation;
    private boolean active;

    public MonitorContext(MonitorType type, String name, String description, String cron, String service, Set<String> labels, String documentation, boolean active) {
        this.type = type;
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

    public MonitorType getType() {
        return type;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
