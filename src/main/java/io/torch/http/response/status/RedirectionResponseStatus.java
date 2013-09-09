package io.torch.http.response.status;

public enum RedirectionResponseStatus implements ResponseStatus {

    MULTIPLE_CHOICES(300), MOVED_PERMANENTLY(301), FOUND(302), SEE_OTHER(303), NOT_MODIFIED(304), USE_PROXY(305),TEMPORARY_REDIRECT(307);

    private RedirectionResponseStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    private final int statusCode;

    @Override
    public int getStatusCode() {
        return statusCode;
    }

}
