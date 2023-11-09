package com.obernal.portal_monitoratge.model.monitor;

public interface Assert<R> {

    boolean isAlert(R result);

}
