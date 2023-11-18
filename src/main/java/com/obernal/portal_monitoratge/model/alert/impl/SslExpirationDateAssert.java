package com.obernal.portal_monitoratge.model.alert.impl;

import com.obernal.portal_monitoratge.model.alert.Assert;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslResult;

import java.time.LocalDateTime;

public class SslExpirationDateAssert implements Assert<SslResult> {

    private final int daysInAdvance;

    public SslExpirationDateAssert(int daysInAdvance) {
        this.daysInAdvance = daysInAdvance;
    }

    @Override
    public boolean isAlert(SslResult result) {
        LocalDateTime certificateExpiration = result.getSSLExpirationDate(0);
        LocalDateTime alertDate = LocalDateTime.now().plusDays(daysInAdvance);
        return alertDate.isAfter(certificateExpiration);
    }

    @Override
    public String getMessage(SslResult result) throws Exception {
        return "SSL Certificate expires in les than " + daysInAdvance + " days: " + result.getSSLExpirationDate(0);
    }

    public int getDaysInAdvance() {
        return daysInAdvance;
    }
}
