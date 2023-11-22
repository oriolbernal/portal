package com.obernal.portal_monitoratge.core.rest;

import com.obernal.portal_monitoratge.core.http.AbstractHttpApiClient;

import java.net.http.HttpClient;
import java.util.HashMap;

public abstract class RestApiClient<T> extends AbstractHttpApiClient<T> {

    protected RestApiClient(HttpClient client, String endpoint, String authorization) {
        super(client, endpoint, new HashMap<>() {{
            put("Authorization", authorization != null ? authorization: "");
            put("User-Agent", "Java BOT");
            put("Accept", "application/json");
            put("Content-Type", "application/json");
        }});
    }

}
