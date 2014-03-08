package io.torch.route;

import io.torch.controller.WebPage;
import io.torch.exception.NoSuchConstructorException;
import io.torch.http.request.RequestMethod;
import java.util.EnumSet;

public interface RouteManager {

    /**
     * Add a new route to the defined routes.
     *
     * @param route the uri of the route
     * @param target the target of the route
     */
    public void defineRoute(String route, Class<? extends WebPage> target) throws NoSuchConstructorException;

    /**
     * Add a new route to the defined routes.
     */
    public void defineRoute(String route, Class<? extends WebPage> target, Object[] dependency) throws NoSuchConstructorException;

    /**
     * Add a new route to the defined routes.
     */
    public void defineRoute(String route, Class<? extends WebPage> target, Object[] dependency, RequestMethod method) throws NoSuchConstructorException;

    /**
     * Add a new route to the defined routes.
     */
    public void defineRoute(String route, Class<? extends WebPage> target, Object[] dependency, EnumSet<RequestMethod> methods) throws NoSuchConstructorException;

    /**
     * Calculate the best matching route for the uri.
     *
     * @param routeUri the url of the route
     * @param method the method of the request
     * @return the target or null if no target found
     */
    public Route calculateRouteByUrl(String routeUri, RequestMethod method);
}
