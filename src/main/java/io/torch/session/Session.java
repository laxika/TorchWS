package io.torch.session;

import java.util.HashMap;

public class Session {

    private final String sessionId;
    private final HashMap<String, Object> sessionVariables = new HashMap<>();

    public Session(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionVariable(String name, Object value) {
        if (value == null) {
            sessionVariables.remove(name);
        } else {
            sessionVariables.put(name, value);
        }
    }

    public Object getSessionVariable(String name) {
        return sessionVariables.get(name);
    }

    public boolean isSessionVariableSet(String name) {
        return sessionVariables.containsKey(name);
    }
    
    public void clearSessionVariables() {
        sessionVariables.clear();
    }
}
