package io.torch.example.sessions;

import io.torch.torchws.controller.WebPage;
import io.torch.torchws.http.request.TorchHttpRequest;
import io.torch.torchws.http.response.TorchHttpResponse;
import io.torch.torchws.session.Session;

public class SetSessionVar extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        session.setSessionVariable("testvar", "XYZ smthing");
    }
    
}
