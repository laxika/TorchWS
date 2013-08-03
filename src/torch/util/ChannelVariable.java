package torch.util;

import io.netty.util.AttributeKey;

public enum ChannelVariable {
    
    ROUTE_MANAGER(new AttributeKey<>("RouteManager"));
    
    private AttributeKey<Object> var;
    
    ChannelVariable(AttributeKey<Object> var) {
        this.var = var;
    }
    
    public AttributeKey<Object> getVariableKey() {
        return var;
    }
}
