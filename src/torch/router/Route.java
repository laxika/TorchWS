package torch.router;

import torch.handler.WebPage;

public class Route {

    private final String routingUri;
    private final int hopCount;
    private final WebPage target;

    public Route(String routingUri, int hopCount, WebPage target) {
        this.routingUri = routingUri;
        this.hopCount = hopCount;
        this.target = target;
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
}
