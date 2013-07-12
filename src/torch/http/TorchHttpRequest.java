package torch.http;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import io.netty.handler.codec.http.HttpRequest;
import java.util.HashMap;
import java.util.Set;
import torch.route.Route;
import torch.session.Session;

/**
 *
 * @author laxika
 */
public class TorchHttpRequest extends HttpBase {

    private final HttpRequest request;
    private final Route route;
    private final HashMap<String, Cookie> cookies = new HashMap<>();
    private final HashMap<String, String> routeVariables;

    public TorchHttpRequest(HttpRequest request, Route route) {
        this.request = request;
        this.route = route;

        if (route != null) {
            this.routeVariables = route.calculateVariablesValuesFromUrl(request.getUri());
        } else {
            this.routeVariables = new HashMap<>();
        }

        String cookieString = request.headers().get(COOKIE);

        if (cookieString != null) {
            Set<Cookie> cookies = CookieDecoder.decode(cookieString);

            for (Cookie cookie : cookies) {
                this.cookies.put(cookie.getName(), cookie);
            }
        }
    }

    //Return the session for the request via the sessdata cookie
    public Session getSession() {
        return session.getSession(cookies.get("SESSID").getValue());
    }

    public String getUrlVariable(String name) {
        return routeVariables.get(name);
    }
}
