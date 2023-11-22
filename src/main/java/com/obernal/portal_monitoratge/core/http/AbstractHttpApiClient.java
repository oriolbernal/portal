package com.obernal.portal_monitoratge.core.http;

import com.obernal.portal_monitoratge.core.http.exception.HttpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractHttpApiClient<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractHttpApiClient.class);

    private static final HttpResponse.BodyHandler<String> STRING_HANDLER = HttpResponse.BodyHandlers.ofString();
    private static final String GENERIC_ERROR = "Uncontrolled Error";

    protected final HttpClient client;
    protected final String endpoint;
    protected final Map<String, String> headers;

    protected AbstractHttpApiClient(HttpClient client, String endpoint, Map<String, String> headers) {
        this.client = client;
        this.endpoint = endpoint;
        this.headers = headers;
    }

    protected abstract T convert(String response);

    protected HttpRequest.Builder commonRequest(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(endpoint + path));
        headers.forEach(builder::header);
        return builder;
    }

    public CompletableFuture<T> get(String path) {
        var request = commonRequest(path)
                .GET()
                .build();
        logger.debug("Sending HTTP request: GET " + request.uri().toString());
        return send(request);
    }

    public CompletableFuture<T> post(String path, String data) {
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(data);
        var request = commonRequest(path)
                .POST(publisher)
                .build();
        logger.debug("Sending HTTP request: POST " + request.uri().toString() + " with data: " + data);
        return send(request);
    }

    public CompletableFuture<T> put(String path, String data) {
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(data);
        var request = commonRequest(path)
                .PUT(publisher)
                .build();
        logger.debug("Sending HTTP request: PUT " + request.uri().toString()+ " with data: " + data);
        return send(request);
    }

    public CompletableFuture<T> delete(String path) {
        var request = commonRequest(path)
                .DELETE()
                .build();
        logger.debug("Sending HTTP request: DELETE " + request.uri().toString());
        return send(request);
    }

    protected CompletableFuture<T> send(HttpRequest request) {
        return client.sendAsync(request, STRING_HANDLER)
                .thenApply(this::log)
                .thenApply(this::convert);
    }

    private String log(HttpResponse<String> response) {
        //logger.debug("Response statusCode is: " + response.statusCode());
        //logger.debug("Response body is: " + response.body());
        return switch (response.statusCode()) {
            case 200, 201 -> response.body();
            case 401 -> throw new HttpClientException(response.statusCode(), "Not authenticated");
            case 404 -> throw new HttpClientException(response.statusCode(), "Not found");
            case 429 -> throw new HttpClientException(response.statusCode(), "Too Many Requests");
            default -> throw new HttpClientException(response.statusCode(), GENERIC_ERROR + response.body());
        };
    }

}
