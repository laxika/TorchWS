package io.torch.route.container;

import io.torch.route.Route;
import io.torch.route.RouteVariable;
import java.util.HashMap;
import java.util.Iterator;

public class ReadOnlyRouteDataStorage implements Iterable<RouteVariable> {

    private HashMap<String, RouteVariable> urlVariableStorage = new HashMap<>();

    public ReadOnlyRouteDataStorage(Route route, String uri) {
        this.urlVariableStorage = route.calculateVariablesValuesFromUrl(uri);
    }

    public RouteVariable getVariable(String name) {
        return urlVariableStorage.get(name);
    }

    @Override
    public Iterator<RouteVariable> iterator() {
        return urlVariableStorage.values().iterator();
    }
}
