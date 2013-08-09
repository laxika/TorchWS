package torch.example.routing;

import torch.controller.WebPage;
import torch.http.request.TorchHttpRequest;
import torch.http.response.TorchHttpResponse;
import torch.session.Session;


public class HelloWorldWithOneVar extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("This is a route with one variable! The value of the lonely variable is: "+request.getRouteData().getVariable("variable1").getValue());
    }

}
