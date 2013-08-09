package torch.example.helloworld;

import torch.controller.WebPage;
import torch.http.request.TorchHttpRequest;
import torch.http.response.TorchHttpResponse;
import torch.session.Session;

public class HelloWorld extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("hello world");
    }
}
