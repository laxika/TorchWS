package torch.example.helloworld;

import torch.Server;

public class Main {

    public static void main(String[] args) throws Exception {
        //Creating a new server instance
        Server torch = new Server(8080);
        
        //Adding the helloworld page to the route mapper
        torch.getRouteManager().defineRoute("/hello", new HelloWorld());
        
        //Run the server
        torch.run();
    }
}
