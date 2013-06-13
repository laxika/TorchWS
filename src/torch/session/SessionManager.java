package torch.session;

import java.util.HashMap;

public class SessionManager {

    private HashMap<String, Session> sessions = new HashMap<>();
    private SessionIdentifierGenerator generator = new SessionIdentifierGenerator();
    
    public Session getSession(String sessid) {
        return sessions.get(sessid);
    }
    
    public Session startNewSession() {
        String sessionId = generator.nextSessionId();
        
        return sessions.put(sessionId, new Session(sessionId));
    }
    
}
