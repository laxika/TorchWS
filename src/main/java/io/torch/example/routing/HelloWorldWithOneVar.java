package io.torch.example.routing;

import io.torch.controller.WebPage;
import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import io.torch.session.Session;


public class HelloWorldWithOneVar extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("This is a route with one variable! The value of the lonely variable is: "+request.getRouteData().getVariable("variable1").getValue());
    }

}
