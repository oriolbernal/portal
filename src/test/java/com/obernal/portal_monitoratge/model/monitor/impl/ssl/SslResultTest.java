package com.obernal.portal_monitoratge.model.monitor.impl.ssl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.auth.x500.X500Principal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SslResultTest {

    HttpResponse<String> response;

    @BeforeEach
    public void setup() {
        response = mock(HttpResponse.class);
    }

    @Test
    public void create() throws SSLPeerUnverifiedException, ParseException {
        SSLSession session = mock(SSLSession.class);
        X509Certificate[] certificates = new X509Certificate[1];
        certificates[0] = mock(X509Certificate.class);
        when(certificates[0].getNotAfter()).thenReturn(new SimpleDateFormat("yyyy-MM-dd").parse("2023-10-28"));
        X500Principal principal = mock(X500Principal.class);
        when(principal.getName()).thenReturn("CN=aa,bb");
        when(certificates[0].getSubjectX500Principal()).thenReturn(principal);
        when(session.getPeerCertificates()).thenReturn(certificates);
        when(response.sslSession()).thenReturn(Optional.of(session));

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
        SslResult result = new SslResult(response);
        assertEquals(headersMap, result.getHeaders());
        assertEquals("body", result.getBody());
        assertEquals(200, result.getStatusCode());
        assertEquals("HTTP_2", result.getVersion());
        assertEquals("https://example.com", result.getUrl());
        when(response.statusCode()).thenReturn(200);


        assertEquals(2023, result.getSSLExpirationDate(0).getYear());
        assertEquals(Month.OCTOBER, result.getSSLExpirationDate(0).getMonth());
        assertEquals(28, result.getSSLExpirationDate(0).getDayOfMonth());
        assertEquals("aa", result.getCN(0));
        assertNull(result.getSSLExpirationDate(1));
        assertNull(result.getCN(1));
    }

}