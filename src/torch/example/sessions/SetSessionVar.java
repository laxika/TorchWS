package torch.example.sessions;

import torch.controller.WebPage;
import torch.http.request.TorchHttpRequest;
import torch.http.response.TorchHttpResponse;
import torch.session.Session;

public class SetSessionVar extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        session.setSessionVariable("testvar", "XYZ smthing");
    }
    
}
