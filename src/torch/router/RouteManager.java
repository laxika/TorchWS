package torch.router;

import java.util.HashMap;
import torch.handler.WebPage;

public class RouteManager {
    
    private static final HashMap<String, WebPage> routes = new HashMap<>();
    
    public void addRoute(String route, WebPage target) {
        routes.put(route, target);
    }
    
    public WebPage getRouteTarget(String route) {
        return routes.get(route);
    }
}
