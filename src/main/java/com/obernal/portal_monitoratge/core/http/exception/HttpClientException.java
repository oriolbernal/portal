package com.obernal.portal_monitoratge.core.http.exception;

public class HttpClientException extends RuntimeException {

    private final Integer statusCode;
    private final String message;

    public HttpClientException(Integer statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
