package torch.route;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import torch.example.routing.HelloWorldExactRoute;
import torch.example.routing.HelloWorldWithOneVar;
import torch.example.routing.HelloWorldWithTwoVar;
import torch.handler.WebPage;
import torch.http.RequestMethod;

public class RouteManagerTest {

    private RouteManager testTarget;

    @BeforeMethod
    public void setUpMethod() throws Exception {
        testTarget = new RouteManager();
    }
    
    @Test
    public void testGetRouteTargetWithDynamicVariables() {
        WebPage page = new HelloWorldWithTwoVar();
        
        testTarget.defineRoute("/hello/@variable/@variable2/", page);
        
        assert testTarget.calculateRouteByUrl("/hello/var1/var2", RequestMethod.GET).getTarget().equals(page) : "Dynamic variable test 1";
        assert testTarget.calculateRouteByUrl("/hello/lol", RequestMethod.GET) == null : "Dynamic variable test 2";
        assert testTarget.calculateRouteByUrl("/var1/var2/var3", RequestMethod.GET) == null : "Dynamic variable test 3";
    }
    
    @Test
    public void testGetRouteTargetPriority() {
        WebPage page1 = new HelloWorldWithTwoVar();
        WebPage page2 = new HelloWorldWithOneVar();
        WebPage page3 = new HelloWorldExactRoute();
        
        testTarget.defineRoute("/", page1);
        testTarget.defineRoute("/hello/@variable/@variable2/", page1);
        testTarget.defineRoute("/hello/@variable/lol/", page2);
        testTarget.defineRoute("/hello/exact/route", page3);
        testTarget.defineRoute("/hello/some/post/route", page3, RequestMethod.POST);
        
        assert testTarget.calculateRouteByUrl("/", RequestMethod.GET).getTarget().equals(page1) : "Priority test 0";
        assert testTarget.calculateRouteByUrl("/hello/exact/route", RequestMethod.GET).getTarget().equals(page3) : "Priority test 1";
        assert testTarget.calculateRouteByUrl("/hello/var1/var2/", RequestMethod.GET).getTarget().equals(page1) : "Priority test 2";
        assert testTarget.calculateRouteByUrl("/hello/var1/lol/", RequestMethod.GET).getTarget().equals(page2) : "Priority test 3";
        assert testTarget.calculateRouteByUrl("/hello/some/post/route", RequestMethod.GET) == null : "Priority test 4";
        assert testTarget.calculateRouteByUrl("/hello/some/post/route", RequestMethod.POST).getTarget().equals(page3) : "Priority test 5";
    }
    
    @Test
    public void testGetRouteStartPage() {
        WebPage page = new HelloWorldWithTwoVar();
        
        testTarget.defineRoute("/", page);
        
        assert testTarget.calculateRouteByUrl("/", RequestMethod.GET).getTarget().equals(page) : "Priority test 1";
    }
}