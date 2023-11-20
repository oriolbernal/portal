package com.obernal.portal_monitoratge.model.monitor;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static com.obernal.portal_monitoratge.model.monitor.MonitorState.*;
import static org.junit.jupiter.api.Assertions.*;

class MonitorContextTest {

    @Test
    void create(){
        MonitorContext context = getContext(0);
        assertEquals(OK, context.state);
        assertEquals(0, context.counter);
    }

    @Test
    void ok(){
        MonitorContext context = getContext(0);
        System.out.println(context.state.toString());
        context.changeState(false);
        System.out.println(context.state.toString());
        assertEquals(OK, context.state);
        assertEquals(1, context.counter);
    }

    @Test
    void alert(){
        MonitorContext context = getContext(0);
        context.changeState(true);
        assertEquals(FIRST_ALERT, context.state);
        assertEquals(1, context.counter);
    }

    @Test
    void ok_alert(){
        MonitorContext context = getContext(0);
        context.changeState(false);
        context.changeState(true);
        assertEquals(FIRST_ALERT, context.state);
        assertEquals(1, context.counter);
    }

    @Test
    void alert_ok(){
        MonitorContext context = getContext(0);
        context.changeState(true);
        context.changeState(false);
        assertEquals(RECOVERY, context.state);
        assertEquals(1, context.counter);
    }

    @Test
    void ok_ok(){
        MonitorContext context = getContext(0);
        context.changeState(false);
        context.changeState(false);
        assertEquals(OK, context.state);
        assertEquals(2, context.counter);
    }

    @Test
    void alert_alert_noInsist(){
        MonitorContext context = getContext(3);
        context.changeState(true);
        context.changeState(true);
        assertEquals(ALERT, context.state);
        assertEquals(2, context.counter);
    }

    @Test
    void alert_alert_insist(){
        MonitorContext context = getContext(2);
        context.changeState(true);
        context.changeState(true);
        context.changeState(true);
        context.changeState(true);
        assertEquals(INSIST, context.state);
        assertEquals(4, context.counter);
    }

    private MonitorContext getContext(long insistAfter) {
        return new MonitorContext(new MonitorMetadata("name", "description", "cron", "service", new HashSet<>(), "documentation", insistAfter)) {
            @Override
            public MonitorType getType() {
                return null;
            }
        };
    }

}