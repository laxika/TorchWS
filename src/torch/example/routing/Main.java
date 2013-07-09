package torch.example.routing;

import torch.Server;

public class Main {

    public static void main(String[] args) throws Exception {
        //Creating a new server instance
        Server torch = new Server(8080);
        
        //Adding the helloworld page to the route mapper
        torch.getRouteManager().addRoute("/hello/@variable/@variable2/", new HelloWorldWithTwoVar());
        torch.getRouteManager().addRoute("/hello/@variable/lol/", new HelloWorldWithOneVar());
        torch.getRouteManager().addRoute("/hello/exact/route", new HelloWorldExactRoute());
        
        //Run the server
        torch.run();
    }
}
