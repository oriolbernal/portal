package com.obernal.portal_monitoratge.model.alert.impl;

import com.obernal.portal_monitoratge.model.alert.Assert;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbResult;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpResult;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.ArrayList;

public class MinMaxAssert implements Assert<DbResult> {

    private final Long min;
    private final Long max;

    public MinMaxAssert(Long min, Long max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isAlert(DbResult result) {
        String firstKey = new ArrayList<>(result.getTable().get(0).keySet()).get(0);
        String firstValue = result.getTable().get(0).get(firstKey);
        long number = Long.parseLong(firstValue);
        if (max == null) return number < min;
        else if (min == null) return number > max;
        else return number > max || number < min;
    }

    @Override
    public String getMessage(DbResult result) {
        String firstKey = new ArrayList<>(result.getTable().get(0).keySet()).get(0);
        String firstValue = result.getTable().get(0).get(firstKey);
        long number = Long.parseLong(firstValue);
        return "number (" + number + ") not between range (" + min + ", " + max + ")";
    }
    
}
