package io.torch.example.dependency;

import io.torch.controller.WebPage;
import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import io.torch.session.Session;

public class PageWithDependency extends WebPage {

    private final String str1;
    private final String str2;

    public PageWithDependency(String str1, String str2) {
        this.str1 = str1;
        this.str2 = str2;
    }

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("Hello! The first dependency string is '" + str1 + "' and the second is '" + str2 + "'");
    }
}
