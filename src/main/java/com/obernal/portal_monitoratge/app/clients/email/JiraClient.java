package com.obernal.portal_monitoratge.app.clients.email;

public interface JiraClient {

    String createIssue(String issue);
    void insist(String key, String message);

}
