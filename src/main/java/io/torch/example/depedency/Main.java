package io.torch.example.depedency;

import io.torch.Server;

public class Main {

    public static void main(String[] args) throws Exception {
        //Creating a new server instance
        Server torch = new Server();

        //Adding the helloworld page to the route mapper
        torch.getRouteManager().defineRoute("/", PageWithDepedency.class, new Object[]{"This is a string depedency", "And another one"});

        //Run the server
        torch.run();
    }
}
