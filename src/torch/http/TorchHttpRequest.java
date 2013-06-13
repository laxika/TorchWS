package torch.http;

import io.netty.handler.codec.http.HttpRequest;
import torch.session.Session;
import torch.session.SessionManager;

/**
 *
 * @author laxika
 */
public class TorchHttpRequest {
    
    public TorchHttpRequest(HttpRequest req) {
        
    }

    //Save the session data
    private static SessionManager session = new SessionManager();

    //Return the session for the request via the sessdata cookie
    public Session getSession() {
        return null;
    }
}
