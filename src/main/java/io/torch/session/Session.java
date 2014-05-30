package io.torch.session;

public interface Session {

    public static final String SESSION_COOKIE_NAME = "SESSID";

    public String getSessionId();

    public void setSessionVariable(String name, Object value);

    public Object getSessionVariable(String name);

    public boolean isSessionVariableSet(String name);

    public void clearSessionVariables();
}
