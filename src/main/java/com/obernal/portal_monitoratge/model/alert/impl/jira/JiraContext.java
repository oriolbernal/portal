package com.obernal.portal_monitoratge.model.alert.impl.jira;

import com.obernal.portal_monitoratge.model.alert.AlertContext;
import com.obernal.portal_monitoratge.model.alert.AlertType;

public class JiraContext extends AlertContext {

    private String assignee;
    private String relatedUsers;
    private String watchers;

    public JiraContext(String assignee, String relatedUsers, String watchers) {
        this.assignee = assignee;
        this.relatedUsers = relatedUsers;
        this.watchers = watchers;
    }

    @Override
    public AlertType getType() {
        return AlertType.JIRA;
    }
}
