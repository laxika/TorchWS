package torch.http.request;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import torch.route.Route;

public class ReadOnlyRouteDataStorage implements Iterable {

    private HashMap<String, String> urlVariableStorage = new HashMap<>();

    public ReadOnlyRouteDataStorage(Route route, String uri) {
        this.urlVariableStorage = route.calculateVariablesValuesFromUrl(uri);
    }

    public String getVariableValue(String name) {
        return urlVariableStorage.get(name);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return urlVariableStorage.entrySet().iterator();
    }
}
