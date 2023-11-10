package com.obernal.portal_monitoratge.model.monitor.impl;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class SslResult {

    private final Map<String, List<String>> headers;
    private final String body;
    private final int statusCode;
    private final String url;
    private final String version;
    private final boolean isSSL;
    private final List<X509Certificate> certificates;
    private final String protocol;
    private final String cipherSuite;

    public SslResult(HttpResponse<String> response) {
        headers = response.headers().map();
        body = response.body();
        statusCode = response.statusCode();
        version = response.version().toString();
        url = response.uri().toString();
        isSSL = response.sslSession().isPresent();
        if (isSSL) {
            certificates = loadSslCertificates(response.sslSession().get());
            protocol = response.sslSession().get().getProtocol();
            cipherSuite = response.sslSession().get().getCipherSuite();
        } else {
            certificates = new ArrayList<>();
            protocol = null;
            cipherSuite = null;
        }
    }

    private List<X509Certificate> loadSslCertificates(SSLSession session) {
        try {
            return Arrays.stream(session.getPeerCertificates())
                    .map(certificate -> (X509Certificate) certificate)
                    .collect(Collectors.toList());
        } catch (SSLPeerUnverifiedException e) {
            return new ArrayList<>();
        }
    }

    public Optional<LocalDateTime> getSSLExpirationDate(int depth) {
        if (!isSSL || certificates.size() <= depth) {
            return Optional.empty();
        }
        X509Certificate certificate = certificates.get(depth);
        return Optional.of(getSSLExpirationDate(certificate));
    }

    private LocalDateTime getSSLExpirationDate(X509Certificate certificate) {
        return certificate.getNotAfter()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public String getCN(int depth) {
        String dn = certificates.get(depth).getSubjectX500Principal().getName();
        return dn.split("CN=")[1].split(",")[0];
    }

}
