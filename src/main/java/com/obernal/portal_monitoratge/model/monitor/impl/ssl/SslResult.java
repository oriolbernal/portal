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

    public SslResult(HttpResponse<String> response) {
        super(response);
        if (response.sslSession().isEmpty()) {
            throw new RuntimeException("Connection is not ssl!");
        }
        data.put("isSSL", response.sslSession().isPresent());
        data.put("certificates", loadSslCertificates(response.sslSession().get()));
        data.put("protocol", response.sslSession().get().getProtocol());
        data.put("cipherSuite", response.sslSession().get().getCipherSuite());
        data.put("sslExpiration", loadSslExpiration(response.sslSession().get()));
        data.put("cn", loadCNs(response.sslSession().get()));
    }

    private List<LocalDateTime> loadSslExpiration(SSLSession session) {
        return loadSslCertificates(session)
                .stream()
                .map(this::getSSLExpirationDate)
                .collect(Collectors.toList());
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

    private List<String> loadCNs(SSLSession session) {
        return loadSslCertificates(session)
                .stream()
                .map(this::getCN)
                .collect(Collectors.toList());
    }

    private String getCN(X509Certificate certificate) {
        return certificate.getSubjectX500Principal()
                .getName()
                .split("CN=")[1]
                .split(",")[0];
    }

    @SuppressWarnings("unchecked")
    public LocalDateTime getSSLExpirationDate(int depth) {
        List<LocalDateTime> sslExpirations = (List<LocalDateTime>) data.get("sslExpiration");
        if(depth < sslExpirations.size()) {
            return sslExpirations.get(depth);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public String getCN(int depth) {
        List<String> cns = (List<String>) data.get("cn");
        if(depth < cns.size()) {
            return cns.get(depth);
        }
        return null;
    }

}
