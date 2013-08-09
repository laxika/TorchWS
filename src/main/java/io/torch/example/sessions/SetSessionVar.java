package io.torch.example.sessions;

import io.torch.handler.WebPage;
import io.torch.http.TorchHttpRequest;
import io.torch.http.TorchHttpResponse;
import io.torch.session.Session;

public class SetSessionVar extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        session.setSessionVariable("testvar", "XYZ smthing");
    }
    
}
