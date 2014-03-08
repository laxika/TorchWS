package io.torch.example.login;

import io.torch.Server;
import io.torch.example.login.controller.IndexPage;
import io.torch.example.login.controller.LoginPage;
import io.torch.example.login.controller.LogoutPage;
import io.torch.http.request.RequestMethod;
import io.torch.route.DefaultRouteTarget;

public class Main {

    public static void main(String[] args) throws Exception {
        //Creating a new server instance
        Server torch = new Server();

        //Adding the webpages to the route manager
        torch.getRouteManager().defineRoute("/", IndexPage.class);
        torch.getRouteManager().defineRoute("/login", LoginPage.class, DefaultRouteTarget.NO_DEPENDENCY, RequestMethod.GET);
        torch.getRouteManager().defineRoute("/login", LoginPage.class, DefaultRouteTarget.NO_DEPENDENCY, RequestMethod.POST);
        torch.getRouteManager().defineRoute("/logout", LogoutPage.class);

        //Run the server
        torch.run();
    }
}
