package torch.handler;

import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;

public abstract class WebPage {

    public abstract void handle(TorchHttpRequest request, TorchHttpResponse response, Session session);
}
