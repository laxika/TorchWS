package torch.example.login;

import torch.handler.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;

public class IndexPage extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        if (!session.isSessionVariableSet("userid")) {
            response.appendContent("<html><head><title>Hello!</title></head>"
                    + "<body>Welcome on our awesome website! Please login <a href=\"/login\">here</a>!</body></html>");
        } else {
            response.appendContent("<html><head><title>Hello!</title></head>"
                    + "<body>Welcome on our awesome website dear user! You are officially logged in! :) Logout <a href=\"/logout\">here!</a></body></html>");
        }
    }
}
