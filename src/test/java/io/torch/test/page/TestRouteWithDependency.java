package io.torch.test.page;

import io.torch.torchws.controller.WebPage;
import io.torch.torchws.http.request.TorchHttpRequest;
import io.torch.torchws.http.response.TorchHttpResponse;
import io.torch.torchws.session.Session;

public class TestRouteWithDependency extends WebPage {
    
    private final String str1;
    private final String str2;
    
    public TestRouteWithDependency(String str1, String str2) {
        this.str1 = str1;
        this.str2 = str2;
    }
    
    public String getDependencyOne() {
        return str1;
    }
    
    public String getDependencyTwo() {
        return str2;
    }

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
    }
    
}
