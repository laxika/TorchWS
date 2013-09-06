package io.torch.example.login.controller;

import io.torch.torchws.controller.WebPage;
import io.torch.torchws.http.request.TorchHttpRequest;
import io.torch.torchws.http.response.TorchHttpResponse;
import io.torch.torchws.session.Session;

public class LogoutPage extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        //Remove the user session
        session.clearSessionVariables();
        
        //Redirect the user
        response.redirect("/");
    }
}
