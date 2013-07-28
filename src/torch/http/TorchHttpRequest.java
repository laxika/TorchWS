package torch.http;

import io.netty.handler.codec.http.HttpRequest;
import java.util.HashMap;
import torch.route.Route;

public class TorchHttpRequest {

    private final HttpRequest request;
    private final HashMap<String, String> routeVariables;

    public TorchHttpRequest(HttpRequest request, Route route) {
        this.request = request;

        if (route != null) {
            this.routeVariables = route.calculateVariablesValuesFromUrl(request.getUri());
        } else {
            this.routeVariables = new HashMap<>();
        }
    }

    public String getUrlVariable(String name) {
        return routeVariables.get(name);
    }
}
