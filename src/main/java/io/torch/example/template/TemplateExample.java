package io.torch.example.template;

import io.torch.controller.WebPage;
import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import io.torch.session.Session;
import io.torch.template.Templateable;

public class TemplateExample extends WebPage implements Templateable {

    public TemplateRoot templateRoot = new TemplateRoot();

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        templateRoot.getUser().setUsername("TestUser");
        templateRoot.getUser().setExtrainfo("You think that Laxika is the best guy ever!");
    }

    @Override
    public String getTemplate() {
        return "example/template/example.tpl";
    }

    @Override
    public Object getTemplateRoot() {
        return templateRoot;
    }
}
