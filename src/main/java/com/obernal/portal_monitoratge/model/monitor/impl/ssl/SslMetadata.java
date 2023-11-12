package com.obernal.portal_monitoratge.model.monitor.impl.ssl;

import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;
import com.obernal.portal_monitoratge.model.monitor.MonitorType;

import javax.net.ssl.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

public class SslMetadata extends MonitorMetadata {
    private final String endpoint;
    private final RequestMethod method;
    private final HttpRequest.BodyPublisher publisher;
    private final int timeOutInSeconds;
    private final HttpClient.Version version;
    private final HttpClient.Redirect redirect;
    private final SSLParameters sslParameters;
    private final int daysInAdvance;

    public SslMetadata(String name, String description, String cron, String service, Set<String> labels, String documentation, String endpoint, int timeOutInSeconds, HttpClient.Version version, HttpClient.Redirect redirect, String[] sslProtocols, int daysInAdvance) {
        super(MonitorType.SSL, name, description, cron, service, labels, documentation);
        this.endpoint = endpoint;
        this.method = RequestMethod.GET;
        this.publisher = null;
        this.timeOutInSeconds = timeOutInSeconds;
        this.version = version;
        this.redirect = redirect;
        sslParameters = new SSLParameters();
        sslParameters.setProtocols(sslProtocols);
        this.daysInAdvance = daysInAdvance;
    }

    public SslMetadata(String id, LocalDateTime created, LocalDateTime updated, String name, String description, String cron, String service, Set<String> labels, String documentation, boolean active, String endpoint, int timeOutInSeconds, HttpClient.Version version, HttpClient.Redirect redirect, String[] sslProtocols, int daysInAdvance) {
        super(id, created, updated, MonitorType.SSL, name, description, cron, service, labels, documentation, active);
        this.endpoint = endpoint;
        this.method = RequestMethod.GET;
        this.publisher = null;
        this.timeOutInSeconds = timeOutInSeconds;
        this.version = version;
        this.redirect = redirect;
        sslParameters = new SSLParameters();
        sslParameters.setProtocols(sslProtocols);
        this.daysInAdvance = daysInAdvance;
    }


    public HttpClient getClient(KeyManager[] keyManagers, TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(
                keyManagers,
                trustManagers,
                new SecureRandom()
        );
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeOutInSeconds))
                .version(version)
                .followRedirects(redirect)
                .sslContext(sslContext)
                .sslParameters(sslParameters)
                .build();
    }

    public HttpRequest getRequest() {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint));
        return switch (method) {
            case GET -> builder.GET().build();
            case POST -> builder.POST(publisher).build();
            case PUT -> builder.PUT(publisher).build();
            case DELETE -> builder.DELETE().build();
            default -> builder.method(method.toString(), publisher).build();
        };
    }

    public String getEndpoint() {
        return endpoint;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public HttpRequest.BodyPublisher getPublisher() {
        return publisher;
    }

    public int getTimeOutInSeconds() {
        return timeOutInSeconds;
    }

    public HttpClient.Version getVersion() {
        return version;
    }

    public HttpClient.Redirect getRedirect() {
        return redirect;
    }

    public SSLParameters getSslParameters() {
        return sslParameters;
    }

    public int getDaysInAdvance() {
        return daysInAdvance;
    }

    public enum RequestMethod {
        GET, POST, PUT, DELETE, HEAD, PATCH, OPTIONS, TRACE;
    }

}
