package torch.example.routing;

import torch.Server;

public class Main {

    public static void main(String[] args) throws Exception {
        //Creating a new server instance
        Server torch = new Server(8080);
        
        //Adding the webpages to the route manager
        torch.getRouteManager().defineRoute("/hello/@variable1/@variable2/", HelloWorldWithTwoVar.class);
        torch.getRouteManager().defineRoute("/hello/@variable1/lol/", HelloWorldWithOneVar.class);
        torch.getRouteManager().defineRoute("/hello/exact/route", HelloWorldExactRoute.class);
        
        //Run the server
        torch.run();
    }
}
