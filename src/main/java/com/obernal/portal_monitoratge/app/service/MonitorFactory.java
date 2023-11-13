package com.obernal.portal_monitoratge.app.service;

import com.obernal.portal_monitoratge.model.monitor.Monitor;
import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;

public interface MonitorFactory {

    Monitor<?, ?> create(MonitorMetadata metadata);

}
