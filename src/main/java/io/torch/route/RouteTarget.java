package io.torch.route;

import io.torch.exception.NoSuchConstructorException;

public class RouteTarget {

    private final Class target;
    private final Object[] depedencyObjectList;
    private final Class[] depedencyClassList;

    public RouteTarget(Class target, Object[] depedency) throws NoSuchConstructorException {
        if (target == null || depedency == null) {
            throw new IllegalArgumentException();
        }
        
        this.target = target;
        this.depedencyObjectList = depedency;
        this.depedencyClassList = new Class[depedencyObjectList.length];

        //Calculate the classes
        for (int i = 0; i < depedencyObjectList.length; i++) {
            depedencyClassList[i] = depedencyObjectList[i].getClass();
        }

        try {
            target.getConstructor(depedencyClassList);
        } catch (NoSuchMethodException e) {
            throw new NoSuchConstructorException("No such constructor in " + e.getMessage());
        }
    }

    public Class getTargetClass() {
        return target;
    }

    public Object[] getDepedencyObjectList() {
        return depedencyObjectList;
    }

    public Class[] getDepedencyClassList() {
        return depedencyClassList;
    }
}
