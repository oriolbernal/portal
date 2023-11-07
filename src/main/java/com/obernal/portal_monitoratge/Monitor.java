package com.obernal.portal_monitoratge;

public interface Monitor {

    Execution run();
    String getId();
    String getCron();
    void update(Object data);
    void toggle();
    boolean isActive();

}
