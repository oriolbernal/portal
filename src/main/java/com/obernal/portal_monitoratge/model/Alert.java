package com.obernal.portal_monitoratge.model;

import java.util.List;

public class Alert {
    protected final List<String> messages;
    public Alert(List<String> messages) {
        this.messages = messages;
    }

}
