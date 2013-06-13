package torch.example.helloworld;

import torch.handler.WebPageHandler;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;

public class Test extends WebPageHandler{

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response) {
        response.appendContent("hellow world");
    }
    
}
