package com.obernal.portal_monitoratge.model.alert.impl;

import com.obernal.portal_monitoratge.model.alert.Assert;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpResult;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class JsonAssert implements Assert<HttpResult> {

    private final String expected;
    private final boolean strictCompare;

    public JsonAssert(String expected, boolean strictCompare) {
        this.expected = expected;
        this.strictCompare = strictCompare;
    }

    @Override
    public boolean isAlert(HttpResult result) throws JSONException {
        return !JSONCompare.compareJSON(
                expected,
                result.getBody(),
                strictCompare ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT
        ).passed();
    }

    @Override
    public String getMessage(HttpResult result) throws Exception {
        return "Expected JSON in " + (strictCompare ? "strict" : "non-strict") + " compare mode does not coincide: expected(" + expected + ") and responseBody (" + result.getBody()+ ")";
    }

}
