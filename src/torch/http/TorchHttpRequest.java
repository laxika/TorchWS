package torch.http;

import io.netty.handler.codec.http.HttpRequest;
import java.util.HashMap;
import torch.route.Route;
import torch.session.Session;

/**
 *
 * @author laxika
 */
public class TorchHttpRequest extends HttpBase {

    private final HttpRequest request;
    private final HashMap<String, String> routeVariables;
    private final Session session;

    public TorchHttpRequest(HttpRequest request, Route route, String sessionId) {
        this.request = request;
        this.session = sessionManager.getSession(sessionId);

        if (route != null) {
            this.routeVariables = route.calculateVariablesValuesFromUrl(request.getUri());
        } else {
            this.routeVariables = new HashMap<>();
        }
    }

    //Return the session for the request via the sessdata cookie
    public Session getSession() {
        return session;
    }

    public String getUrlVariable(String name) {
        return routeVariables.get(name);
    }
}
