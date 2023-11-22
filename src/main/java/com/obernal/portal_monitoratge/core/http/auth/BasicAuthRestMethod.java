package com.obernal.portal_monitoratge.core.http.auth;

public class BasicAuthRestMethod implements AuthRestMethod {

    private final String encodedAuth;

    public BasicAuthRestMethod(String username, String password) {
        this.encodedAuth = AuthRestMethod.base64encode(username + ":" + password);
    }

    @Override
    public String getAuthorization() {
        return encodedAuth;
    }

}
