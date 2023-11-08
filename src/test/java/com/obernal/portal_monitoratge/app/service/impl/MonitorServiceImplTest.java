package com.obernal.portal_monitoratge.app.service.impl;

import com.obernal.portal_monitoratge.Execution;
import com.obernal.portal_monitoratge.Monitor;
import com.obernal.portal_monitoratge.app.persistence.MonitorPersistence;
import com.obernal.portal_monitoratge.app.service.MonitorService;
import com.obernal.portal_monitoratge.app.service.exception.NotFoundException;
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
        Monitor existingMonitor = new DummyMonitor("existing_id", "cron1", true);
        when(persistence.findById("existing_id")).thenReturn(Optional.of(existingMonitor));
        when(persistence.update(any(Monitor.class))).thenReturn(existingMonitor);
        Monitor updatedMonitor = service.update("existing_id", "new cron");
        assertNotNull(updatedMonitor);
        assertEquals("new cron", updatedMonitor.getCron());
    }

    @Test
    public void testToggle_Enable() throws NotFoundException {
        Monitor existingMonitor = new DummyMonitor("existing_id", "cron1", false);
        when(persistence.findById("existing_id")).thenReturn(Optional.of(existingMonitor));
        when(persistence.update(any(Monitor.class))).thenReturn(existingMonitor);
        Monitor toggledMonitor = service.toggle("existing_id");
        assertNotNull(toggledMonitor);
        assertTrue(toggledMonitor.isActive());
    }

    @Test
    public void testToggle_Disable() throws NotFoundException {
        Monitor existingMonitor = new DummyMonitor("existing_id", "cron1", true);
        when(persistence.findById("existing_id")).thenReturn(Optional.of(existingMonitor));
        when(persistence.update(any(Monitor.class))).thenReturn(existingMonitor);
        Monitor toggledMonitor = service.toggle("existing_id");
        assertNotNull(toggledMonitor);
        assertFalse(toggledMonitor.isActive());
    }

    @Test
    public void testDelete() throws NotFoundException {
        Monitor existingMonitor = new DummyMonitor("existing_id", "cron1", true);
        when(persistence.findById("existing_id")).thenReturn(Optional.of(existingMonitor));
        Monitor deletedMonitor = service.delete("existing_id");
        assertNotNull(deletedMonitor);
    }

    @Test
    public void testRun() throws NotFoundException {
        Monitor existingMonitor = new DummyMonitor("existing_id", "cron1", true);
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

class DummyMonitor implements Monitor {

    private final String id;
    private String cron;
    private boolean active;

    public DummyMonitor(String id, String cron, boolean active) {
        this.id = id;
        this.cron = cron;
        this.active = active;
    }

    @Override
    public Execution run() {
        return new DummyExecution(System.currentTimeMillis());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCron() {
        return cron;
    }

    @Override
    public void update(Object data) {
        this.cron = data.toString();
    }

    @Override
    public void toggle() {
        this.active = !active;
    }

    @Override
    public boolean isActive() {
        return active;
    }
}

class DummyExecution implements Execution {

    private final String id;
    private final float elapsedTimeInSeconds;

    public DummyExecution(long start) {
        this.id = UUID.randomUUID().toString();
        this.elapsedTimeInSeconds = (System.currentTimeMillis() - start) / 1000F;
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public float getElapsedTimeInSeconds() {
        return elapsedTimeInSeconds;
    }
}