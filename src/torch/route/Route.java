package torch.route;

import java.util.ArrayList;
import torch.handler.WebPage;

public class Route {

    private final String routingUri;
    private final int hopCount;
    private final WebPage target;
    private final String[] routingHops;
    private final int dynamicVariableCount;
    private final ArrayList<Integer> dynamicVariablePositions = new ArrayList<>();

    public Route(String routingUri, int hopCount, WebPage target) {
        this.routingUri = routingUri;
        this.hopCount = hopCount;
        this.target = target;
        this.routingHops = routingUri.split("/");

        for (int i = 0; i < routingHops.length; i++) {
            if (routingHops[i].startsWith("@")) {
                dynamicVariablePositions.add(i);
            }
        }

        this.dynamicVariableCount = dynamicVariablePositions.size();
    }

    public int getHopCount() {
        return hopCount;
    }

    public WebPage getTarget() {
        return target;
    }

    public String getRoutingUri() {
        return routingUri;
    }

    public int getDynamicVariableCount() {
        return dynamicVariableCount;
    }

    public ArrayList<Integer> getDynamicVariablePositions() {
        return dynamicVariablePositions;
    }
}