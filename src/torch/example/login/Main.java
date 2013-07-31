package torch.example.login;

import torch.Server;
import torch.http.RequestMethod;

public class Main {

    public static void main(String[] args) throws Exception {
        //Creating a new server instance
        Server torch = new Server(8080);
        
        //Adding the webpages to the route manager
        torch.getRouteManager().defineRoute("/", IndexPage.class);
        torch.getRouteManager().defineRoute("/login", LoginPage.class, RequestMethod.GET);
        torch.getRouteManager().defineRoute("/login", LoginPage.class, RequestMethod.POST);
        torch.getRouteManager().defineRoute("/logout", LogoutPage.class);
        
        //Run the server
        torch.run();
    }
}
