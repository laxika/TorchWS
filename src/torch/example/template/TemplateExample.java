package torch.example.template;

import torch.handler.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;

public class TemplateExample extends WebPage{

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        this.getTemplateStorage().setVariable("user", new UserData("TestUser", "You think that Laxika is the best guy ever!"));
    }
    
    @Override
    public String getTemplate() {
        return "example/TemplateExample.tpl";
    }
}
