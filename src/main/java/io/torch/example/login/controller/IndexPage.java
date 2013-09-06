package io.torch.example.login.controller;

import io.torch.example.login.data.IndexData;
import io.torch.torchws.controller.WebPage;
import io.torch.torchws.http.request.TorchHttpRequest;
import io.torch.torchws.http.response.TorchHttpResponse;
import io.torch.torchws.session.Session;
import io.torch.torchws.template.Templateable;

public class IndexPage extends WebPage implements Templateable {
    
    private IndexData indexData;

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        indexData = new IndexData(session.isSessionVariableSet("userid"));
        
        if(session.isSessionVariableSet("userid")) {
            indexData.setUsername("admin"); //query the real username from the db here
        }
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
