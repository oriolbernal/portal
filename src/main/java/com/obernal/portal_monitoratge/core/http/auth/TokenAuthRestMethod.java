package com.obernal.portal_monitoratge.core.http.auth;

public class TokenAuthRestMethod implements AuthRestMethod {

    private final String encodedAuth;

    public TokenAuthRestMethod(String email, String token) {
        this.encodedAuth = AuthRestMethod.base64encode(email + "/token:" + token);
    }

    @Override
    public String getAuthorization() {
        return encodedAuth;
    }

}
