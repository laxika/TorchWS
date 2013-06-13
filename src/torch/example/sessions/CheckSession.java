package torch.example.sessions;

import torch.handler.WebPageHandler;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;

public class CheckSession extends WebPageHandler {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response) {
        response.appendContent("The session id is: " + request.getSession().getSessionId());
    }
}
