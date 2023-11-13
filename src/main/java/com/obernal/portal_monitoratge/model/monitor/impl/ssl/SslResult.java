package com.obernal.portal_monitoratge.model.monitor.impl.ssl;

import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpResult;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class SslResult extends HttpResult {

    private final boolean isSSL;
    private final List<X509Certificate> certificates;
    private final String protocol;
    private final String cipherSuite;
    private final List<LocalDateTime> sslExpiration;
    private final List<String> cn;

    public SslResult(HttpResponse<String> response) {
        super(response);
        if (response.sslSession().isEmpty()) {
            throw new RuntimeException("Connection is not ssl!");
        }
        isSSL = response.sslSession().isPresent();
        certificates = loadSslCertificates(response.sslSession().get());
        protocol = response.sslSession().get().getProtocol();
        cipherSuite = response.sslSession().get().getCipherSuite();
        sslExpiration = loadSslCertificates(response.sslSession().get()).stream().map(this::getSSLExpirationDate).collect(Collectors.toList());
        cn = loadSslCertificates(response.sslSession().get()).stream().map(this::getCN).collect(Collectors.toList());
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

    private LocalDateTime getSSLExpirationDate(X509Certificate certificate) {
        return certificate.getNotAfter()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private String getCN(X509Certificate certificate) {
        return certificate.getSubjectX500Principal()
                .getName()
                .split("CN=")[1]
                .split(",")[0];
    }

    public LocalDateTime getSSLExpirationDate(int depth) {
        return sslExpiration.get(depth);
    }

    public String getCN(int depth) {
        return cn.get(depth);
    }

}
