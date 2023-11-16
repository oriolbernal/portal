package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.model.monitor.impl.clients.DbConnectionPool;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMetadata;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpMetadata;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMetadata;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MonitorFactoryTest {

    private MonitorFactory factory;

    @BeforeEach
    void setUp() {
        DbConnectionPool connectionPool = mock(DbConnectionPool.class);
        factory = new MonitorFactory(connectionPool);
    }


    @Test
    public void testCreate_shouldReturnCorrectMonitor() {
        testCreate(MonitorType.SSL, SslMetadata.class, SslMonitor.class);
        testCreate(MonitorType.DB, DbMetadata.class, DbMonitor.class);
        testCreate(MonitorType.HTTP, HttpMetadata.class, HttpMonitor.class);
    }

    public void testCreate(MonitorType type, Class<? extends MonitorMetadata> metadataClass, Class<?> monitorClass) {
        MonitorMetadata metadata = mock(metadataClass);
        when(metadata.getType()).thenReturn(type);
        Monitor<?, ?> monitor = factory.create(metadata);
        assertEquals(monitorClass, monitor.getClass());
    }

    @Test()
    public void testCreate_shouldThrowExceptionForNullType() {
        MonitorMetadata metadata = mock(MonitorMetadata.class);
        when(metadata.getType()).thenReturn(null);
        assertThrows(RuntimeException.class, () -> factory.create(metadata));
    }
}