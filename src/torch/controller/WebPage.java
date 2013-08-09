package torch.controller;

import torch.http.request.TorchHttpRequest;
import torch.http.response.TorchHttpResponse;
import torch.session.Session;

public abstract class WebPage {

    public abstract void handle(TorchHttpRequest request, TorchHttpResponse response, Session session);
}
