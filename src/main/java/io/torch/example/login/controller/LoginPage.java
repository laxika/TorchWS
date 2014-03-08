package io.torch.example.login.controller;

import io.torch.controller.WebPage;
import io.torch.http.request.RequestMethod;
import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import io.torch.session.Session;
import io.torch.template.Templateable;

@Templateable(path = "example/login/login.tpl")
public class LoginPage extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        //User already logged in
        if (session.isSessionVariableSet("userid")) {
            response.redirect("/");
            return;
        }

        if (request.getMethod() == RequestMethod.POST) {
            //Validate the password/username
            if ("admin".equals(request.getPostData().getVariable("username").getValue()) && "admin".equals(request.getPostData().getVariable("password").getValue())) {
                session.setSessionVariable("userid", 1); //1th user is the admin, use real userid here

                response.redirect("/");
            }
        }
    }
}
