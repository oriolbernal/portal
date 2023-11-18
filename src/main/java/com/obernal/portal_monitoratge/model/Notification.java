package com.obernal.portal_monitoratge.model;

import com.obernal.portal_monitoratge.model.alert.Alert;

public interface Notification {

    String getId();
    Alert alert();
    Alert insist();
    Alert recovery();
    void error();

}
