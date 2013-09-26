package io.torch.session;

public interface SessionManager {

    public Session getSession(String sessionId);

    public Session startNewSession();

    public void removeSessionById(String session);
}
