package com.obernal.portal_monitoratge.model.alert;

import com.obernal.portal_monitoratge.app.service.AlertService;
import com.obernal.portal_monitoratge.model.monitor.MonitorResult;

import java.util.List;

public class Alert {
    protected List<String> messages;
    protected String generatedEvent;

    public Alert(List<String> messages) {
        this.messages = messages;
        generatedEvent = null;
    }

}
