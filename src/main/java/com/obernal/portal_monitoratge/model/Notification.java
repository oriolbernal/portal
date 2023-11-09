package com.obernal.portal_monitoratge.model;

public interface Notification {

    String getId();
    Alert alert();
    Alert insist();
    Alert recovery();
    void error();

}
