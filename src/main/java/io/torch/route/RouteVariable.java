package io.torch.route;

public class RouteVariable {

    private final String variableName;
    private final String variableValue;

    public RouteVariable(String variableName, String variableValue) {
        this.variableName = variableName;
        this.variableValue = variableValue;
    }
    
    public String getName() {
        return variableName;
    }
    
    public String getValue() {
        return variableValue;
    }
    
    @Override
    public String toString() {
        return variableValue;
    }
}
