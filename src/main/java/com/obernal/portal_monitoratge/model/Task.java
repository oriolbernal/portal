package com.obernal.portal_monitoratge.model;

public interface Task<R> {

    String getId();
    R run();

}
