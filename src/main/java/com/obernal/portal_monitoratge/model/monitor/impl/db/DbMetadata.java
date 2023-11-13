package com.obernal.portal_monitoratge.model.monitor.impl.db;

import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;
import com.obernal.portal_monitoratge.model.monitor.MonitorType;

import java.time.LocalDateTime;
import java.util.Set;

public class DbMetadata extends MonitorMetadata {

    private final String datasource;
    private final String query;
    private final Long minValue;
    private final Long maxValue;
    private final String wordToSearch;


    public DbMetadata(String name, String description, String cron, String service, Set<String> labels, String documentation, String datasource, String query, Long minValue, Long maxValue, String wordToSearch) {
        super(name, description, cron, service, labels, documentation);
        this.datasource = datasource;
        this.query = query;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.wordToSearch = wordToSearch;
    }

    public DbMetadata(String id, LocalDateTime created, LocalDateTime updated, String name, String description, String cron, String service, Set<String> labels, String documentation, boolean active, String datasource, String query, Long minValue, Long maxValue, String wordToSearch) {
        super(id, created, updated, name, description, cron, service, labels, documentation, active);
        this.datasource = datasource;
        this.query = query;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.wordToSearch = wordToSearch;
    }

    @Override
    public MonitorType getType() {
        return MonitorType.DB;
    }

    public String getDatasource() {
        return datasource;
    }

    public String getQuery() {
        return query;
    }

    public Long getMinValue() {
        return minValue;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public String getWordToSearch() {
        return wordToSearch;
    }

}
