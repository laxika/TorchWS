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

Now your server is ready to run, so feel free to run it and then call http://127.0.0.1:8080/hello to see the results.

## More about the routing

The routing in Torch is pretty complex. You can define routes as you saw in the previous section but not only that, you can also add variables to your routes. For example:

``` Java
torch.getRouteManager().defineRoute("/hello/@variable1/@variable2/", HelloWorldWithTwoVar.class);
```

The variables in the uris starts with `@` so you declare two variables in this method. The first variable is called `@variable1` and the second one is `@variable2`.

You can also mix non variables and variables in any order in a given route, for example:

``` Java
torch.getRouteManager().defineRoute("/hello/@variable1/lol/", HelloWorldWithOneVar.class);
```

Okey, so you can declare variables in the routes, but how can you access them? Accessing them is really easy:

``` Java
request.getRouteData().getValue("variable1");
```

This will return you a `RouteVariable` object. You can call `getName()` and `getValue()` on it to get it's name/value.

You can use routing variables to create SEO links and so on. Imagine that you plan to do an item database for roleplaying games. You can define a route like this: `/@gamename/@itemtype/@itemname` and match them to something like `/supermmorpg/weapon/blade-of-the-programmers`.

The routing order is easy too:

``` Java
torch.getRouteManager().defineRoute("/hello/@variable1/@variable2/", HelloWorldWithTwoVar.class);
torch.getRouteManager().defineRoute("/hello/@variable1/lol/", HelloWorldWithOneVar.class);
torch.getRouteManager().defineRoute("/hello/exact/route", HelloWorldExactRoute.class);
```

The `/hello/smthing/lol` will route to the `HelloWorldWithOneVar` class while the `/hello/thisis/stupid` will route to the `HelloWorldWithTwoVar` class.

You can also iterate over the routing variables this way:

``` Java
for (RouteVariable routeVar : request.getRouteData()) {
    response.appendContent("Route var: <b>" + routeVar.getName() + "</b> = '" + routeVar.getValue()+"'<br>");
}
```

You can also route GET/POST/PUT/DELETE request to different classes (the defines without `RequestMethod` will handle the GET requests):

``` Java
torch.getRouteManager().defineRoute("/hello/exact/route", HelloWorldExactRoute.class, RequestMethod.POST);
```

The routing engine will route all POST requests for the `/hello/exact/route` uri to the `HelloWorldExactRoute` class, while the GET requests for the same uri will return a 404 Not Found error.

## Handling POST requests

Handling POST request is easy in Torch. You can get POST variables by calling `request.getPostData().getVariable("postvarname")`. This will return you a `PostVariable` object. You can call `getName()` and `getValue()` on it to get it's name/value.

You can also iterate over the post variables just like you did with the route variables:

``` Java
for (PostVariable postVar : request.getPostData()) {
    response.appendContent("Post var: <b>" + postVar.getName() + "</b> = '" + postVar.getValue()+"'<br>");
}
```

## Handling Cookies

Clients can send cookies to the server with every request. We need to handle them. You can get a cookie's value on the same way as a post/route variable's value.

``` Java
request.getCookieData().getCookie("cookiename");
```

This will return you a `CookieVariable` object. You can call `getName()` and `getValue()` on it to get it's name/value.

You can also iterate over the cookies just like you did with the route/post variables:

``` Java
for (CookieVariable cookieVar : request.getCookieData()) {
    response.appendContent("Cookie var: <b>" + cookieVar.getName() + "</b> = '" + cookieVar.getValue()+"'<br>");
}
```

You can create a new cookie and send it with the response this way:

``` Java
response.getCookieData().addCookie(new CookieVariable("cookiename","cookievalue"));
```

Timing cookies is not supported yet, but it's a planned feature for the final release.

The `SESSID` cookie name is reserved for the session handling, please don't use it.

## Session management

The Torch server start a new session automatically at the client's first connectation. You don't need to do anything to start sessions, it's all done automatically. At now there is no way to set sessions to expire at a given time and sessions stored in the memory so if you restart the server then all session data is suddenly lost. We are planning session expire time and session id saving to db/file for the final release.

You can set a session variable's value this way:

``` Java
session.setSessionVariable("userid", 1);
```

Get a session variable:

``` Java
session.getSessionVariable("userid");
```

Check if a session variable is set:

``` Java
session.isSessionVariableSet("userid");
```

And destroying all session variables (it's used mostly to log out/clear the user):

``` Java
session.clearSessionVariables();
```

## Manipulating the response headers

You can also manipulate the response headers this way:

``` Java
response.getHeaderData().setHeader("headername", "headervalue");
```

For redirecting you can use `response.redirect("uri");` and for changing the content type use `response.setContentType("application/text");` and for the status use `response.setStatus(HttpResponseStatus.FORBIDDEN);`.

## Templating engine

Torch has a built in templating engine. You can use the response.appendContent() to manipulate the response content or use templates. (You can't do the both at the same time.) 

To make a `WebPage` templateable implement tha `Templateable` interface and override the `getTemplate()` and `getTemplateRoot()` methods.

The `getTemplate()` method must return the template's path relative to the `template` folder while the `getTemplateRoot()` method must return a java bean what have getters for the variables used in the templates. For more info and templating tutorials check this link: http://freemarker.org/docs/dgui.html

## Static files

Torch can server static files too but if you plan to do a bigger site we suggest you to use a CDN for this task instead. Put your static files to the `public` folder and they will be served if no matching route found for the given uri.



