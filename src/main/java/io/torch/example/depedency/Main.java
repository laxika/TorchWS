package io.torch.example.depedency;

import io.torch.Server;
import io.torch.example.helloworld.*;

public class Main {

    public static void main(String[] args) throws Exception {
        //Creating a new server instance
        Server torch = new Server();
        
        //Adding the helloworld page to the route mapper
        torch.getRouteManager().defineRoute("/hello", HelloWorld.class);
        
        //Run the server
        torch.run();
    }
}
