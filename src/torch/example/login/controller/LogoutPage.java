package torch.example.login.controller;

import torch.controller.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;

public class LogoutPage extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        //Remove the user session
        session.clearSessionVariables();
        
        //Redirect the user
        response.redirect("/");
    }
}
