package torch.example.sessions;

import torch.handler.WebPageHandler;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;


public class StartSession extends WebPageHandler{

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response) {
        Session session = response.startNewSession();
        
        response.appendContent("Created a new session with id: "+session.getSessionId());
    }

}
