package com.obernal.portal_monitoratge.model.monitor.impl.http;

import com.obernal.portal_monitoratge.model.monitor.Result;

import java.net.http.HttpResponse;
import java.util.*;

public class HttpResult extends Result {

    public HttpResult(HttpResponse<String> response) {
        super();
        data.put("headers", response.headers().map());
        data.put("body", response.body());
        data.put("statusCode", response.statusCode());
        data.put("version", response.version().toString());
        data.put("url", response.uri().toString());
    }

    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getHeaders() {
        return (Map<String, List<String>>) data.get("headers");
    }

    public String getBody() {
        return data.get("body").toString();
    }

    public int getStatusCode() {
        return (int) data.get("statusCode");
    }

    public String getVersion() {
        return data.get("version").toString();
    }

    public String getUrl() {
        return data.get("url").toString();
    }

}
