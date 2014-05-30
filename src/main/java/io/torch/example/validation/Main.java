package io.torch.example.validation;

import io.torch.Server;

public class Main {

    public static void main(String[] args) throws Exception {
        //Creating a new server instance
        Server torch = new Server();

        //Adding the helloworld page to the route mapper
        torch.getRouteManager().defineRoute("/validation/@test", Validation.class);

        //Run the server
        torch.run();
    }
}
