package com.obernal.portal_monitoratge.app.service.impl;

import com.obernal.portal_monitoratge.model.monitor.*;
import com.obernal.portal_monitoratge.app.persistence.MonitorPersistence;
import com.obernal.portal_monitoratge.app.service.MonitorService;
import com.obernal.portal_monitoratge.app.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MonitorServiceImplTest {

    private MonitorService service;

    private MonitorPersistence persistence;
    private MonitorFactory factory;

    @BeforeEach
    void setUp() {
        persistence = mock(MonitorPersistence.class);
        SchedulerService scheduler = mock(SchedulerService.class);
        factory = mock(MonitorFactory.class);
        service = new MonitorServiceImpl(persistence, scheduler, factory);
    }

    @Test
    public void findAll() {
        List<MonitorMetadata> monitors = Arrays.asList(
                new DummyMetadata("md1", "cron1"),
                new DummyMetadata("md2", "cron2")
        );
        when(persistence.findAll()).thenReturn(monitors.stream());
        Stream<MonitorMetadata> result = service.findAll();
        assertEquals(2, result.count());
    }

    @Test
    public void scheduleActiveMonitors() {
        List<MonitorMetadata> metadatas = Arrays.asList(
                new DummyMetadata("md1", "cron1"),
                new DummyMetadata("md2", "cron2", false)
        );
        when(persistence.findAll()).thenReturn(metadatas.stream());
        when(factory.create(any(DummyMetadata.class)))
                .thenAnswer(invocation -> {
                    DummyMetadata metadata = invocation.getArgument(0);
                    if ("md1".equals(metadata.getId())) {
                        return new DummyMonitor((DummyMetadata) metadatas.get(0));
                    } else {
                        return new DummyMonitor((DummyMetadata) metadatas.get(1));
                    }
                });
        long scheduledCount = service.scheduleActiveMonitors();
        assertEquals(1, scheduledCount);
    }

    @Test
    public void create() {
        var metadata = new DummyMetadata("m1", "new_cron");
        when(persistence.create(any(MonitorMetadata.class))).thenReturn(metadata);
        var created = service.create(metadata);
        assertNotNull(created.getId());
        assertNotNull(created.getCreated());
        assertNull(created.getUpdated());
        assertEquals("m1", created.getName());
    }

    @Test
    public void findByIdExistingMonitor() throws NotFoundException {
        var existingMonitor = new DummyMetadata("existing", "cron1");
        when(persistence.findById("existing")).thenReturn(Optional.of(existingMonitor));
        var foundMonitor = service.findById("existing");
        assertNotNull(foundMonitor);
        assertEquals("existing", foundMonitor.getName());
    }

    @Test
    public void error_if_findById_notFound() throws NotFoundException {
        when(persistence.findById("non_existing_id")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById("non_existing_id"));
    }

    @Test
    public void update() throws NotFoundException {
        var existing = new DummyMetadata("name", "cron1");
        when(persistence.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(persistence.update(any(MonitorMetadata.class))).thenReturn(existing);
        var metadata = new DummyMetadata("name", "new cron", false);
        var updated = service.update(existing.getId(), metadata);
        assertNotNull(updated.getUpdated());
        assertNotEquals(metadata.getUpdated(), updated.getUpdated());
        assertEquals(existing.getId(), updated.getId());
        assertEquals("new cron", updated.getCron());
    }

    @Test
    public void toggle_enable() throws NotFoundException {
        var existing = new DummyMetadata("existing", "cron1", false);
        when(persistence.findById("existing")).thenReturn(Optional.of(existing));
        when(persistence.update(any(MonitorMetadata.class))).thenReturn(existing);
        var toggled = service.toggle("existing");
        assertTrue(toggled.isActive());
    }

    @Test
    public void toggle_disable() throws NotFoundException {
        var existing = new DummyMetadata("existing", "cron1", true);
        when(persistence.findById("existing")).thenReturn(Optional.of(existing));
        when(persistence.update(any(MonitorMetadata.class))).thenReturn(existing);
        var toggled = service.toggle("existing");
        assertFalse(toggled.isActive());
    }

    @Test
    public void delete() throws NotFoundException {
        var existing = new DummyMetadata("existing", "cron1");
        when(persistence.findById("existing")).thenReturn(Optional.of(existing));
        var deletedMonitor = service.delete("existing");
        assertNotNull(deletedMonitor);
    }

    @Test
    public void run() throws NotFoundException {
        var existing = new DummyMetadata("existing", "cron1");
        var monitor = new DummyMonitor(existing);
        when(persistence.findById("existing")).thenReturn(Optional.of(existing));
        when(factory.create(any(DummyMetadata.class)))
                .thenAnswer(invocation -> {
                    DummyMetadata metadata = invocation.getArgument(0);
                    if ("existing".equals(metadata.getName())) {
                        return monitor;
                    } else {
                        return null;
                    }
                });
        var result = service.run("existing");
        assertNotNull(result);
    }

    @Test()
    public void error_if_run_notFound() throws NotFoundException {
        when(persistence.findById("non_existing_id")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.run("non_existing_id"));
    }

}

class DummyMonitor extends Monitor<DummyMetadata, DummyResult> {

    public DummyMonitor(DummyMetadata metadata) {
        super(metadata);
    }

    @Override
    public DummyResult perform() {
        System.out.println("this is a dummy performance");
        return new DummyResult();
    }

    @Override
    public boolean isAlert(DummyResult result) {
        return metadata.getRandom() >= 0.5;
    }

}

class DummyMetadata extends MonitorMetadata {
    private final double random;

    public DummyMetadata(String name, String cron) {
        super(name, "description", cron, "service", new HashSet<>(), "documentation");
        random = Math.random(); // greater than or equal to 0.0 and less than 1.0
    }

    public DummyMetadata(String name, String cron, boolean active) {
        super(name, "description", cron, "service", new HashSet<>(), "documentation");
        if(!active) toggle();
        random = Math.random(); // greater than or equal to 0.0 and less than 1.0
    }

    public double getRandom() {
        return random;
    }

    @Override
    public MonitorType getType() {
        return null;
    }
}

class DummyResult extends Result {

}
