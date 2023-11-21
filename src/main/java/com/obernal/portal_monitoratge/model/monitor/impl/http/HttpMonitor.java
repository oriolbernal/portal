package com.obernal.portal_monitoratge.model.monitor.impl.http;

import com.obernal.portal_monitoratge.app.service.AlertService;
import com.obernal.portal_monitoratge.model.monitor.Monitor;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLHandshakeException;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HttpMonitor extends Monitor<HttpContext, HttpResult> {
    private static final Logger logger = LoggerFactory.getLogger(HttpMonitor.class);
    private final Properties properties;
    public HttpMonitor(AlertService alertService, HttpContext context, Properties properties) {
        super(alertService, context);
        this.properties = properties;
    }

    @Override
    protected HttpResult perform() throws Exception {
        try {
            KeyManager[] keyManagers = null;
            if(context.isClientCertificate()) {
                keyManagers = getClientCertificate(properties.getProperty("http.certificateClient.p12"), properties.getProperty("http.certificateClient.password"));
            }
            HttpClient client = context.getClient(keyManagers, null);
            HttpRequest request = context.getRequest();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new HttpResult(response);
        } catch (HttpConnectTimeoutException e) {
            throw new RuntimeException("Connection Timeout: " + e.getMessage());
        } catch (SSLHandshakeException e) {
            throw new RuntimeException("SSL Connection problem: " + e.getMessage());
        } catch (ConnectException e) {
            throw new RuntimeException("ConnectException (Unresolved host?): " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Nonexistent SSLProtocol: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("I/O error occurred when sending or receiving: " + e.getMessage());
        }
    }

    protected KeyManager[] getClientCertificate(String p12Path, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(p12Path), password.toCharArray());
            KeyManagerFactory keyMgrFactory = KeyManagerFactory.getInstance("SunX509");
            keyMgrFactory.init(keyStore, password.toCharArray());
            return keyMgrFactory.getKeyManagers();
        } catch (Exception e) {
            logger.warn("Unable to load client certificate, no certificate will be configured", e);
        }
        return null;
    }

    @Override
    public List<String> getAlerts(HttpResult result) throws Exception {
        List<String> alerts = new ArrayList<>();
        if(context.getExpectedStatusCode() != null && checkStatusCode(result.getStatusCode())){
            alerts.add("StatusCode (" + result.getStatusCode() + ") does not coincide with expected (" + context.getExpectedStatusCode() + ")");
        }
        if(context.getExpectedBody() != null && checkBody(result.getBody())) {
            alerts.add("Body (" + result.getBody() +  ") does not coincide (" + (context.isStrictCompare() ? "strict" : "non-strict") + " mode) with expected (" + context.getExpectedBody() + ") and responseBody (" + result.getBody()+ ")");
        }
        return alerts;
    }

    private boolean checkStatusCode(Integer statusCode) {
        return !context.getExpectedStatusCode().equals(statusCode);
    }

    private boolean checkBody(String body) throws JSONException {
        return !JSONCompare.compareJSON(
                context.getExpectedBody(),
                body,
                context.isStrictCompare() ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT
        ).passed();
    }

}
