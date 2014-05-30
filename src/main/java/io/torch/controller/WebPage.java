package io.torch.controller;

import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import io.torch.session.Session;

/**
 * This is the base class of all pages. A new instance created at every routed requests.
 */
public abstract class WebPage {

    /**
     * This method is called to handle the request. You can manipulate the response in this method.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param session the user's session
     */
    public abstract void handle(TorchHttpRequest request, TorchHttpResponse response, Session session);
}
