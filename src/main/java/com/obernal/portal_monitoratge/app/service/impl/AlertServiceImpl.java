package com.obernal.portal_monitoratge.app.service.impl;

import com.obernal.portal_monitoratge.model.alert.Alert;
import com.obernal.portal_monitoratge.model.monitor.MonitorContext;
import com.obernal.portal_monitoratge.model.monitor.MonitorResult;

import java.util.List;

public interface AlertServiceImpl {

    Alert alert(MonitorContext context, MonitorResult result, List<String> messages);
    Alert insist(MonitorContext context, MonitorResult result, List<String> messages);
    Alert recover(MonitorContext context, MonitorResult result);

}
