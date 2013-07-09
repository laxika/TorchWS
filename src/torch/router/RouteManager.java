package torch.router;

import io.netty.handler.codec.http.HttpRequest;
import java.util.ArrayList;
import java.util.HashMap;
import torch.handler.WebPage;

public class RouteManager {

    private final HashMap<String, ArrayList<Integer>> staticRouteHops = new HashMap<>();
    private final HashMap<Integer, ArrayList<Integer>> dynamicRouteHops = new HashMap<>();
    private final ArrayList<Route> routingTargets = new ArrayList<>();

    public void addRoute(String route, WebPage target) {
        String[] routeHops = route.split("/");
        
        Route routeToAdd = new Route(route, routeHops.length, target);

        routingTargets.add(routeToAdd);

        int targetId = routingTargets.indexOf(routeToAdd);

        for (int i = 1; i < routeHops.length; i++) {
            if (routeHops[i].startsWith("@")) {
                //Dynamic route hop
                if (!dynamicRouteHops.containsKey(i)) {
                    dynamicRouteHops.put(i, new ArrayList<Integer>());
                }

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

        ArrayList<Integer> possibleTargets = null;

        for (int i = 1; i < routeHops.length; i++) {
            //ToDO!!! ITT EGY LOGIKAI HIBA VAN! Ha van dynemic és nem dynamic is, és a nem dnamic van előbb akkor az kiüti 
            //a dynamicot. Ezt egy kicsit másképp kell megcsinálni!!!
            if (routeHops[i].startsWith("@")) {
                //Dynamic route hop
                possibleTargets = keepSameItems(possibleTargets, dynamicRouteHops.get(i));
            } else {
                //Static route hop
                System.out.println(staticRouteHops.get(routeHops[i] + "_" + i));
                possibleTargets = keepSameItems(possibleTargets, staticRouteHops.get(routeHops[i] + "_" + i));
            }
        }

        //Have result
        if (possibleTargets != null) {
            Route target = routingTargets.get(possibleTargets.get(0));
            System.out.println(target.getRoutingUri());

            if (target.getHopCount() == routeHops.length) {
                return target.getTarget();
            }
        }

        return null;
    }

    private ArrayList<Integer> keepSameItems(ArrayList<Integer> first, ArrayList<Integer> second) {
        if (first == null) {
            return second;
        }

        if (second == null) {
            return first;
        }

        ArrayList<Integer> result = new ArrayList<>();

        for (Integer i : first) {
            if (second.contains(i)) {
                result.add(i);
            }
        }

        return result;
    }
}
