package com.obernal.portal_monitoratge.core.rest;

import com.obernal.portal_monitoratge.core.http.auth.AuthRestMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

public abstract class JsonRestApiClient extends RestApiClient<JSONObject> {
    private static final Logger logger = LoggerFactory.getLogger(JsonRestApiClient.class);

    protected JsonRestApiClient(HttpClient client, String endpoint, AuthRestMethod authMethod) {
        super(client, endpoint, authMethod != null ? authMethod.getAuthorization() : null);
    }

    @Override
    protected JSONObject convert(HttpResponse<String> response) {
        logger.debug("Response statusCode is: " + response.statusCode());
        logger.debug("Response body is: " + response.body());
        logger.debug("Converting response to json: " + response);
        try {
            return new JSONObject(response.body());
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

}
