package io.torch.route;

import io.torch.controller.WebPage;
import io.torch.exception.NoSuchConstructorException;
import io.torch.http.request.RequestMethod;
import io.torch.route.container.DynamicRouteContainer;
import io.torch.route.container.StaticRouteContainer;
import io.torch.util.ArrayUtils;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;

public class DefaultRouteManager implements RouteManager {

    private final StaticRouteContainer staticRouteContainer = new StaticRouteContainer();
    private final DynamicRouteContainer dynamicRouteContainer = new DynamicRouteContainer();

    /**
     * Add a new route to the defined routes.
     *
     * @param route the uri of the route
     * @param target the target of the route
     */
    @Override
    public void defineRoute(String route, Class<? extends WebPage> target) throws NoSuchConstructorException {
        defineRoute(route, target, DefaultRouteTarget.NO_DEPENDENCY, RequestMethod.GET);
    }

    /**
     * Add a new route to the defined routes.
     */
    @Override
    public void defineRoute(String route, Class<? extends WebPage> target, Object[] dependency) throws NoSuchConstructorException {
        defineRoute(route, target, dependency, RequestMethod.GET);
    }

    /**
     * Add a new route to the defined routes.
     */
    @Override
    public void defineRoute(String route, Class<? extends WebPage> target, Object[] dependency, RequestMethod method) throws NoSuchConstructorException {
        String[] routeHops = route.split("/");

        if (routeHops.length == 0) {
            addNewRoutePart("", 0, new DefaultRoute(route, target, dependency, method));
        } else {
            for (int level = 1; level < routeHops.length; level++) {
                addNewRoutePart(routeHops[level], level, new DefaultRoute(route, target, dependency, method));
            }
        }
    }

    /**
     * Add a new route to the defined routes.
     */
    @Override
    public void defineRoute(String route, Class<? extends WebPage> target, Object[] dependency, EnumSet<RequestMethod> methods) throws NoSuchConstructorException {
        for (RequestMethod method : methods) {
            defineRoute(route, target, dependency, method);
        }
    }

    /**
     * Calculate the best matching route for the uri.
     *
     * @param routeUri the url of the route
     * @param method the method of the request
     * @return the target or null if no target found
     */
    @Override
    public Route calculateRouteByUrl(String routeUri, RequestMethod method) {
        String[] routeHops = routeUri.split("/");

        if (routeHops.length == 0) {
            ArrayList<Route> possibleTargets = staticRouteContainer.getRoutePartPossibleTargets("", 0);

            if (possibleTargets.size() > 0) {
                return staticRouteContainer.getRoutePartPossibleTargets("", 0).get(0);
            }

            return null;
        }

        ArrayList<Route> possibleTargets = null;

        //Check the request url
        for (int level = 1; level < routeHops.length; level++) {
            possibleTargets = recalculatePossibleTargetsAtLevel(level, routeHops[level], possibleTargets);
        }

        if (possibleTargets == null) {
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
                if (actTarget.getDynamicVariableCount() == 0 && actTarget.getHopCount() == routeHops.length) {
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
