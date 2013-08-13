package io.torch.test.route;

import io.torch.example.routing.HelloWorldExactRoute;
import io.torch.example.routing.HelloWorldWithOneVar;
import io.torch.example.routing.HelloWorldWithTwoVar;
import io.torch.http.request.RequestMethod;
import io.torch.route.RouteManager;
import io.torch.route.RouteTarget;
import io.torch.test.page.TestRouteWithDependency;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RouteManagerTest {

    private RouteManager testTarget;

    @BeforeMethod
    public void setUpMethod() throws Exception {
        testTarget = new RouteManager();
    }

    @Test
    public void testNoVariableInRoute() throws Exception {
        testTarget.defineRoute("/hello/exact/route/", HelloWorldExactRoute.class);

        assert testTarget.calculateRouteByUrl("/hello/exact/route/", RequestMethod.GET).getTarget().newInstance() instanceof HelloWorldExactRoute : "Exact route test 1";
        assert testTarget.calculateRouteByUrl("/hello", RequestMethod.GET) == null : "Exact route test 2";
    }

    @Test
    public void testGetRouteTargetWithDynamicVariables() throws Exception {
        testTarget.defineRoute("/hello/@variable/@variable2/", HelloWorldWithTwoVar.class);

        assert testTarget.calculateRouteByUrl("/hello/var1/var2", RequestMethod.GET).getTarget().newInstance() instanceof HelloWorldWithTwoVar : "Dynamic variable test 1";
        assert testTarget.calculateRouteByUrl("/hello/lol", RequestMethod.GET) == null : "Dynamic variable test 2";
        assert testTarget.calculateRouteByUrl("/var1/var2/var3", RequestMethod.GET) == null : "Dynamic variable test 3";
    }

    @Test
    public void testGetRouteTargetPriority() throws Exception {
        testTarget.defineRoute("/", HelloWorldWithTwoVar.class);
        testTarget.defineRoute("/hello/@variable/@variable2/", HelloWorldWithTwoVar.class);
        testTarget.defineRoute("/hello/@variable/lol/", HelloWorldWithOneVar.class);
        testTarget.defineRoute("/hello/exact/route", HelloWorldExactRoute.class);
        testTarget.defineRoute("/hello/some/post/route", HelloWorldExactRoute.class, RouteTarget.NO_DEPENDENCY, RequestMethod.POST);

        assert testTarget.calculateRouteByUrl("/", RequestMethod.GET).getTarget().newInstance() instanceof HelloWorldWithTwoVar : "Priority test 0";
        assert testTarget.calculateRouteByUrl("/hello/exact/route", RequestMethod.GET).getTarget().newInstance() instanceof HelloWorldExactRoute : "Priority test 1";
        assert testTarget.calculateRouteByUrl("/hello/var1/var2/", RequestMethod.GET).getTarget().newInstance() instanceof HelloWorldWithTwoVar : "Priority test 2";
        assert testTarget.calculateRouteByUrl("/hello/var1/lol/", RequestMethod.GET).getTarget().newInstance() instanceof HelloWorldWithOneVar : "Priority test 3";
        assert testTarget.calculateRouteByUrl("/hello/some/post/route", RequestMethod.GET) == null : "Priority test 4";
        assert testTarget.calculateRouteByUrl("/hello/some/post/route", RequestMethod.POST).getTarget().newInstance() instanceof HelloWorldExactRoute : "Priority test 5";
    }

    @Test
    public void testGetRouteStartPage() throws Exception {
        testTarget.defineRoute("/", HelloWorldWithTwoVar.class);

        assert testTarget.calculateRouteByUrl("/", RequestMethod.GET).getTarget().newInstance() instanceof HelloWorldWithTwoVar : "Priority test 1";
    }

    @Test
    public void testRouteDependency() throws Exception {
        try {
            testTarget.defineRoute("/", TestRouteWithDependency.class);
            Assert.fail("NoSuchConstructorException expected");
        } catch (Exception expected) {
        }
        try {
            testTarget.defineRoute("/", TestRouteWithDependency.class, null);
            Assert.fail("IllegalStateException expected");
        } catch (Exception expected) {
        }
        
        testTarget.defineRoute("/", TestRouteWithDependency.class, new Object[] {"Dependency One", "Dependency Two"});
        
        assert ((TestRouteWithDependency)testTarget.calculateRouteByUrl("/", RequestMethod.GET).getTarget().newInstance()).getDependencyOne().equals("Dependency One") : "Dependency test 1";
        assert ((TestRouteWithDependency)testTarget.calculateRouteByUrl("/", RequestMethod.GET).getTarget().newInstance()).getDependencyTwo().equals("Dependency Two") : "Dependency test 2";
    }
}