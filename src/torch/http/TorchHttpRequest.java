package torch.http;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import torch.cookie.ReadOnlyCookieDataStorage;
import torch.http.request.ReadOnlyPostDataStorage;
import torch.http.request.ReadOnlyRouteDataStorage;
import torch.route.Route;

public class TorchHttpRequest {

    private final HttpRequest request;
    private final ReadOnlyPostDataStorage postVariables;
    private final ReadOnlyRouteDataStorage routeVariables;
    private final ReadOnlyCookieDataStorage cookieStorage;

    public TorchHttpRequest(HttpRequest request, Route route) {
        this.request = request;
        this.cookieStorage = new ReadOnlyCookieDataStorage(request.headers().get(COOKIE));
        this.routeVariables = new ReadOnlyRouteDataStorage(route,request.getUri());
        this.postVariables =  new ReadOnlyPostDataStorage(request);
    }

    public RequestMethod getMethod() {
        HttpMethod method = request.getMethod();

        if (method == HttpMethod.POST) {
            return RequestMethod.POST;
        }

        if (method == HttpMethod.DELETE) {
            return RequestMethod.DELETE;
        }

        if (method == HttpMethod.PUT) {
            return RequestMethod.PUT;
        }

        return RequestMethod.GET;
    }
    
    public ReadOnlyRouteDataStorage getRouteData() {
        return routeVariables;
    }

    public ReadOnlyPostDataStorage getPostData() {
        return postVariables;
    }

    public ReadOnlyCookieDataStorage getCookieData() {
        return cookieStorage;
    }
}
