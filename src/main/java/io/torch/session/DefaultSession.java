package io.torch.session;

import java.util.HashMap;

public class DefaultSession implements Session {

    private final String sessionId;
    private final HashMap<String, Object> sessionVariables = new HashMap<>();

    public DefaultSession(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public void setSessionVariable(String name, Object value) {
        if (value == null) {
            sessionVariables.remove(name);
        } else {
            sessionVariables.put(name, value);
        }
    }

    @Override
    public Object getSessionVariable(String name) {
        return sessionVariables.get(name);
    }

    @Override
    public boolean isSessionVariableSet(String name) {
        return sessionVariables.containsKey(name);
    }

    @Override
    public void clearSessionVariables() {
        sessionVariables.clear();
    }
}
