package com.obernal.portal_monitoratge.model.monitor.impl.ssl;

import com.obernal.portal_monitoratge.model.monitor.MonitorType;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpMetadata;

import java.net.http.HttpClient;
import java.time.LocalDateTime;
import java.util.Set;

public class SslMetadata extends HttpMetadata {
    private final int daysInAdvance;

    public SslMetadata(String name, String description, String cron, String service, Set<String> labels, String documentation, String endpoint, int timeOutInSeconds, HttpClient.Version version, HttpClient.Redirect redirect, String[] sslProtocols, boolean sslAuth, int daysInAdvance) {
        super(name, description, cron, service, labels, documentation, endpoint, HttpMetadata.RequestMethod.GET, null, timeOutInSeconds, version, redirect, sslProtocols, sslAuth, null, null);
        this.daysInAdvance = daysInAdvance;
    }

    public SslMetadata(String id, LocalDateTime created, LocalDateTime updated, String name, String description, String cron, String service, Set<String> labels, String documentation, boolean active, String endpoint, int timeOutInSeconds, HttpClient.Version version, HttpClient.Redirect redirect, String[] sslProtocols, boolean sslAuth, int daysInAdvance) {
        super(id, created, updated, name, description, cron, service, labels, documentation, active, endpoint, HttpMetadata.RequestMethod.GET, null, timeOutInSeconds, version, redirect, sslProtocols, sslAuth, null, null);
        this.daysInAdvance = daysInAdvance;
    }

    @Override
    public MonitorType getType() {
        return MonitorType.SSL;
    }

    public int getDaysInAdvance() {
        return daysInAdvance;
    }

}
