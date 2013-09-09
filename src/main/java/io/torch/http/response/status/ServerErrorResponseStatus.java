package io.torch.http.response.status;

public enum ServerErrorResponseStatus implements ResponseStatus {
    /** TODO **/
    INTERNAL_SERVER_ERROR(500);

    private ServerErrorResponseStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    private final int statusCode;

    @Override
    public int getStatusCode() {
        return statusCode;
    }

}
