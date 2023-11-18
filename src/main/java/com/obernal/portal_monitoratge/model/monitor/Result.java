package com.obernal.portal_monitoratge.model.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Result {
    protected final Map<String, Object> data;
    public Result() {
        this.data = new HashMap<>();
    }

   // public abstract boolean isAlert();
    //public abstract List<String> getAlertMessages();
}
