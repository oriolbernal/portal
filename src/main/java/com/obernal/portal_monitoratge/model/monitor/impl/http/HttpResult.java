package com.obernal.portal_monitoratge.model.monitor.impl.http;

import java.net.http.HttpResponse;
import java.util.*;

public class HttpResult {

    private final Map<String, List<String>> headers;
    private final String body;
    private final int statusCode;
    private final String url;
    private final String version;

    public HttpResult(HttpResponse<String> response) {
        headers = response.headers().map();
        body = response.body();
        statusCode = response.statusCode();
        version = response.version().toString();
        url = response.uri().toString();
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getVersion() {
        return version;
    }

    public String getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getUrl() {
        return url;
    }
}
