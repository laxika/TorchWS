package io.torch.example.routing;

import io.torch.torchws.controller.WebPage;
import io.torch.torchws.http.request.TorchHttpRequest;
import io.torch.torchws.http.response.TorchHttpResponse;
import io.torch.torchws.session.Session;

public class HelloWorldExactRoute extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("This is a direct route to a variable!");
    }
}
