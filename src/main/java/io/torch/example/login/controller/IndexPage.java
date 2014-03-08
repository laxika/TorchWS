package io.torch.example.login.controller;

import io.torch.controller.WebPage;
import io.torch.example.login.data.IndexData;
import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import io.torch.session.Session;
import io.torch.template.Templateable;
import io.torch.template.TemplateRoot;

@Templateable(path = "example/login/index.tpl")
public class IndexPage extends WebPage {

    @TemplateRoot
    public IndexData indexData;

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        indexData = new IndexData(session.isSessionVariableSet("userid"));

        if (session.isSessionVariableSet("userid")) {
            indexData.setUsername("admin"); //query the real username from the db here
        }
    }
}
