package torch.router;

import torch.route.RouteManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import torch.example.routing.HelloWorldExactRoute;
import torch.example.routing.HelloWorldWithOneVar;
import torch.example.routing.HelloWorldWithTwoVar;
import torch.handler.WebPage;

public class RouteManagerTest {

    private RouteManager testTarget;

    @BeforeMethod
    public void setUpMethod() throws Exception {
        testTarget = new RouteManager();
    }
    
    @Test
    public void testGetRouteTargetWithDynamicVariables() {
        WebPage page = new HelloWorldWithTwoVar();
        
        testTarget.addRoute("/hello/@variable/@variable2/", page);
        
        assert testTarget.getRouteByUrl("/hello/var1/var2").getTarget().equals(page) : "Dynamic variable test 1";
        assert testTarget.getRouteByUrl("/hello/lol") == null : "Dynamic variable test 2";
        assert testTarget.getRouteByUrl("/var1/var2/var3") == null : "Dynamic variable test 3";
    }
    
    @Test
    public void testGetRouteTargetPriority() {
        WebPage page1 = new HelloWorldWithTwoVar();
        WebPage page2 = new HelloWorldWithOneVar();
        WebPage page3 = new HelloWorldExactRoute();
        
        testTarget.addRoute("/hello/@variable/@variable2/", page1);
        testTarget.addRoute("/hello/@variable/lol/", page2);
        testTarget.addRoute("/hello/exact/route", page3);
        
        assert testTarget.getRouteByUrl("/hello/exact/route").getTarget().equals(page3) : "Priority test 1";
        assert testTarget.getRouteByUrl("/hello/var1/var2/").getTarget().equals(page1) : "Priority test 2";
        assert testTarget.getRouteByUrl("/hello/var1/lol/").getTarget().equals(page2) : "Priority test 3";
    }
}