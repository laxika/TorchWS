package torch.example.sessions;

import torch.Server;

public class Main {

    public static void main(String[] args) throws Exception {
        //Creating a new server instance
        Server torch = new Server(8080);

        //Adding the check page to the route mapper
        torch.getRouteManager().defineRoute("/check", CheckSessionVar.class);
        torch.getRouteManager().defineRoute("/set", SetSessionVar.class);

        //Run the server
        torch.run();
    }
}
