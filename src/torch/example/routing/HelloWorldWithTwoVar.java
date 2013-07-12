package torch.example.routing;

import torch.handler.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;

public class HelloWorldWithTwoVar extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response) {
        response.appendContent("This is a route with two variables! Variable1: " + request.getUrlVariable("variable1") + " Variable2: " + request.getUrlVariable("variable2"));
    }
}
