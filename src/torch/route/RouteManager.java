package torch.route;

import java.util.ArrayList;
import torch.handler.WebPage;
import torch.route.container.DynamicRouteContainer;
import torch.route.container.StaticRouteContainer;
import torch.util.ArrayUtils;

public class RouteManager {

    private final StaticRouteContainer staticRouteContainer = new StaticRouteContainer();
    private final DynamicRouteContainer dynamicRouteContainer = new DynamicRouteContainer();

    /**
     * Add a new route to the defined routes.
     *
     * @param route the uri of the route
     * @param target the target of the route
     */
    public void addRoute(String route, WebPage target) {
        String[] routeHops = route.split("/");

        for (int level = 1; level < routeHops.length; level++) {
            addNewRoutePart(routeHops[level], level, new Route(route, routeHops.length, target));
        }
    }

    /**
     * Add a new url part to the routing manager.
     *
     * @param part the url part
     * @param partPosition the position of the part in the uri
     * @param targetId the target webpage of the url
     */
    private void addNewRoutePart(String part, int level, Route route) {
        if (part.startsWith("@")) {
            dynamicRouteContainer.addRoutePart(level, route);
        } else {
            staticRouteContainer.addRoutePart(part, level, route);
        }
    }

    /**
     * Get the best matching route for the uri.
     *
     * @param routeUri the url of the route
     * @return the target or null if no target found
     */
    public Route getRouteByUrl(String routeUri) {
        String[] routeHops = routeUri.split("/");

        ArrayList<Route> possibleTargets = null;

        for (int level = 1; level < routeHops.length; level++) {
            possibleTargets = recalculatePossibleTargetsAtLevel(level, routeHops[level], possibleTargets);
        }

        //Have result, calculate the one we need from the possible routes
        if (possibleTargets != null && possibleTargets.size() > 0) {
            Route target = possibleTargets.get(0);

            for (Route actTarget : possibleTargets) {

                //Exact match
                if (actTarget.getDynamicVariableCount() == 0) {
                    return actTarget;
                }

                //Smalles dynamic route wins
                if (actTarget.getDynamicVariableCount() < target.getDynamicVariableCount()) {
                    target = actTarget;
                }
            }

            if (target.getHopCount() == routeHops.length) {
                return target;
            }
        }

        return null;
    }

    private ArrayList<Route> recalculatePossibleTargetsAtLevel(int level, String part, ArrayList<Route> possibleTargets) {
        ArrayList<Route> possibleTargetsAtThisLevel = new ArrayList<>();

        possibleTargetsAtThisLevel.addAll(dynamicRouteContainer.getRoutePartPossibleTargets(level));
        possibleTargetsAtThisLevel.addAll(staticRouteContainer.getRoutePartPossibleTargets(part, level));

        if (possibleTargets == null) {
            return possibleTargetsAtThisLevel;
        } else {
            return (ArrayList) ArrayUtils.intersection(possibleTargets, possibleTargetsAtThisLevel);
        }
    }
}
