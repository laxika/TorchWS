package io.torch.session;

import java.util.HashMap;

public class DefaultSessionManager implements SessionManager {

    private HashMap<String, DefaultSession> sessions = new HashMap<>();
    private SessionIdentifierGenerator generator = new SessionIdentifierGenerator();

    @Override
    public DefaultSession getSession(String sessid) {
        return sessions.get(sessid);
    }

    @Override
    public DefaultSession startNewSession() {
        DefaultSession session = new DefaultSession(generator.nextSessionId());

        sessions.put(session.getSessionId(), session);

        return session;
    }

    @Override
    public void removeSessionById(String sessionId) {
        sessions.remove(sessionId);
    }

}
