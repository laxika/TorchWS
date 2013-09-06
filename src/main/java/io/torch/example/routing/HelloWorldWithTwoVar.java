package io.torch.example.routing;

import io.torch.torchws.controller.WebPage;
import io.torch.torchws.http.request.TorchHttpRequest;
import io.torch.torchws.http.response.TorchHttpResponse;
import io.torch.torchws.route.RouteVariable;
import io.torch.torchws.session.Session;

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
