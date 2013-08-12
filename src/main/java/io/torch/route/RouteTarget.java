package io.torch.route;

public class RouteTarget {

    private final Class target;
    private final Object[] depedencyObjectList;
    private final Class[] depedencyClassList;

    public RouteTarget(Class target, Object[] depedency) {
        this.target = target;
        this.depedencyObjectList = depedency;

        if (depedencyObjectList != null) {
            depedencyClassList = new Class[depedencyObjectList.length];
            
            for(int i = 0; i < depedencyObjectList.length; i++) {
                depedencyClassList[i] = depedencyObjectList[i].getClass();
            }
        } else {
            depedencyClassList = null;
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
