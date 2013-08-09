package torch.example.template;

import torch.controller.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;
import torch.template.Templateable;

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
