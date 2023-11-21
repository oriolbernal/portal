package com.obernal.portal_monitoratge.model.monitor;

import java.util.HashMap;
import java.util.Map;

public abstract class MonitorResult {
    protected final Map<String, Object> data;
    public MonitorResult() {
        this.data = new HashMap<>();
    }

}
