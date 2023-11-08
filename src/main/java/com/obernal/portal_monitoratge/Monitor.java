package com.obernal.portal_monitoratge;

public interface Monitor {

    String getId();
    Execution run();
    String getCron();
    void update(Object data);
    void toggle();
    boolean isActive();

}
