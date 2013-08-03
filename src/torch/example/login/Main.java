package torch.example.login;

import torch.example.login.controller.IndexPage;
import torch.example.login.controller.LogoutPage;
import torch.example.login.controller.LoginPage;
import torch.Server;
import torch.http.request.RequestMethod;

public class Main {

    public static void main(String[] args) throws Exception {
        //Creating a new server instance
        Server torch = new Server();
        
        //Adding the webpages to the route manager
        torch.getRouteManager().defineRoute("/", IndexPage.class);
        torch.getRouteManager().defineRoute("/login", LoginPage.class, RequestMethod.GET);
        torch.getRouteManager().defineRoute("/login", LoginPage.class, RequestMethod.POST);
        torch.getRouteManager().defineRoute("/logout", LogoutPage.class);
        
        //Run the server
        torch.run();
    }
}
