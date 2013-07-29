package torch.example.login;

import torch.handler.WebPage;
import torch.http.RequestMethod;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;

public class LoginPage extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        if (request.getMethod() == RequestMethod.GET) {
            response.appendContent("<html><head><title>Log in here!</title></head>"
                    + "<body>"
                    + "Welcome on our awesome login page! <br><br>"
                    + "<form method=\"post\" action=\"/login\">"
                    + "    Username: <input name=\"username\"><br>"
                    + "    Password: <input name=\"password\"><br>"
                    + "    <input type=\"submit\" name=\"login\">"
                    + "</form>"
                    + "</body></html>");
        } else if (request.getMethod() == RequestMethod.POST) {
            if("admin".equals(request.getPostVariable("username")) && "admin".equals(request.getPostVariable("password"))) {
                session.setSessionVariable("userid", 1); //1th user is the admin, use real userid here
            }
        }
    }
}
