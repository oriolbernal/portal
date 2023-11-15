package com.obernal.portal_monitoratge.model.monitor;

import java.util.HashMap;
import java.util.Map;

public abstract class Result {
    protected Map<String, Object> data;
    public Result() {
        this.data = new HashMap<>();
    }

    public abstract Object get(String key);

}
