package com.obernal.portal_monitoratge.model.notification;

import com.obernal.portal_monitoratge.model.alert.Alert;
import com.obernal.portal_monitoratge.model.monitor.MonitorResult;

import java.util.List;

public abstract class Notifier<R extends MonitorResult> {

    private final long insistAfter;

    protected Notifier(long insistAfter) {
        this.insistAfter = insistAfter;
    }

    public abstract ChannelType getChannel();

    public abstract Alert alert(R result, List<String> messages);
    public abstract Alert insist(R result);
    public abstract Alert recover(R result);

    public long getInsistAfter() {
        return insistAfter;
    }

}
