package com.obernal.portal_monitoratge;

public interface Notification {

    String getId();
    Alert alert();
    Alert insist();
    Alert recovery();

}
