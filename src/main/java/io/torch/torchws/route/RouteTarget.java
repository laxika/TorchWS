package io.torch.torchws.route;

import io.torch.torchws.controller.WebPage;
import io.torch.torchws.exception.NoSuchConstructorException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RouteTarget {

    public static final Object[] NO_DEPENDENCY = new Object[]{};
    private final Object[] dependencyObjectList;
    private final Constructor constructor;

    public RouteTarget(Class<? extends WebPage> target, Object[] dependency) throws NoSuchConstructorException {
        if (target == null || dependency == null) {
            throw new IllegalArgumentException();
        }
        
        this.dependencyObjectList = dependency;
        
        //Calculate the classes of the depedencies
        Class[] dependencyClassList = new Class[dependencyObjectList.length];

        for (int i = 0; i < dependencyObjectList.length; i++) {
            dependencyClassList[i] = dependencyObjectList[i].getClass();
        }

        //Try to get the constructor if it's not exist we shut down the server
        try {
            this.constructor = target.getConstructor(dependencyClassList);
        } catch (NoSuchMethodException e) {
            throw new NoSuchConstructorException("No such constructor in " + e.getMessage());
        }
    }
    
    public WebPage newInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return (WebPage) constructor.newInstance(dependencyObjectList);
    }
}
