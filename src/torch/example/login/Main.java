package torch.example.login;

import torch.Server;
import torch.http.RequestMethod;

public class Main {

    public static void main(String[] args) throws Exception {
        //Creating a new server instance
        Server torch = new Server(8080);
        
        //Adding the webpages to the route manager
        torch.getRouteManager().defineRoute("/", new IndexPage());
        torch.getRouteManager().defineRoute("/login", new LoginPage(), RequestMethod.GET);
        torch.getRouteManager().defineRoute("/login", new LoginPage(), RequestMethod.POST);
        torch.getRouteManager().defineRoute("/logout", new LogoutPage());
        
        //Run the server
        torch.run();
    }
}
