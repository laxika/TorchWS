package torch.example.routing;

import torch.handler.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;

public class HelloWorldWithTwoVar extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("This is a route with two variables! Variable1: " + request.getRouteData().getValue("variable1").getValue() + " Variable2: " + request.getRouteData().getValue("variable2").getValue());
    }
}
