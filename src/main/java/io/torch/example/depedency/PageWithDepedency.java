package io.torch.example.depedency;

import io.torch.controller.WebPage;
import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import io.torch.session.Session;

public class PageWithDepedency extends WebPage {

    private final String str1;
    private final String str2;

    public PageWithDepedency(String str1, String str2) {
        this.str1 = str1;
        this.str2 = str2;
        System.out.println("Dep");
    }

    public PageWithDepedency(Object str1, Object str2) {
        this.str1 = "";
        this.str2 = "";
        System.out.println("Dep2");
    }

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("Hello! The first depedency string is '" + str1 + "' and the second is '" + str2 + "'");
    }
}
