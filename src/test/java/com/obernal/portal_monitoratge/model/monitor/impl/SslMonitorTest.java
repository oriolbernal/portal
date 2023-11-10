package com.obernal.portal_monitoratge.model.monitor.impl;

import com.obernal.portal_monitoratge.model.Execution;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

class SslMonitorTest {

    @Test
    void run() {
        SslMonitor monitor = new SslMonitor(
                "id",
                "https://google.es",
                50,
                HttpClient.Version.HTTP_1_1,
                HttpClient.Redirect.NEVER,
                new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                0
        );
        Execution execution = monitor.run();
        assertNotNull(execution);
        assertFalse(execution.isError());
        assertFalse(execution.isAlert());
    }
}