package io.torch.route;

import io.torch.http.request.RequestMethod;
import java.util.ArrayList;
import java.util.HashMap;

public interface Route {

    public int getHopCount();

    public RouteTarget getTarget();

    public RequestMethod getMethod();

    public String getRoutingUri();

    public int getDynamicVariableCount();

    public ArrayList<Integer> getDynamicVariablePositions();

    public HashMap<String, RouteVariable> calculateVariablesValuesFromUrl(String url);
}
