package com.obernal.portal_monitoratge.model.alert.impl.email;

import com.obernal.portal_monitoratge.model.alert.AlertContext;
import com.obernal.portal_monitoratge.model.alert.AlertType;
import com.obernal.portal_monitoratge.model.monitor.MonitorState;

public class EmailContext extends AlertContext {

    private String to;
    private String cc;
    private String bcc;

    public EmailContext(MonitorState state, long counter, String to, String cc, String bcc) {
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
    }

    @Override
    public AlertType getType() {
        return AlertType.EMAIL;
    }
}
