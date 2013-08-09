package io.torch.example.login.controller;

import io.torch.handler.WebPage;
import io.torch.http.TorchHttpRequest;
import io.torch.http.TorchHttpResponse;
import io.torch.session.Session;

public class LogoutPage extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        //Remove the user session
        session.clearSessionVariables();
        
        //Redirect the user
        response.redirect("/");
    }
}
