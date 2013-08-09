package io.torch.example.sessions;

import io.torch.handler.WebPage;
import io.torch.http.TorchHttpRequest;
import io.torch.http.TorchHttpResponse;
import io.torch.session.Session;

public class CheckSessionVar extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("The session id is: '" + session.getSessionId() + "'.<br>"
                + "The session variable testvar's value is '" + session.getSessionVariable("testvar") + "'.");
    }
}
