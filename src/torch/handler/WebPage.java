package torch.handler;

import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.session.Session;
import torch.template.ReadWriteTemplateStorage;

public abstract class WebPage {

    private ReadWriteTemplateStorage templateStorage = new ReadWriteTemplateStorage();

    public abstract void handle(TorchHttpRequest request, TorchHttpResponse response, Session session);

    public ReadWriteTemplateStorage getTemplateStorage() {
        return templateStorage;
    }

    public String getTemplate() {
        return null;
    }
}
