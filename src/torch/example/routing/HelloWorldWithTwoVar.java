package torch.example.routing;

import torch.handler.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;

public class HelloWorldWithTwoVar extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("This is a route with two variables! Variable1: " + request.getRouteData().getVariableValue("variable1") + " Variable2: " + request.getRouteData().getVariableValue("variable2"));
    }
}
