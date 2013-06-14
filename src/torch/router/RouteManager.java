package torch.router;

import io.netty.handler.codec.http.HttpRequest;
import java.util.HashMap;
import torch.handler.WebPage;

public class RouteManager {

    private static final HashMap<String, WebPage> routes = new HashMap<>();

    public void addRoute(String route, WebPage target) {
        routes.put(route, target);
    }

    public WebPage getRouteTarget(HttpRequest route) {
        if (routes.containsKey(route.getMethod() + " " + route.getUri())) {
            //Check for 'GET /route'
            return routes.get(route.getMethod() + " " + route.getUri());
        } else if (routes.containsKey(route.getUri())) {
            //Check for '/route'
            return routes.get(route.getUri());
        }

        return null;
    }
}
