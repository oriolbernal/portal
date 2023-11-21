package com.obernal.portal_monitoratge.model.alert;

import java.util.List;

public abstract class Alert {
    protected final List<String> messages;
    public Alert(List<String> messages) {
        this.messages = messages;
    }

    public abstract AlertType getType();

}
