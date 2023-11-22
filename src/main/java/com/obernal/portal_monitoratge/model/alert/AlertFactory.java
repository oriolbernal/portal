package com.obernal.portal_monitoratge.model.alert;

import com.obernal.portal_monitoratge.app.clients.DbConnectionPool;
import com.obernal.portal_monitoratge.app.service.AlertService;
import com.obernal.portal_monitoratge.model.alert.impl.email.EmailContext;
import com.obernal.portal_monitoratge.model.alert.impl.jira.JiraContext;
import com.obernal.portal_monitoratge.model.monitor.Monitor;
import com.obernal.portal_monitoratge.model.monitor.MonitorContext;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbContext;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpContext;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslContext;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMonitor;

import java.util.ArrayList;
import java.util.Properties;

public class AlertFactory {

    private final AlertService alertService;

    public AlertFactory(AlertService alertService) {
        this.alertService = alertService;
    }

    public Alert create(AlertContext context) {
        if (context.getType() == null) {
            throw new RuntimeException("Type does not exist: " + context.getType());
        }
        return switch (context.getType()) {
            case EMAIL -> create((EmailContext) context);
            case JIRA -> create((JiraContext) context);
        };
    }

    private Alert create(EmailContext context) {
        return new Alert(new ArrayList<>());
    }

    private Alert create(JiraContext context) {
        return new Alert(new ArrayList<>());
    }

}
