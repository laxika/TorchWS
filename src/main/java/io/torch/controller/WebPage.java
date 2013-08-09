package io.torch.controller;

import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import io.torch.session.Session;

public abstract class WebPage {

    public abstract void handle(TorchHttpRequest request, TorchHttpResponse response, Session session);
}
