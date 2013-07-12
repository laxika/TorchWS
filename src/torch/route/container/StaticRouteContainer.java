package torch.route.container;

import java.util.ArrayList;
import java.util.HashMap;
import torch.route.Route;

public class StaticRouteContainer {

    private static final ArrayList<Route> EMPTY_ARRAY_LIST = new ArrayList<>();
    private final HashMap<String, ArrayList<Route>> staticRouteParts = new HashMap<>();

    public void addRoutePart(String part, int level, Route route) {
        if (!staticRouteParts.containsKey(part + "_" + level)) {
            staticRouteParts.put(part + "_" + level, new ArrayList<Route>());
        }

        staticRouteParts.get(part + "_" + level).add(route);
    }

    public ArrayList<Route> getRoutePartPossibleTargets(String part, int level) {
        if (containsRoutePart(part, level)) {
            return staticRouteParts.get(part + "_" + level);
        }

        return EMPTY_ARRAY_LIST;
    }

    private boolean containsRoutePart(String part, int level) {
        return staticRouteParts.containsKey(part + "_" + level);
    }
}
