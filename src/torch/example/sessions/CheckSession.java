package torch.example.sessions;

import torch.handler.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;

public class CheckSession extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response) {
        response.appendContent("The session id is: " + request.getSession().getSessionId());
    }
}
