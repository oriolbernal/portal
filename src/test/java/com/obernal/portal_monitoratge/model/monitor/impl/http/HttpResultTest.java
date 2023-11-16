package com.obernal.portal_monitoratge.model.monitor.impl.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpResultTest {

    HttpResponse<String> response;

    @BeforeEach
    public void setup() {
        response = mock(HttpResponse.class);
    }

    @Test
    public void create() {
        Map<String, List<String>> headersMap = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("value");
        headersMap.put("key", values);
        HttpHeaders headers = mock(HttpHeaders.class);
        when(headers.map()).thenReturn(headersMap);
        when(response.headers()).thenReturn(headers);
        when(response.body()).thenReturn("body");
        when(response.statusCode()).thenReturn(200);
        when(response.version()).thenReturn(HttpClient.Version.HTTP_2);
        URI uri = URI.create("https://example.com");
        when(response.uri()).thenReturn(uri);
        HttpResult result = new HttpResult(response);
        assertEquals(headersMap, result.getHeaders());
        assertEquals("body", result.getBody());
        assertEquals(200, result.getStatusCode());
        assertEquals("HTTP_2", result.getVersion());
        assertEquals("https://example.com", result.getUrl());
    }

}