package torch.example.login.controller;

import torch.example.login.data.IndexData;
import torch.handler.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;
import torch.template.Templateable;

public class IndexPage extends WebPage implements Templateable {
    
    private IndexData indexData;

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        indexData = new IndexData(session.isSessionVariableSet("userid"));
        
        if(session.isSessionVariableSet("userid")) {
            indexData.setUsername("admin"); //query the real username from the db here
        }
        
//        if (!session.isSessionVariableSet("userid")) {
//            response.appendContent("<html><head><title>Hello!</title></head>"
//                    + "<body>Welcome on our awesome website! Please login <a href=\"/login\">here</a>!</body></html>");
//        } else {
//            response.appendContent("<html><head><title>Hello!</title></head>"
//                    + "<body>Welcome on our awesome website dear user! You are officially logged in! :) Logout <a href=\"/logout\">here!</a></body></html>");
//        }
    }

    @Override
    public String getTemplate() {
        return "example/login/index.tpl";
    }

    @Override
    public Object getTemplateRoot() {
        return indexData;
    }
}
