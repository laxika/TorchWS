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
            //Merging all the possibilities then remove the not possible routes from the merged ones.
            ArrayList<Integer> possibleTargetsAtThisLevel = new ArrayList<>();

            if (dynamicRouteHops.containsKey(i)) {
                possibleTargetsAtThisLevel.addAll(dynamicRouteHops.get(i));
            }
            if (staticRouteHops.containsKey(routeHops[i] + "_" + i)) {
                possibleTargetsAtThisLevel.addAll(staticRouteHops.get(routeHops[i] + "_" + i));
            }

            possibleTargets = keepSameItems(possibleTargets, possibleTargetsAtThisLevel);
        }

        //Have result, calculate the one we need from the possible routes
        if (possibleTargets != null && possibleTargets.size() > 0) {
            Route target = routingTargets.get(possibleTargets.get(0));
                    
            for(int act :possibleTargets) {
                Route actTarget = routingTargets.get(act);
                
                //Exact match
                if(actTarget.getDynamicVariableCount() == 0) {
                    return actTarget.getTarget();
                }
                
                //Smalles dynamic route wins
                if(actTarget.getDynamicVariableCount() < target.getDynamicVariableCount()) {
                    target = actTarget;
                }
            }

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
