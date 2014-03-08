package io.torch.route;

import io.torch.controller.WebPage;
import java.lang.reflect.InvocationTargetException;

public interface RouteTarget {

    public WebPage newInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
