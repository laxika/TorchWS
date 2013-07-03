package torch.router;

import io.netty.handler.codec.http.HttpRequest;
import java.util.ArrayList;
import java.util.HashMap;
import torch.handler.WebPage;

public class RouteManager {

    private final HashMap<String, ArrayList<Integer>> staticRouteHops = new HashMap<>();
    private final HashMap<Integer, ArrayList<Integer>> dynamicRouteHops = new HashMap<>();
    private final ArrayList<WebPage> routingTargets = new ArrayList<>();

    public void addRoute(String route, WebPage target) {
        String[] routeHops = route.split("/");

        routingTargets.add(target);

        int targetId = routingTargets.indexOf(target);

        for (int i = 0; i < routeHops.length; i++) {
            if (routeHops[i].startsWith("@")) {
                //Dynamic route hop
                dynamicRouteHops.get(i).add(targetId);
            } else {
                //Static route hop
                if (!staticRouteHops.containsKey(routeHops[i] + "_" + i)) {
                    staticRouteHops.put(routeHops[i] + "_" + i, new ArrayList<Integer>());
                }

                staticRouteHops.get(routeHops[i] + "_" + i).add(targetId);
            }
        }
    }

    public WebPage getRouteTarget(HttpRequest route) {
        String routeUri = route.getUri();

        String[] routeHops = routeUri.split("/");
        
        ArrayList<Integer> possibleTargets = new ArrayList<>();

        for (int i = 0; i < routeHops.length; i++) {
            if (routeHops[i].startsWith("@")) {
                //Dynamic route hop
                possibleTargets = keepSameItems(possibleTargets, dynamicRouteHops.get(i));
            } else {
                //Static route hop
                possibleTargets = keepSameItems(possibleTargets, staticRouteHops.get(routeHops[i] + "_" + i));
            }
        }
        
        if(possibleTargets.size() > 0) {
            return routingTargets.get(possibleTargets.get(0));
        }

        return null;
    }
    
    public ArrayList<Integer> keepSameItems(ArrayList<Integer> first, ArrayList<Integer> second) {
        ArrayList<Integer> result = new ArrayList<>();
        
        for(Integer i : first) {
            if(second.contains(i)) {
                result.add(i);
            }
        }
        
        return result;
    }
}
