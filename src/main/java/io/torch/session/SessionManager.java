package io.torch.session;

import java.util.HashMap;

public class SessionManager {

    private HashMap<String, Session> sessions = new HashMap<>();
    private SessionIdentifierGenerator generator = new SessionIdentifierGenerator();
    
    public Session getSession(String sessid) {
        return sessions.get(sessid);
    }
    
    public Session startNewSession() {
        Session session = new Session(generator.nextSessionId());
        
        sessions.put(session.getSessionId(), session);
        
        return session;
    }
    
    public void removeSessionById(String sessionId) {
        sessions.remove(sessionId);
    }
    
}
