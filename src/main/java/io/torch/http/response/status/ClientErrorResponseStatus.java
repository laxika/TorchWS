package io.torch.http.response.status;

public enum ClientErrorResponseStatus implements ResponseStatus {

    BAD_REQUEST(400), UNAUTHORIZED(401), PAYMENT_REQUIRED(402), FORBIDDEN(403), NOT_FOUND(404), METHOD_NOT_ALLOWED(405),
    NOT_ACCEPTABLE(406), PROXY_AUTHENTICATION_REQUIRED(407), REQUEST_TIMEOUT(408), CONFLICT(409), GONE(410),
    LENGTH_REQUIRED(411), PRECONDITION_FAILED(412), REQUEST_ENTITY_TOO_LARGE(413), REQUEST_URI_TOO_LONG(414),
    UNSUPPORTED_MEDIA_TYPE(415), REQUESTED_RANGE_NOT_SATISFIABLE(416), EXPECTATION_FAILED(417);

    private ClientErrorResponseStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    private final int statusCode;

    @Override
    public int getStatusCode() {
        return statusCode;
    }

}
