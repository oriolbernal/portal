package com.obernal.portal_monitoratge.app.service.impl;

import com.obernal.portal_monitoratge.app.clients.email.EmailClient;
import com.obernal.portal_monitoratge.app.clients.email.JiraClient;
import com.obernal.portal_monitoratge.app.service.AlertService;
import com.obernal.portal_monitoratge.model.alert.Alert;
import com.obernal.portal_monitoratge.model.monitor.MonitorContext;
import com.obernal.portal_monitoratge.model.monitor.MonitorResult;

import java.util.List;

public class AlertServiceImpl implements AlertService {

    private final EmailClient emailClient;
    private final JiraClient jiraClient;

    public AlertServiceImpl(EmailClient emailClient, JiraClient jiraClient) {
        this.emailClient = emailClient;
        this.jiraClient = jiraClient;
    }

    @Override
    public Alert alert(MonitorContext context, MonitorResult result, List<String> messages) {
        return null;
    }

    @Override
    public Alert insist(MonitorContext context, MonitorResult result, List<String> messages) {
        return null;
    }

    @Override
    public Alert recover(MonitorContext context, MonitorResult result) {
        return null;
    }
}
