#Torch WS

**TorchWS is a powerful yet easy to use and lightweight java web framework.**

*For detiled examples, check the src/example package. The framework is in alpha state at the moment, so API breaks can happen without any further notice.*

## Getting started

This tutorial show you how to create the simplest TorchWS program, the 'Hello World' application.

The base of TorchWS is it's routing system. First create a new Server instance.

``` Java
Server torch = new Server();
```

This will initialize the server on 8080 port by default. You have the server initialized, it's time to add some routes.

``` Java
torch.getRouteManager().defineRoute("/hello", HelloWorld.class);
```

Congratulations you have defined your first route. This line will make the server route the request for /hello to the HelloWorld class. So let's create the controller class too.

``` Java
import torch.handler.WebPage;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;

public class HelloWorld extends WebPage {

    @Override
    public void handle(TorchHttpRequest request, TorchHttpResponse response, Session session) {
        response.appendContent("hello world");
    }
}
```

In Torch, every controller extends the `WebPage` class and override the default `handle` method. For now we only add some plain text to the response by the `response.appendContent` method. The handle method has tree variables, the 'request' store everything what we get from the client when it's request a new webpage, the response is what we'll send to the client, and the session is the actual session. You don't need to manually start a session like in PHP, it's started automatically at the client's first connectation. For more info on the sessions please visit this link:

http://en.wikipedia.org/wiki/Session_(computer_science)#Web_server_session_management

Now our server is ready to run, so feel free to run it and then call http://127.0.0.1:8080/hello to see the results.

## More about the routing

