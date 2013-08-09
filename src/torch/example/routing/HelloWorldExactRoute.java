package torch.example.routing;

import torch.controller.WebPage;
import torch.http.request.TorchHttpRequest;
import torch.http.response.TorchHttpResponse;
import torch.session.Session;

public class HelloWorldExactRoute extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("This is a direct route to a variable!");
    }
}
