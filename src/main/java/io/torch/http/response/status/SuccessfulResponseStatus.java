package io.torch.http.response.status;

public enum SuccessfulResponseStatus implements ResponseStatus {

    OK(200), CREATED(201), ACCEPTED(202), NON_AUTHORITATIVE(203), NO_CONTENT(204), RESET_CONTENT(205),
    PARTIAL_CONTENT(206), MULTI_STATUS(207), ALREADY_REPORTED(208), IM_USED(226);

    private SuccessfulResponseStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    private final int statusCode;

    @Override
    public int getStatusCode() {
        return statusCode;
    }

}
