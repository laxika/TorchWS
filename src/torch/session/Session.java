package torch.session;

import java.util.HashMap;

public class Session {
    
    private final String sessionId;
    private final HashMap<String,Object> sessionVariables = new HashMap<>();
    
    public Session(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionVariable(String name, Object value) {
        sessionVariables.put(name, value);
    }
    
    public Object getSessionVariable(String name) {
        return sessionVariables.get(name);
    }
}
