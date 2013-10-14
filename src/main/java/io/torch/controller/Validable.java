package io.torch.controller;

import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import io.torch.session.Session;

public interface Validable {

    public boolean validate(TorchHttpRequest request, TorchHttpResponse response, Session session);
}
