package torch.example.routing;

import torch.controller.WebPage;
import torch.http.request.TorchHttpRequest;
import torch.http.response.TorchHttpResponse;
import torch.route.RouteVariable;
import torch.session.Session;

public class HelloWorldWithTwoVar extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("This is a route with two variables! Variable1: " + request.getRouteData().getVariable("variable1").getValue() + " Variable2: " + request.getRouteData().getVariable("variable2").getValue());
        response.appendContent("<br><br>Route variables listened here to show how to use the iterator with them:<br><br>");

        for (RouteVariable routeVar : request.getRouteData()) {
            response.appendContent("Route var: <b>" + routeVar.getName() + "</b> = '" + routeVar.getValue()+"'<br>");
        }
    }
}
