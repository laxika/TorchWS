package torch.router;

import java.util.ArrayList;
import java.util.HashMap;
import torch.handler.WebPage;
import torch.util.ArrayUtils;

public class RouteManager {

    private final HashMap<String, ArrayList<Integer>> staticRouteParts = new HashMap<>();
    private final HashMap<Integer, ArrayList<Integer>> dynamicRouteParts = new HashMap<>();
    private final ArrayList<Route> routingTargets = new ArrayList<>();

    /**
     * Add a new route to the defined routes.
     *
     * @param route the uri of the route
     * @param target the target of the route
     */
    public void addRoute(String route, WebPage target) {
        String[] routeHops = route.split("/");

        Route routeToAdd = new Route(route, routeHops.length, target);

        routingTargets.add(routeToAdd);

        for (int i = 1; i < routeHops.length; i++) {
            addNewRoutePart(routeHops[i], i, routingTargets.indexOf(routeToAdd));
        }
    }

    /**
     * Add a new url part to the routing manager.
     *
     * @param part the url part
     * @param partPosition the position of the part in the uri
     * @param targetId the target webpage of the url
     */
    private void addNewRoutePart(String part, int partPosition, int urlTargetId) {
        if (part.startsWith("@")) {
            //Dynamic route hop
            if (!dynamicRouteParts.containsKey(partPosition)) {
                dynamicRouteParts.put(partPosition, new ArrayList<Integer>());
            }

            dynamicRouteParts.get(partPosition).add(urlTargetId);
        } else {
            //Static route hop
            if (!staticRouteParts.containsKey(part + "_" + partPosition)) {
                staticRouteParts.put(part + "_" + partPosition, new ArrayList<Integer>());
            }

            staticRouteParts.get(part + "_" + partPosition).add(urlTargetId);
        }
    }

    /**
     * Get a target webpage of a route.
     *
     * @param routeUri the url of the route
     * @return the target or null if no target found
     */
    public WebPage getRouteTarget(String routeUri) {
        String[] routeHops = routeUri.split("/");

        ArrayList<Integer> possibleTargets = null;

        for (int level = 1; level < routeHops.length; level++) {
            possibleTargets = recalculatePossibleTargetsAtLevel(level, routeHops[level], possibleTargets);
        }

        //Have result, calculate the one we need from the possible routes
        if (possibleTargets != null && possibleTargets.size() > 0) {
            Route target = routingTargets.get(possibleTargets.get(0));

            for (int act : possibleTargets) {
                Route actTarget = routingTargets.get(act);

                //Exact match
                if (actTarget.getDynamicVariableCount() == 0) {
                    return actTarget.getTarget();
                }

                //Smalles dynamic route wins
                if (actTarget.getDynamicVariableCount() < target.getDynamicVariableCount()) {
                    target = actTarget;
                }
            }

            if (target.getHopCount() == routeHops.length) {
                return target.getTarget();
            }
        }

        return null;
    }

    private ArrayList<Integer> recalculatePossibleTargetsAtLevel(int level, String part, ArrayList<Integer> possibleTargets) {
        ArrayList<Integer> possibleTargetsAtThisLevel = new ArrayList<>();

        if (dynamicRouteParts.containsKey(level)) {
            possibleTargetsAtThisLevel.addAll(dynamicRouteParts.get(level));
        }
        if (staticRouteParts.containsKey(part + "_" + level)) {
            possibleTargetsAtThisLevel.addAll(staticRouteParts.get(part + "_" + level));
        }

        if (possibleTargets == null) {
            return possibleTargetsAtThisLevel;
        } else {
            return (ArrayList) ArrayUtils.intersection(possibleTargets, possibleTargetsAtThisLevel);
        }
    }
}
