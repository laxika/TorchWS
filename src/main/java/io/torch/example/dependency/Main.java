package io.torch.example.dependency;

import io.torch.torchws.Server;

public class Main {

    public static void main(String[] args) throws Exception {
        //Creating a new server instance
        Server torch = new Server();

        //Adding the helloworld page to the route mapper
        torch.getRouteManager().defineRoute("/", PageWithDependency.class, new Object[]{"This is a string dependency", "And another one"});

        //Run the server
        torch.run();
    }
}
