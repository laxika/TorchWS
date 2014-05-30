package io.torch.http.request;

import io.netty.handler.codec.http.HttpHeaders;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import io.netty.handler.codec.http.HttpRequest;
import io.torch.cookie.ReadOnlyCookieDataStorage;
import io.torch.http.header.ReadOnlyHeaderDataStorage;
import io.torch.http.post.ReadOnlyPostDataStorage;
import io.torch.route.Route;
import io.torch.route.container.ReadOnlyRouteDataStorage;

public class TorchHttpRequest {

    private final Route route;
    private final HttpRequest request;
    private final ReadOnlyPostDataStorage postVariables;
    private final ReadOnlyRouteDataStorage routeVariables;
    private final ReadOnlyCookieDataStorage cookieStorage;
    private final ReadOnlyHeaderDataStorage headerStorage;

    public TorchHttpRequest(HttpRequest request, Route route) {
        this.route = route;
        this.request = request;
        this.cookieStorage = new ReadOnlyCookieDataStorage(request.headers().get(COOKIE));
        this.routeVariables = new ReadOnlyRouteDataStorage(route, request.getUri());
        this.postVariables = new ReadOnlyPostDataStorage(request);
        this.headerStorage = new ReadOnlyHeaderDataStorage(request.headers());
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

    public ReadOnlyHeaderDataStorage getHeaderData() {
        return headerStorage;
    }

    public boolean isKeepAlive() {
        return HttpHeaders.isKeepAlive(request);
    }

    public Route getRoute() {
        return route;
    }

    public String getUri() {
        return request.getUri();
    }
}
