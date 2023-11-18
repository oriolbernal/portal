package com.obernal.portal_monitoratge.model.monitor.impl.http;

import com.obernal.portal_monitoratge.model.monitor.MonitorContext;
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

public class HttpContext extends MonitorContext {
    private final String endpoint;
    private final RequestMethod method;
    private final HttpRequest.BodyPublisher publisher;
    private final int timeOutInSeconds;
    private final HttpClient.Version version;
    private final HttpClient.Redirect redirect;
    private final SSLParameters sslParameters;
    private final boolean clientCertificate;
    private final Integer expectedStatusCode;
    private final String expectedBody;
    private final boolean strictCompare;

    public HttpContext(MonitorMetadata context, String endpoint, RequestMethod method, String body, int timeOutInSeconds, HttpClient.Version version, HttpClient.Redirect redirect, String[] sslProtocols, boolean clientCertificate, Integer expectedStatusCode, String expectedBody, boolean strictCompare) {
        super(context);
        this.endpoint = endpoint;
        this.method = method;
        this.publisher = body != null ? HttpRequest.BodyPublishers.ofString(body) : null;
        this.timeOutInSeconds = timeOutInSeconds;
        this.version = version;
        this.redirect = redirect;
        sslParameters = new SSLParameters();
        sslParameters.setProtocols(sslProtocols);
        this.clientCertificate = clientCertificate;
        this.expectedStatusCode = expectedStatusCode;
        this.expectedBody = expectedBody;
        this.strictCompare = strictCompare;
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

    public Integer getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public String getExpectedBody() {
        return expectedBody;
    }

    public boolean isClientCertificate() {
        return clientCertificate;
    }

    public boolean isStrictCompare() {
        return strictCompare;
    }

    public enum RequestMethod {
        GET, POST, PUT, DELETE, HEAD, PATCH, OPTIONS, TRACE
    }

}
