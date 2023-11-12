package com.obernal.portal_monitoratge.app.service.impl;

import com.obernal.portal_monitoratge.model.Execution;
import com.obernal.portal_monitoratge.model.monitor.Monitor;
import com.obernal.portal_monitoratge.app.persistence.MonitorPersistence;
import com.obernal.portal_monitoratge.app.service.MonitorService;
import com.obernal.portal_monitoratge.app.service.exception.NotFoundException;
import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MonitorServiceImplTest {

    private MonitorService service;

    private MonitorPersistence persistence;
    private SchedulerService scheduler;

    @BeforeEach
    void setUp() {
        persistence = mock(MonitorPersistence.class);
        scheduler = mock(SchedulerService.class);
        service = new MonitorServiceImpl(persistence, scheduler);
    }

    @Test
    public void testFindAll() {
        List<Monitor> monitors = Arrays.asList(
                new DummyMonitor("id1", "cron1", true),
                new DummyMonitor("id2", "cron2", false)
        );
        when(persistence.findAll()).thenReturn(monitors.stream());
        Stream<Monitor> result = service.findAll();
        assertEquals(2, result.count());
    }

    @Test
    public void testScheduleActiveMonitors() {
        List<Monitor> monitors = Arrays.asList(
                new DummyMonitor("id1", "cron1", true),
                new DummyMonitor("id2", "cron2", false)
        );
        when(persistence.findAll()).thenReturn(monitors.stream());
        long scheduledCount = service.scheduleActiveMonitors();
        assertEquals(1, scheduledCount);
    }

    @Test
    public void testCreate() {
        Monitor newMonitor = new DummyMonitor("new_id", "new_cron", true);
        when(persistence.create(any(Monitor.class))).thenReturn(newMonitor);
        Monitor createdMonitor = service.create(newMonitor);
        assertNotNull(createdMonitor);
        assertEquals("new_id", createdMonitor.getId());
    }

    @Test
    public void testFindByIdExistingMonitor() throws NotFoundException {
        Monitor existingMonitor = new DummyMonitor("existing_id", "cron1", true);
        when(persistence.findById("existing_id")).thenReturn(Optional.of(existingMonitor));
        Monitor foundMonitor = service.findById("existing_id");
        assertNotNull(foundMonitor);
        assertEquals("existing_id", foundMonitor.getId());
    }

    @Test
    public void testFindByIdNonExistingMonitor() throws NotFoundException {
        when(persistence.findById("non_existing_id")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById("non_existing_id"));
    }

    @Test
    public void testUpdate() throws NotFoundException {
        var existingMonitor = new DummyMonitor("existing_id", "cron1", true);
        when(persistence.findById("existing_id")).thenReturn(Optional.of(existingMonitor));
        when(persistence.update(any(Monitor.class))).thenReturn(existingMonitor);
        DummyMonitorMetadata metadata = new DummyMonitorMetadata("id", "new cron", false);
        var updatedMonitor = service.update("existing_id", metadata);
        assertNotNull(updatedMonitor);
        assertEquals("new cron", updatedMonitor.getCron());
    }

    @Test
    public void testToggle_Enable() throws NotFoundException {
        var existingMonitor = new DummyMonitor("existing_id", "cron1", false);
        when(persistence.findById("existing_id")).thenReturn(Optional.of(existingMonitor));
        when(persistence.update(any(Monitor.class))).thenReturn(existingMonitor);
        var toggledMonitor = service.toggle("existing_id");
        assertNotNull(toggledMonitor);
        assertTrue(toggledMonitor.isActive());
    }

    @Test
    public void testToggle_Disable() throws NotFoundException {
        DummyMonitor existingMonitor = new DummyMonitor("existing_id", "cron1", true);
        when(persistence.findById("existing_id")).thenReturn(Optional.of(existingMonitor));
        when(persistence.update(any(Monitor.class))).thenReturn(existingMonitor);
        Monitor toggledMonitor = service.toggle("existing_id");
        assertNotNull(toggledMonitor);
        assertFalse(toggledMonitor.isActive());
    }

    @Test
    public void testDelete() throws NotFoundException {
        DummyMonitor existingMonitor = new DummyMonitor("existing_id", "cron1", true);
        when(persistence.findById("existing_id")).thenReturn(Optional.of(existingMonitor));
        Monitor deletedMonitor = service.delete("existing_id");
        assertNotNull(deletedMonitor);
    }

    @Test
    public void testRun() throws NotFoundException {
        DummyMonitor existingMonitor = new DummyMonitor("existing_id", "cron1", true);
        when(persistence.findById("existing_id")).thenReturn(Optional.of(existingMonitor));
        Execution result = service.run("existing_id");
        assertNotNull(result);
    }

    @Test()
    public void testRun_NotFound() throws NotFoundException {
        when(persistence.findById("non_existing_id")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.run("non_existing_id"));
    }

}

class DummyMonitor extends Monitor<DummyMonitorMetadata, Object> {

    public DummyMonitor(String id, String cron, boolean alert) {
        super(new DummyMonitorMetadata(id, cron, alert));
    }

    @Override
    public Object perform() {
        System.out.println("this is a dummy performance");
        return metadata.getRandom();
    }

    @Override
    public boolean isAlert(Object result) {
        return metadata.getRandom() >= 0.5;
    }

}

class DummyMonitorMetadata extends MonitorMetadata {
    private final double random;

    public DummyMonitorMetadata(String id, String cron, boolean active) {
        super(id, null, null, null, "name", "description", cron, "service", new HashSet<>(), "documentation", active);
        random = Math.random(); // greater than or equal to 0.0 and less than 1.0
    }

    public double getRandom() {
        return random;
    }
}
