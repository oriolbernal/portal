package com.obernal.portal_monitoratge.model.monitor;

import com.obernal.portal_monitoratge.app.clients.DbConnectionPool;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbContext;
import com.obernal.portal_monitoratge.model.monitor.impl.db.DbMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpContext;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslContext;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MonitorFactoryTest {

    private MonitorFactory factory;

    @BeforeEach
    void setUp() {
        Properties properties = mock(Properties.class);
        DbConnectionPool connectionPool = mock(DbConnectionPool.class);
        factory = new MonitorFactory(properties, null, connectionPool);
    }

    @Test
    public void testCreate_shouldReturnCorrectMonitor() {
        testCreate(MonitorType.SSL, SslContext.class, SslMonitor.class);
        testCreate(MonitorType.DB, DbContext.class, DbMonitor.class);
        testCreate(MonitorType.HTTP, HttpContext.class, HttpMonitor.class);
    }

    public void testCreate(MonitorType type, Class<? extends MonitorContext> contextClass, Class<?> monitorClass) {
        MonitorContext context = mock(contextClass);
        when(context.getType()).thenReturn(type);
        Monitor<?, ?> monitor = factory.create(context);
        assertEquals(monitorClass, monitor.getClass());
    }

    @Test()
    public void testCreate_shouldThrowExceptionForNullType() {
        MonitorContext context = mock(MonitorContext.class);
        when(context.getType()).thenReturn(null);
        assertThrows(RuntimeException.class, () -> factory.create(context));
    }

}