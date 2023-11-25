package com.obernal.portal_monitoratge.model.notification.impl.jira;

import com.obernal.portal_monitoratge.app.clients.email.JiraClient;
import com.obernal.portal_monitoratge.model.alert.Alert;
import com.obernal.portal_monitoratge.model.monitor.MonitorResult;
import com.obernal.portal_monitoratge.model.notification.ChannelType;
import com.obernal.portal_monitoratge.model.notification.Notifier;

import java.util.ArrayList;
import java.util.List;

public class JiraNotifier<R extends MonitorResult> extends Notifier<R> {

    private final JiraClient jiraClient;
    private String assignee;
    private String[] relatedUsers;
    private String[] watchers;

    public JiraNotifier(JiraClient jiraClient, long insistAfter, String assignee, String[] relatedUsers, String[] watchers) {
        super(insistAfter);
        this.jiraClient = jiraClient;
        this.assignee = assignee;
        this.relatedUsers = relatedUsers;
        this.watchers = watchers;
    }

    @Override
    public ChannelType getChannel() {
        return ChannelType.JIRA;
    }

    @Override
    public Alert alert(R result, List<String> messages) {
        return new Alert(messages);
    }

    @Override
    public Alert insist(R result) {
        return new Alert(new ArrayList<>());
    }

    @Override
    public Alert recover(R result) {
        return null;
    }

}
