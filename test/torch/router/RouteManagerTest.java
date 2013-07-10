package torch.router;

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
        
        assert testTarget.getRouteTarget("/hello/var1/var2").equals(page) : "Dynamic variable test 1";
        assert testTarget.getRouteTarget("/hello/lol") == null : "Dynamic variable test 2";
        assert testTarget.getRouteTarget("/var1/var2/var3") == null : "Dynamic variable test 3";
    }
    
    @Test
    public void testGetRouteTargetPriority() {
        WebPage page1 = new HelloWorldWithTwoVar();
        WebPage page2 = new HelloWorldWithOneVar();
        WebPage page3 = new HelloWorldExactRoute();
        
        testTarget.addRoute("/hello/@variable/@variable2/", page1);
        testTarget.addRoute("/hello/@variable/lol/", page2);
        testTarget.addRoute("/hello/exact/route", page3);
        
        assert testTarget.getRouteTarget("/hello/exact/route").equals(page3) : "Priority test 1";
        assert testTarget.getRouteTarget("/hello/var1/var2/").equals(page1) : "Priority test 2";
        assert testTarget.getRouteTarget("/hello/var1/lol/").equals(page2) : "Priority test 3";
    }
}