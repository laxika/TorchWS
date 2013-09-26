package io.torch.util;

import io.netty.util.AttributeKey;

public enum ChannelVariable {
    
    ROUTE_MANAGER(new AttributeKey<>("RouteManager")),
    SESSION_MANAGER(new AttributeKey<>("SessionManager"));
    
    private AttributeKey<Object> var;
    
    ChannelVariable(AttributeKey<Object> var) {
        this.var = var;
    }
    
    public AttributeKey<Object> getVariableKey() {
        return var;
    }
}
