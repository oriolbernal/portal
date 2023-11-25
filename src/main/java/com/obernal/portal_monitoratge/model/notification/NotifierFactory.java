package com.obernal.portal_monitoratge.model.notification;

import com.obernal.portal_monitoratge.app.clients.email.EmailClient;
import com.obernal.portal_monitoratge.app.clients.email.JiraClient;
import com.obernal.portal_monitoratge.model.monitor.MonitorContext;
import com.obernal.portal_monitoratge.model.notification.impl.email.EmailNotifier;
import com.obernal.portal_monitoratge.model.notification.impl.jira.JiraNotifier;

public class NotifierFactory {
    private final EmailClient emailClient;
    private final JiraClient jiraClient;

    public NotifierFactory(EmailClient emailClient, JiraClient jiraClient) {
        this.emailClient = emailClient;
        this.jiraClient = jiraClient;
    }

    public Notifier<?> create(MonitorContext context) {
        ChannelType channel = context.getMetadata().getChannel();
        if (channel == null) {
            throw new RuntimeException("Type does not exist: " + channel);
        }
        return switch (channel) {
            case EMAIL -> new EmailNotifier(emailClient, 0, null, null, null);
            case JIRA -> new JiraNotifier(jiraClient, 0, null, null, null);
        };
    }

}
