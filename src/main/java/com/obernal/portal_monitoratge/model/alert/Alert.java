package com.obernal.portal_monitoratge.model.alert;

import java.util.List;

public class Alert {
    protected List<String> messages;
    protected String generatedEvent;

    public Alert(List<String> messages) {
        this.messages = messages;
        generatedEvent = null;
    }

}
