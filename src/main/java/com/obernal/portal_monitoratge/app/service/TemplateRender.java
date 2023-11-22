package com.obernal.portal_monitoratge.app.service;

import java.util.Map;

public interface TemplateRender<T, R> {

    R render(T template, Map<String, Object> context);

}
