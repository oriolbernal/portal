package com.obernal.portal_monitoratge.model.monitor.impl.ssl;

import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;
import com.obernal.portal_monitoratge.model.monitor.MonitorType;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpContext;

import java.net.http.HttpClient;

public class SslContext extends HttpContext {
    private final int daysInAdvance;

    public SslContext(MonitorMetadata context, String endpoint, int timeOutInSeconds, HttpClient.Version version, String[] sslProtocols, boolean clientCertificate, int daysInAdvance) {
        super(context, endpoint, RequestMethod.GET, null, timeOutInSeconds, version, HttpClient.Redirect.NEVER, sslProtocols, clientCertificate, null, null, false);
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
