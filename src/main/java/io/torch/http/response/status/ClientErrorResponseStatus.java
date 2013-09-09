package io.torch.http.response.status;

public enum ClientErrorResponseStatus implements ResponseStatus {
    /** TODO **/
    BAD_REQUEST(400);

    private ClientErrorResponseStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    private final int statusCode;

    @Override
    public int getStatusCode() {
        return statusCode;
    }

}
