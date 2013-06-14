package torch.example.helloworld;

import torch.handler.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;

public class Test extends WebPage{

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response) {
        response.appendContent("hellow world");
    }
    
}
