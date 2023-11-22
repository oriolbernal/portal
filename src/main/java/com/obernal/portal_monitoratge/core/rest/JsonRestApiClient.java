package com.obernal.portal_monitoratge.core.rest;

import com.obernal.portal_monitoratge.core.http.auth.AuthRestMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;

public abstract class JsonRestApiClient extends RestApiClient<JSONObject> {
    private static final Logger logger = LoggerFactory.getLogger(JsonRestApiClient.class);

    protected JsonRestApiClient(HttpClient client, String endpoint, AuthRestMethod authMethod) {
        super(client, endpoint, authMethod != null ? authMethod.getAuthorization() : null);
    }

    @Override
    protected JSONObject convert(String response) {
        logger.debug("Converting response to json: " + response);
        try {
            return new JSONObject(response);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

}
