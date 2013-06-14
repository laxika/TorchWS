package torch.handler;

import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;

/**
 *
 * @author laxika
 */
public abstract class WebPage {

    public abstract void handle(TorchHttpRequest request, TorchHttpResponse response);
}
