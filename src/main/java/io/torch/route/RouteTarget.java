package io.torch.route;

import io.torch.controller.WebPage;
import io.torch.exception.NoSuchConstructorException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RouteTarget {

    public static final Object[] NO_DEPEDENCY = new Object[]{};
    private final Object[] depedencyObjectList;
    private final Constructor constructor;

    public RouteTarget(Class target, Object[] depedency) throws NoSuchConstructorException {
        if (!WebPage.class.isAssignableFrom(target) || target == null || depedency == null) {
            throw new IllegalArgumentException();
        }
        
        this.depedencyObjectList = depedency;
        
        //Calculate the classes of the depedencies
        Class[] depedencyClassList = new Class[depedencyObjectList.length];

        for (int i = 0; i < depedencyObjectList.length; i++) {
            depedencyClassList[i] = depedencyObjectList[i].getClass();
        }

        //Try to get the constructor if it's not exist we shut down the server
        try {
            this.constructor = target.getConstructor(depedencyClassList);
        } catch (NoSuchMethodException e) {
            throw new NoSuchConstructorException("No such constructor in " + e.getMessage());
        }
    }
    
    public WebPage newInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return (WebPage) constructor.newInstance(depedencyObjectList);
    }
}
