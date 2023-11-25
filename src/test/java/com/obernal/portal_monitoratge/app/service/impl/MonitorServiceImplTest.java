package com.obernal.portal_monitoratge.app.service.impl;

import com.obernal.portal_monitoratge.model.monitor.MonitorResult;
import com.obernal.portal_monitoratge.model.monitor.*;
import com.obernal.portal_monitoratge.app.persistence.MonitorPersistence;
import com.obernal.portal_monitoratge.app.service.MonitorService;
import com.obernal.portal_monitoratge.app.service.exception.NotFoundException;
import com.obernal.portal_monitoratge.model.notification.ChannelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
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
        List<MonitorContext> monitors = Arrays.asList(
                new DummyContext("md1", "cron1"),
                new DummyContext("md2", "cron2")
        );
        when(persistence.findAll()).thenReturn(monitors.stream());
        Stream<MonitorContext> result = service.findAll();
        assertEquals(2, result.count());
    }

    @Test
    public void scheduleActiveMonitors() {
        List<MonitorContext> metadatas = Arrays.asList(
                new DummyContext("md1", "cron1"),
                new DummyContext("md2", "cron2", false)
        );
        when(persistence.findAll()).thenReturn(metadatas.stream());
        when(factory.create(any(DummyContext.class)))
                .thenAnswer(invocation -> {
                    DummyContext metadata = invocation.getArgument(0);
                    if ("md1".equals(metadata.getId())) {
                        return new DummyMonitor((DummyContext) metadatas.get(0));
                    } else {
                        return new DummyMonitor((DummyContext) metadatas.get(1));
                    }
                });
        long scheduledCount = service.scheduleActiveMonitors();
        assertEquals(1, scheduledCount);
    }

    @Test
    public void create() {
        var metadata = new DummyContext("m1", "new_cron");
        when(persistence.create(any(MonitorContext.class))).thenReturn(metadata);
        var created = service.create(metadata).getMetadata();
        assertNotNull(created.getId());
        assertNotNull(created.getCreated());
        assertNull(created.getUpdated());
        assertEquals("m1", created.getName());
    }

    @Test
    public void findByIdExistingMonitor() throws NotFoundException {
        var existingMonitor = new DummyContext("existing", "cron1");
        when(persistence.findById("existing")).thenReturn(Optional.of(existingMonitor));
        var foundMonitor = service.findById("existing");
        assertNotNull(foundMonitor);
        assertEquals("existing", foundMonitor.getMetadata().getName());
    }

    @Test
    public void error_if_findById_notFound() throws NotFoundException {
        when(persistence.findById("non_existing_id")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById("non_existing_id"));
    }

    @Test
    public void update() throws NotFoundException {
        var existing = new DummyContext("name", "cron1");
        when(persistence.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(persistence.update(any(MonitorContext.class))).thenReturn(existing);
        var metadata = new DummyContext("name", "new cron", false);
        var updated = service.update(existing.getId(), metadata);
        assertNotNull(updated.getMetadata().getUpdated());
        assertNotEquals(metadata.getMetadata().getUpdated(), updated.getMetadata().getUpdated());
        assertEquals(existing.getId(), updated.getId());
        assertEquals("new cron", updated.getMetadata().getCron());
    }

    @Test
    public void toggle_enable() throws NotFoundException {
        var existing = new DummyContext("existing", "cron1", false);
        when(persistence.findById("existing")).thenReturn(Optional.of(existing));
        when(persistence.update(any(MonitorContext.class))).thenReturn(existing);
        var toggled = service.toggle("existing");
        assertTrue(toggled.isActive());
    }

    @Test
    public void toggle_disable() throws NotFoundException {
        var existing = new DummyContext("existing", "cron1", true);
        when(persistence.findById("existing")).thenReturn(Optional.of(existing));
        when(persistence.update(any(MonitorContext.class))).thenReturn(existing);
        var toggled = service.toggle("existing");
        assertFalse(toggled.isActive());
    }

    @Test
    public void delete() throws NotFoundException {
        var existing = new DummyContext("existing", "cron1");
        when(persistence.findById("existing")).thenReturn(Optional.of(existing));
        when(persistence.deleteById("existing")).thenReturn(existing);
        var deletedMonitor = service.delete("existing");
        assertNotNull(deletedMonitor);
    }

    @Test
    public void run() throws NotFoundException {
        var existing = new DummyContext("existing", "cron1");
        var monitor = new DummyMonitor(existing);
        when(persistence.findById("existing")).thenReturn(Optional.of(existing));
        when(factory.create(any(DummyContext.class)))
                .thenAnswer(invocation -> {
                    DummyContext metadata = invocation.getArgument(0);
                    if ("existing".equals(metadata.getMetadata().getName())) {
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

class DummyMonitor extends Monitor<DummyContext, DummyResult> {

    public DummyMonitor(DummyContext metadata) {
        super(metadata, null);
    }

    @Override
    public DummyResult perform() {
        System.out.println("this is a dummy performance");
        return new DummyResult();
    }

    @Override
    public List<String> getAlerts(DummyResult result) {
        return new ArrayList<>();
    }
}

class DummyContext extends MonitorContext {
    private final double random;

    public DummyContext(String name, String cron) {
        super(new MonitorMetadata(
                name,
                "description",
                cron,
                "service",
                new HashSet<>(),
                "documentation",
                ChannelType.EMAIL,
                0
                //new EmailNotifier(null, 0, null, null, null)
        ));
        random = Math.random(); // greater than or equal to 0.0 and less than 1.0
    }

    public DummyContext(String name, String cron, boolean active) {
        super(
                new MonitorMetadata(
                        name,
                        "description",
                        cron,
                        "service",
                        new HashSet<>(),
                        "documentation",
                        ChannelType.EMAIL,
                        0
                        //new EmailNotifier(null, 0, null, null, null)
                ));
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

class DummyResult extends MonitorResult {

    protected DummyResult() {
        super();
    }
}
