package io.torch.example.login.controller;

import io.torch.controller.WebPage;
import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
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
