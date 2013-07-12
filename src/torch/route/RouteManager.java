package torch.route;

import java.util.ArrayList;
import java.util.Iterator;
import torch.handler.WebPage;
import torch.http.RequestMethod;
import torch.route.container.DynamicRouteContainer;
import torch.route.container.StaticRouteContainer;
import torch.util.ArrayUtils;

public class RouteManager {

    private final StaticRouteContainer staticRouteContainer = new StaticRouteContainer();
    private final DynamicRouteContainer dynamicRouteContainer = new DynamicRouteContainer();

    public void defineRoute(String route, WebPage target) {
        defineRoute(route, target, RequestMethod.GET);
    }

    /**
     * Add a new route to the defined routes.
     *
     * @param route the uri of the route
     * @param target the target of the route
     */
    public void defineRoute(String route, WebPage target, RequestMethod method) {
        String[] routeHops = route.split("/");

        for (int level = 1; level < routeHops.length; level++) {
            addNewRoutePart(routeHops[level], level, new Route(route, method, target));
        }
    }

    /**
     * Calculate the best matching route for the uri.
     *
     * @param routeUri the url of the route
     * @param method the method of the request
     * @return the target or null if no target found
     */
    public Route calculateRouteByUrl(String routeUri, RequestMethod method) {
        String[] routeHops = routeUri.split("/");

        ArrayList<Route> possibleTargets = null;

        //Check the request url
        for (int level = 1; level < routeHops.length; level++) {
            possibleTargets = recalculatePossibleTargetsAtLevel(level, routeHops[level], possibleTargets);
        }
        
        if(possibleTargets == null) {
            return null;
        }

        Iterator<Route> it = possibleTargets.iterator();
        while (it.hasNext()) {
            Route route = it.next();
            if (route.getMethod() != method) {
                it.remove();
            }
        }

        //Have result, calculate the one we need from the possible routes
        if (possibleTargets.size() > 0) {
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
