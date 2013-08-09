package io.torch.test.route;

import io.torch.route.RouteManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.torch.example.routing.HelloWorldExactRoute;
import io.torch.example.routing.HelloWorldWithOneVar;
import io.torch.example.routing.HelloWorldWithTwoVar;
import io.torch.http.request.RequestMethod;

public class RouteManagerTest {

    private RouteManager testTarget;

    @BeforeMethod
    public void setUpMethod() throws Exception {
        testTarget = new RouteManager();
    }
    
    @Test
    public void testNoVariableInRoute() {
        testTarget.defineRoute("/hello/exact/route/", HelloWorldExactRoute.class);
        
        assert testTarget.calculateRouteByUrl("/hello/exact/route/", RequestMethod.GET).getTarget().equals(HelloWorldExactRoute.class) : "Exact route test 1";
        assert testTarget.calculateRouteByUrl("/hello", RequestMethod.GET) == null : "Exact route test 2";
    }
    
    @Test
    public void testGetRouteTargetWithDynamicVariables() {
        testTarget.defineRoute("/hello/@variable/@variable2/", HelloWorldWithTwoVar.class);
        
        assert testTarget.calculateRouteByUrl("/hello/var1/var2", RequestMethod.GET).getTarget().equals(HelloWorldWithTwoVar.class) : "Dynamic variable test 1";
        assert testTarget.calculateRouteByUrl("/hello/lol", RequestMethod.GET) == null : "Dynamic variable test 2";
        assert testTarget.calculateRouteByUrl("/var1/var2/var3", RequestMethod.GET) == null : "Dynamic variable test 3";
    }
    
    @Test
    public void testGetRouteTargetPriority() {
        testTarget.defineRoute("/", HelloWorldWithTwoVar.class);
        testTarget.defineRoute("/hello/@variable/@variable2/", HelloWorldWithTwoVar.class);
        testTarget.defineRoute("/hello/@variable/lol/", HelloWorldWithOneVar.class);
        testTarget.defineRoute("/hello/exact/route", HelloWorldExactRoute.class);
        testTarget.defineRoute("/hello/some/post/route", HelloWorldExactRoute.class, RequestMethod.POST);
        
        assert testTarget.calculateRouteByUrl("/", RequestMethod.GET).getTarget().equals(HelloWorldWithTwoVar.class) : "Priority test 0";
        assert testTarget.calculateRouteByUrl("/hello/exact/route", RequestMethod.GET).getTarget().equals(HelloWorldExactRoute.class) : "Priority test 1";
        assert testTarget.calculateRouteByUrl("/hello/var1/var2/", RequestMethod.GET).getTarget().equals(HelloWorldWithTwoVar.class) : "Priority test 2";
        assert testTarget.calculateRouteByUrl("/hello/var1/lol/", RequestMethod.GET).getTarget().equals(HelloWorldWithOneVar.class) : "Priority test 3";
        assert testTarget.calculateRouteByUrl("/hello/some/post/route", RequestMethod.GET) == null : "Priority test 4";
        assert testTarget.calculateRouteByUrl("/hello/some/post/route", RequestMethod.POST).getTarget().equals(HelloWorldExactRoute.class) : "Priority test 5";
    }
    
    @Test
    public void testGetRouteStartPage() {
        testTarget.defineRoute("/", HelloWorldWithTwoVar.class);
        
        assert testTarget.calculateRouteByUrl("/", RequestMethod.GET).getTarget().equals(HelloWorldWithTwoVar.class) : "Priority test 1";
    }
}