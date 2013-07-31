package torch.example.template;

import torch.handler.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;

public class TemplateExample extends WebPage{
    
    public TemplateRoot templateRoot = new TemplateRoot();

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        templateRoot.getUser().setUsername("TestUser");
        templateRoot.getUser().setExtrainfo("You think that Laxika is the best guy ever!");
    }
    
    @Override
    public String getTemplate() {
        return "example/TemplateExample.tpl";
    }
    
    @Override
    public Object getTemplateRoot() {
        return templateRoot;
    }
}
