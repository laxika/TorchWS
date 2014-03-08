package io.torch.example.template;

import io.torch.controller.WebPage;
import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import io.torch.session.Session;
import io.torch.template.TemplateRoot;
import io.torch.template.Templateable;

@Templateable(path = "example/template/example.tpl")
public class TemplateExample extends WebPage {

    @TemplateRoot
    public ExampleTemplateRoot templateRoot = new ExampleTemplateRoot();

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        templateRoot.getUser().setUsername("TestUser");
        templateRoot.getUser().setExtrainfo("You think that Laxika is the best guy ever!");
    }

}
