package io.torch.example.template;

import io.torch.torchws.controller.WebPage;
import io.torch.torchws.http.request.TorchHttpRequest;
import io.torch.torchws.http.response.TorchHttpResponse;
import io.torch.torchws.session.Session;
import io.torch.torchws.template.Templateable;

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
