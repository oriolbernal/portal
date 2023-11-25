package com.obernal.portal_monitoratge.app.service;

import com.obernal.portal_monitoratge.model.alert.Alert;
import com.obernal.portal_monitoratge.model.monitor.MonitorResult;
import com.obernal.portal_monitoratge.model.monitor.MonitorContext;

import java.util.List;

public interface AlertService {

    Alert alert(MonitorContext context, MonitorResult result, List<String> messages);
    Alert insist(MonitorContext context, MonitorResult result, List<String> messages);
    Alert recover(MonitorContext context, MonitorResult result);

}
