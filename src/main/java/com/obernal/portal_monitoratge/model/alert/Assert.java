package com.obernal.portal_monitoratge.model.alert;

import com.obernal.portal_monitoratge.model.monitor.Result;

public interface Assert<R extends Result> {

    boolean isAlert(R result) throws Exception;
    String getMessage(R result) throws Exception;

}
