package io.torch.example.validation;

import io.torch.controller.Validable;
import io.torch.controller.WebPage;
import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import io.torch.session.Session;

public class Validation extends WebPage implements Validable {

    @Override
    public boolean validate(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        return request.getRouteData().getVariable("test").getValue().equals("true");
    }

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("this is valid!");
    }
}
