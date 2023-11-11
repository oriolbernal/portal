package com.obernal.portal_monitoratge.model;

public interface Report<R> {
    float getElapsedTimeInSeconds();
    R getData();

}