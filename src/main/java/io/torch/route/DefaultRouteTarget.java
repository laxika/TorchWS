package io.torch.route;

import io.torch.controller.WebPage;
import io.torch.exception.NoSuchConstructorException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class DefaultRouteTarget implements RouteTarget {

    public static final Object[] NO_DEPENDENCY = new Object[]{};
    private final Object[] dependencyObjectList;
    private final Constructor constructor;

    public DefaultRouteTarget(Class<? extends WebPage> target, Object[] dependency) throws NoSuchConstructorException {
        if (target == null || dependency == null) {
            throw new IllegalArgumentException();
        }
        
        this.dependencyObjectList = dependency;
        
        //Calculate the classes of the depedencies
        Class[] dependencyClassList = new Class[dependencyObjectList.length];

        for (int i = 0; i < dependencyObjectList.length; i++) {
            dependencyClassList[i] = dependencyObjectList[i].getClass();
            
            if(dependencyClassList[i].isMemberClass() && !Modifier.isPublic(dependencyClassList[i].getModifiers())) {
                dependencyClassList[i] = dependencyClassList[i].getSuperclass();
            }
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
