package com.obernal.portal_monitoratge.model.monitor.impl.http;

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

public class HttpMetadata extends MonitorMetadata {
    private final String endpoint;
    private final RequestMethod method;
    private final HttpRequest.BodyPublisher publisher;
    private final int timeOutInSeconds;
    private final HttpClient.Version version;
    private final HttpClient.Redirect redirect;
    private final SSLParameters sslParameters;
    private final boolean clientCertificate;
    private final Integer statusCode;
    private final String expectedBody;

    public HttpMetadata(String name, String description, String cron, String service, Set<String> labels, String documentation, String endpoint, RequestMethod method, String body, int timeOutInSeconds, HttpClient.Version version, HttpClient.Redirect redirect, String[] sslProtocols, boolean clientCertificate, Integer statusCode, String expectedBody) {
        super(name, description, cron, service, labels, documentation);
        this.endpoint = endpoint;
        this.method = method;
        this.publisher = body != null ? HttpRequest.BodyPublishers.ofString(body) : null;
        this.timeOutInSeconds = timeOutInSeconds;
        this.version = version;
        this.redirect = redirect;
        sslParameters = new SSLParameters();
        sslParameters.setProtocols(sslProtocols);
        this.clientCertificate = clientCertificate;
        this.statusCode = statusCode;
        this.expectedBody = expectedBody;
    }

    public HttpMetadata(String id, LocalDateTime created, LocalDateTime updated, String name, String description, String cron, String service, Set<String> labels, String documentation, boolean active, String endpoint, RequestMethod method, String body, int timeOutInSeconds, HttpClient.Version version, HttpClient.Redirect redirect, String[] sslProtocols, boolean clientCertificate, Integer statusCode, String expectedBody) {
        super(id, created, updated, name, description, cron, service, labels, documentation, active);
        this.endpoint = endpoint;
        this.method = method;
        this.publisher = body != null ? HttpRequest.BodyPublishers.ofString(body) : null;
        this.timeOutInSeconds = timeOutInSeconds;
        this.version = version;
        this.redirect = redirect;
        sslParameters = new SSLParameters();
        sslParameters.setProtocols(sslProtocols);
        this.clientCertificate = clientCertificate;
        this.statusCode = statusCode;
        this.expectedBody = expectedBody;
    }

    @Override
    public MonitorType getType() {
        return MonitorType.HTTP;
    }

    public HttpClient getClient(KeyManager[] keyManagers, TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, new SecureRandom());
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

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getExpectedBody() {
        return expectedBody;
    }

    public boolean isClientCertificate() {
        return clientCertificate;
    }

    public enum RequestMethod {
        GET, POST, PUT, DELETE, HEAD, PATCH, OPTIONS, TRACE;
    }

}
