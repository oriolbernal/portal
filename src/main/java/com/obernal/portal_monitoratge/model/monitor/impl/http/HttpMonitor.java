package com.obernal.portal_monitoratge.model.monitor.impl.http;

import com.obernal.portal_monitoratge.model.monitor.Monitor;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpMonitor extends Monitor<HttpMetadata, HttpResult> {

    public HttpMonitor(HttpMetadata metadata) {
        super(metadata);
    }

    @Override
    protected HttpResult perform() throws Exception {
        HttpClient client = metadata.getClient();
        HttpRequest request = metadata.getRequest();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new HttpResult(response);
        } catch (HttpConnectTimeoutException e) {
            throw new RuntimeException("Connection Timeout: " + e.getMessage());
        } catch (ConnectException e) {
            throw new RuntimeException("ConnectException (Unresolved host?): " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Nonexistent SSLProtocol: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("I/O error occurred when sending or receiving: " + e.getMessage());
        }
    }

    @Override
    protected boolean isAlert(HttpResult result) {
        if(metadata.getStatusCode() != null && result.getStatusCode() != metadata.getStatusCode()) {
            return true;
        }
        return metadata.getExpectedBody() != null && !result.getBody().contains(metadata.getExpectedBody());
    }

}
