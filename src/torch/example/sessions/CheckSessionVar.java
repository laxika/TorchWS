package torch.example.sessions;

import torch.handler.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;

public class CheckSessionVar extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("The session id is: '" + session.getSessionId() + "'.<br>"
                + "The session variable testvar's value is '" + session.getSessionVariable("testvar") + "'.");
    }
}
