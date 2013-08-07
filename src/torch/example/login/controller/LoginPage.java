package torch.example.login.controller;

import torch.handler.WebPage;
import torch.http.request.RequestMethod;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;
import torch.template.Templateable;

public class LoginPage extends WebPage implements Templateable {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        //User already logged in
        if(session.isSessionVariableSet("userid")) {
            response.redirect("/");
            return;
        }
        
        if (request.getMethod() == RequestMethod.POST) {
            //Validate the password/username
            if("admin".equals(request.getPostData().getVariable("username").getValue()) && "admin".equals(request.getPostData().getVariable("password").getValue())) {
                session.setSessionVariable("userid", 1); //1th user is the admin, use real userid here
                
                response.redirect("/");
            }
        }
    }

    @Override
    public String getTemplate() {
        return "example/login/login.tpl";
    }

    @Override
    public Object getTemplateRoot() {
        return null;
    }
}
