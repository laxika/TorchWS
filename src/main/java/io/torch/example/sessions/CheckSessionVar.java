package io.torch.example.sessions;

import io.torch.torchws.controller.WebPage;
import io.torch.torchws.http.request.TorchHttpRequest;
import io.torch.torchws.http.response.TorchHttpResponse;
import io.torch.torchws.session.Session;

public class CheckSessionVar extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("The session id is: '" + session.getSessionId() + "'.<br>"
                + "The session variable testvar's value is '" + session.getSessionVariable("testvar") + "'.");
    }
}
