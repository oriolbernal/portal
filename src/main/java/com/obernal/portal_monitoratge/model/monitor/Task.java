package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.model.Execution;

public interface Task<R> {

    String getId();
    Execution<R> run();

}
