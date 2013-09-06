package io.torch.torchws.controller;

import io.torch.torchws.http.request.TorchHttpRequest;
import io.torch.torchws.http.response.TorchHttpResponse;
import io.torch.torchws.session.Session;

/**
 * This is the base class of all pages. A new instance created at every routed requests.
 */
public abstract class WebPage {

    /**
     * This method is called to handle the request. You can manipulate the response in this method.
     * 
     * @param request the http request
     * @param response the http response
     * @param session the user's session
     */
    public abstract void handle(TorchHttpRequest request, TorchHttpResponse response, Session session);
}
