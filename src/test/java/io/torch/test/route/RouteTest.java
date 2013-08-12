package io.torch.test.route;

import io.torch.route.Route;
import io.torch.route.RouteVariable;
import java.util.HashMap;
import org.testng.annotations.Test;

public class RouteTest {

    @Test
    public void testRouteVariableValuesTest() {
        Route route = new Route("/url/@var1/@var2", null, null, null);

        HashMap<String, RouteVariable> map = route.calculateVariablesValuesFromUrl("/url/something/test");

        assert map.get("var1").getValue().equals("something") : "Variable values test 1";
        assert map.get("var2").getValue().equals("test") : "Variable values test 2";
        assert map.get("var3") == null : "Variable values test 3";
    }
}