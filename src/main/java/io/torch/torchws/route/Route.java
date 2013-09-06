package io.torch.torchws.route;

import io.torch.torchws.controller.WebPage;
import io.torch.torchws.exception.NoSuchConstructorException;
import io.torch.torchws.http.request.RequestMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Route {

    private final String routingUri;
    private final int hopCount;
    private final RouteTarget target;
    private final String[] routingHops;
    private final int dynamicVariableCount;
    private final ArrayList<Integer> dynamicVariablePositions = new ArrayList<>();
    private final RequestMethod method;

    public Route(String routingUri, Class<? extends WebPage> target) throws NoSuchConstructorException {
        this(routingUri, target, RouteTarget.NO_DEPENDENCY, RequestMethod.GET);
    }

    public Route(String routingUri, Class<? extends WebPage> target, Object[] dependency, RequestMethod method) throws NoSuchConstructorException {
        if (target == null || dependency == null || method == null) {
            throw new IllegalArgumentException();
        }

        this.routingUri = routingUri;
        this.target = new RouteTarget(target, dependency);
        this.routingHops = routingUri.split("/");
        this.hopCount = routingHops.length;
        this.method = method;

        for (int i = 0; i < routingHops.length; i++) {
            if (routingHops[i].startsWith("@")) {
                dynamicVariablePositions.add(i);
            }
            routingHops[i] = routingHops[i].startsWith("@") ? routingHops[i].substring(1) : routingHops[i];
        }

        this.dynamicVariableCount = dynamicVariablePositions.size();
    }

    public int getHopCount() {
        return hopCount;
    }

    public RouteTarget getTarget() {
        return target;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public String getRoutingUri() {
        return routingUri;
    }

    public int getDynamicVariableCount() {
        return dynamicVariableCount;
    }

    public ArrayList<Integer> getDynamicVariablePositions() {
        return dynamicVariablePositions;
    }

    public HashMap<String, RouteVariable> calculateVariablesValuesFromUrl(String url) {
        String[] urlParts = url.split("/");

        HashMap<String, RouteVariable> result = new HashMap<>();

        for (int position : dynamicVariablePositions) {
            result.put(routingHops[position], new RouteVariable(routingHops[position], urlParts[position]));
        }

        return result;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(routingUri);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Route && ((Route) o).routingUri.equals(this.routingUri);
    }
}
