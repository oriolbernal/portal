package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.model.notification.ChannelType;
import com.obernal.portal_monitoratge.model.notification.impl.email.EmailNotifier;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static com.obernal.portal_monitoratge.model.monitor.MonitorState.*;
import static org.junit.jupiter.api.Assertions.*;

class MonitormonitorTest {

    @Test
    void create() {
        Monitor monitor = getMonitor(0);
        assertEquals(OK, monitor.state);
        assertEquals(0, monitor.counter);
    }

    @Test
    void ok() {
        Monitor monitor = getMonitor(0);
        System.out.println(monitor.state.toString());
        monitor.changeState(false);
        System.out.println(monitor.state.toString());
        assertEquals(OK, monitor.state);
        assertEquals(1, monitor.counter);
    }

    @Test
    void alert() {
        Monitor monitor = getMonitor(0);
        monitor.changeState(true);
        assertEquals(FIRST_ALERT, monitor.state);
        assertEquals(1, monitor.counter);
    }

    @Test
    void ok_alert() {
        Monitor monitor = getMonitor(0);
        monitor.changeState(false);
        monitor.changeState(true);
        assertEquals(FIRST_ALERT, monitor.state);
        assertEquals(1, monitor.counter);
    }

    @Test
    void alert_ok() {
        Monitor monitor = getMonitor(0);
        monitor.changeState(true);
        monitor.changeState(false);
        assertEquals(RECOVERY, monitor.state);
        assertEquals(1, monitor.counter);
    }

    @Test
    void ok_ok() {
        Monitor monitor = getMonitor(0);
        monitor.changeState(false);
        monitor.changeState(false);
        assertEquals(OK, monitor.state);
        assertEquals(2, monitor.counter);
    }

    @Test
    void alert_alert_noInsist() {
        Monitor monitor = getMonitor(3);
        monitor.changeState(true);
        monitor.changeState(true);
        assertEquals(ALERT, monitor.state);
        assertEquals(2, monitor.counter);
    }

    @Test
    void alert_alert_insist() {
        Monitor monitor = getMonitor(2);
        monitor.changeState(true);
        monitor.changeState(true);
        monitor.changeState(true);
        monitor.changeState(true);
        assertEquals(INSIST, monitor.state);
        assertEquals(4, monitor.counter);
    }

    private Monitor getMonitor(long insistAfter) {
        return new Monitor<MonitorContext, MonitorResult>(
                new MonitorContext(
                        new MonitorMetadata(
                                "name",
                                "description",
                                "cron",
                                "service",
                                new HashSet<>(),
                                "documentation",
                                ChannelType.EMAIL,
                                insistAfter
                        )) {
                    @Override
                    public MonitorType getType() {
                        return null;
                    }
                },
                new EmailNotifier<>(null, insistAfter, null, null, null)
        ) {
            @Override
            protected MonitorResult perform() throws Exception {
                return null;
            }

            @Override
            protected List<String> getAlerts(MonitorResult result) throws Exception {
                return null;
            }
        };
    }

}