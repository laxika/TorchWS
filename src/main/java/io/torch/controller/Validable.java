package io.torch.controller;

import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import io.torch.session.Session;

/**
 * This interface is used to add a validation option to the WebPage. If the validation
 * fails then the server will send a 400 Bad Request error.
 */
public interface Validable {

    /**
     * If this method returns false then the request processing is stoped and the
     * webserver will send a 400 Bad Request to the requester.
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    public boolean validate(TorchHttpRequest request, TorchHttpResponse response, Session session);
}
