package io.torch.example.sessions;

import io.torch.Server;

public class Main {

    public static void main(String[] args) throws Exception {
        //Creating a new server instance
        Server torch = new Server();

        //Adding the check page to the route mapper
        torch.getRouteManager().defineRoute("/check", CheckSessionVar.class);
        torch.getRouteManager().defineRoute("/set", SetSessionVar.class);

        //Run the server
        torch.run();
    }
}
