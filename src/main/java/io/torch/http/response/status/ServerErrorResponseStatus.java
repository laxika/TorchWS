package io.torch.http.response.status;

public enum ServerErrorResponseStatus implements ResponseStatus {

    INTERNAL_SERVER_ERROR(500), NOT_IMPLEMENTED(501), BAD_GATEWAY(502), SERVICE_UNAVAILABLE(503),
    GATEWAY_TIMEOUT(504), HTTP_VERSION_NOT_SUPPORTED(505);

    private ServerErrorResponseStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    private final int statusCode;

    @Override
    public int getStatusCode() {
        return statusCode;
    }

}
