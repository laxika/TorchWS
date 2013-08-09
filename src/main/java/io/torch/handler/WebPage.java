package io.torch.handler;

import io.torch.http.TorchHttpRequest;
import io.torch.http.TorchHttpResponse;
import io.torch.session.Session;

public abstract class WebPage {

    public abstract void handle(TorchHttpRequest request, TorchHttpResponse response, Session session);
}
