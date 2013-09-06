package io.torch.route.container;

import java.util.ArrayList;
import java.util.HashMap;
import io.torch.route.Route;

public class DynamicRouteContainer {

    private static final ArrayList<Route> EMPTY_ARRAY_LIST = new ArrayList<>();
    private final HashMap<Integer, ArrayList<Route>> dynamicRouteParts = new HashMap<>();

    public void addRoutePart(int level, Route route) {
        if (!dynamicRouteParts.containsKey(level)) {
            dynamicRouteParts.put(level, new ArrayList<Route>());
        }

        dynamicRouteParts.get(level).add(route);
    }

    public ArrayList<Route> getRoutePartPossibleTargets(int level) {
        if (dynamicRouteParts.containsKey(level)) {
            return dynamicRouteParts.get(level);
        }

        return EMPTY_ARRAY_LIST;
    }
}
