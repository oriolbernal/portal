package com.obernal.portal_monitoratge.model.monitor.impl.db;

import com.obernal.portal_monitoratge.model.monitor.MonitorContext;
import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;
import com.obernal.portal_monitoratge.model.monitor.MonitorType;

public class DbContext extends MonitorContext {

    private final String datasource;
    private final String query;
    private final Long minValue;
    private final Long maxValue;
    private final String wordToSearch;

    public DbContext(MonitorMetadata metadata, String datasource, String query, Long minValue, Long maxValue, String wordToSearch) {
        super(metadata);
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
