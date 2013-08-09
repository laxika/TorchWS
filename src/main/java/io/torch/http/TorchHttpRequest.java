package io.torch.http;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import io.netty.handler.codec.http.HttpRequest;
import io.torch.cookie.ReadOnlyCookieDataStorage;
import io.torch.http.request.ReadOnlyPostDataStorage;
import io.torch.http.request.ReadOnlyRouteDataStorage;
import io.torch.http.request.RequestMethod;
import io.torch.route.Route;

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
        return RequestMethod.getMethodByNettyMethod(request.getMethod());
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
