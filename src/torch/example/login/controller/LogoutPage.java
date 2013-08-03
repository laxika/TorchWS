package torch.example.login.controller;

import io.netty.handler.codec.http.HttpHeaders;
import torch.handler.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;

public class LogoutPage extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        //Remove the user session
        session.setSessionVariable("userid", null);
        
        //Redirect the user
        response.getHeaderData().setHeader(HttpHeaders.Names.LOCATION, "/");
    }
}
