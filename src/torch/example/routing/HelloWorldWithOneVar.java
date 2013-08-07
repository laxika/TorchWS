package torch.example.routing;

import torch.handler.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;


public class HelloWorldWithOneVar extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("This is a route with one variable! The value of the lonely variable is: "+request.getRouteData().getValue("variable1").getValue());
    }

}
